package XMLParsing;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.HashSet; // Import the HashSet class
import java.util.HashMap;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.util.*;

import org.xml.sax.helpers.DefaultHandler;

public class MainSaxParser extends DefaultHandler {

    List<Movies> myMovies;

    private String tempVal;

    ArrayList<String> genres = new ArrayList<String>();
    HashSet<String> newGenres = new HashSet<String>();

    // order is <genrename, genreid>
    HashMap<String, String> GenresMap = new HashMap<String, String>();

    //order is genre id then movieList
    HashMap<String, ArrayList<String>> GenreMovieMap = new HashMap<String, ArrayList<String>>();

    //to maintain context
    private Movies tempMov;

    public MainSaxParser() {
        myMovies = new ArrayList<Movies>();
    }


    public void runParse() throws IOException, NamingException, SQLException {
        try {
            File inconFile = new File("src/XMLParsing/movieInconsistency.txt");
            if (inconFile.createNewFile()) {
                //System.out.println("File created: " + inocnFile.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        parseDocument();
        printData();
        getGenres();
        addGenres();
        importTable() ;
//        System.out.println(genres);
        //System.out.println(GenresMap);
        //System.out.println(GenreMovieMap);
    }

    public ArrayList<String> getGenres() throws NamingException, SQLException {
        String url = "jdbc:mysql://localhost:3306/moviedb"; // url for the database
        String user = "mytestuser"; // replace with your MySQL username
        String password = "My6$Password"; // replace with your MySQL password

        try {
            // connect to the database using the DriverManager
            Connection conn = DriverManager.getConnection(url, user, password);

            // do whatever you need to do with the database here
            String query = "SELECT name FROM moviedb.genres;";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()){
                this.genres.add(result.getString("name"));
            };
            conn.close();
            return this.genres;
            // close the connection when finished
        } catch (SQLException e) {
            // handle any errors that may occur
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
            return this.genres;
        }
    }

    public HashMap<String, String> addGenres() throws NamingException, SQLException {
        String url = "jdbc:mysql://localhost:3306/moviedb"; // url for the database
        String user = "mytestuser"; // replace with your MySQL username
        String password = "My6$Password"; // replace with your MySQL password

        try {
            // connect to the database using the DriverManager
            Connection conn = DriverManager.getConnection(url, user, password);

            // do whatever you need to do with the database here
            for(String genre: newGenres) {
                String query = String.format("INSERT INTO genres VALUES (Null, \"%s\");", genre);
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);

            }
            String genreQuery = ("Select * from  genres;");
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(genreQuery);

            while(result.next()){
                GenresMap.put(result.getString("name"), result.getString("id"));
            }
            conn.close();
            return GenresMap;
            // close the connection when finished
        } catch (SQLException e) {
            // handle any errors that may occur
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
            return GenresMap;
        }
    }

    public int getLargestID() throws NamingException, SQLException {
        String url = "jdbc:mysql://localhost:3306/moviedb"; // url for the database
        String user = "mytestuser"; // replace with your MySQL username
        String password = "My6$Password"; // replace with your MySQL password

        try {
            // connect to the database using the DriverManager
            Connection conn = DriverManager.getConnection(url, user, password);

            // do whatever you need to do with the database here
            String query = "SELECT id FROM moviedb.movies\n" +
                    "Order by id DESC\n" +
                    "Limit 1;";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.next();
            String largestID = result.getString("id");
            largestID = largestID.substring(2);
            conn.close();

            return Integer.parseInt(largestID);
            // close the connection when finished
        } catch (SQLException e) {
            // handle any errors that may occur
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private void importTable() throws IOException, NamingException, SQLException{
        PrintWriter fileWriter = new PrintWriter("src/XMLParsing/addMovies.sql", "UTF-8");
        int largestID = getLargestID();
        fileWriter.printf("BEGIN; -- START TRANSACTION\n");
        for(Movies movie: myMovies){
            largestID++;
            String newMovieID = String.format("tt%s", largestID);
            String[] movieGen = movie.getGenres();
            if(movieGen!= null){
                for(String genre: movieGen){
                    String genreID = GenresMap.get(genre);
                    // add to genre id of movie lists ids
                    if (GenreMovieMap.containsKey(genreID)) {
                        // If it does, add the movie ID to the existing list of movies for that genre
                        ArrayList<String> movies = GenreMovieMap.get(genreID);
                        movies.add(newMovieID);
                        GenreMovieMap.put(genreID, movies);
                    } else {
                        // If it doesn't, create a new list and add the movie ID to it
                        ArrayList<String> movies = new ArrayList<>();
                        movies.add(newMovieID);
                        GenreMovieMap.put(genreID, movies);
                    }
                }
            }
            int createdYear = movie.getYear();


            fileWriter.printf("INSERT INTO moviedb.movies VALUES(" );
            fileWriter.printf("\'%s\', ", newMovieID);
            fileWriter.printf("\"%s\", ", movie.getTitle());
            if(createdYear == 0){
                fileWriter.printf("%s, ", null);
            }
            else{
                fileWriter.printf("%s, ", createdYear);
            }

            fileWriter.printf("\"%s\");\n", movie.getDirector());
        }
        for (String key : GenreMovieMap.keySet()) {
            ArrayList<String> movieIDs = GenreMovieMap.get(key);
            for (String id : movieIDs) {
                fileWriter.printf("INSERT INTO moviedb.genres_in_movies VALUES(" );
                fileWriter.printf("%s, ", key);
                //System.out.println(id);
                fileWriter.printf("\"%s\");\n", id);
            }


        }
        fileWriter.printf("COMMIT; -- Terminate the ONE transaction");
        fileWriter.close();

    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/XMLParsing/stanford-movies.tar/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }


/**
     * Iterate through the list and print
     * the contents
     */

    private void printData() {

        System.out.println("No of movies parsed through '" + myMovies.size() + "'.");

//        Iterator<Movies> it = myMovies.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMov = new Movies();
            //System.out.println("gay");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        //System.out.println(tempVal);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            myMovies.add(tempMov);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMov.setTitle(tempVal);
            //System.out.println(tempMov.getTitle());
        } else if (qName.equalsIgnoreCase("dirn")) {
            tempMov.setDirector(tempVal);

        } else if (qName.equalsIgnoreCase("year")) {
            try {
                int year = Integer.parseInt(tempVal);
                tempMov.setYear(year);
            }
            catch(NumberFormatException nfe){
                tempMov.setYear(0000);
                // inconstincy here
                try {
                    FileWriter inconWriter = new FileWriter("src/XMLParsing/movieInconsistency.txt", true);
                    BufferedWriter bw = new BufferedWriter(inconWriter);
                    PrintWriter out = new PrintWriter(bw);

                    out.println(tempMov.getTitle() + " inconsistency with created year. will not be included");
                    out.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                //System.out.println("movie not included because year was not in correct format");
            }

    } else if (qName.equalsIgnoreCase("cats")   ) {
        String[] newGen = tempVal.split("\\s+");
        tempMov.setGenres(newGen);
        for(String newG: newGen){
            if(!(genres.contains(newG))){
                newGenres.add(newG);
            }
        }

    }
    }

    public static void main(String[] args) throws IOException, NamingException, SQLException{
         MainSaxParser mainParser = new MainSaxParser();
        mainParser.runParse();
    }

}
