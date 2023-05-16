package XMLParsing;


import java.io.*;
import java.sql.*;
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

import org.xml.sax.helpers.DefaultHandler;

public class CastSaxParser extends DefaultHandler {

    List<Cast> myCasts;

    private String tempVal;

    //to maintain context
    private Cast tempCast;

    // order is <movieName, movieId>
    HashMap<String, String> movieMap = new HashMap<String, String>();

    //order is <star name, starid>
    HashMap<String, String> starMap = new HashMap<String,String>();

    HashMap<String,ArrayList<String>> StarMovie = new HashMap<String,ArrayList<String>>();


    public CastSaxParser() {
        myCasts = new ArrayList<Cast>();
    }

    public void runParse() throws IOException, NamingException, SQLException{
        try {
            File inconFile = new File("src/XMLParsing/castInconsistency.txt");
            if (inconFile.createNewFile()) {
                //System.out.println("File created: " + inocnFile.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        parseDocument();
        printData();
        getBothIds();
        importTable();
        System.out.println(StarMovie);
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/XMLParsing/casts124.xml", this);

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


//        Iterator<Cast> it = myCasts.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
        System.out.println("No of cast members '" + myCasts.size() + "'.");

    }

    private void importTable() throws IOException, NamingException, SQLException{
        PrintWriter fileWriter = new PrintWriter("src/XMLParsing/addStarsinMOvies.sql", "UTF-8");
        fileWriter.printf("BEGIN; -- START TRANSACTION\n");
        for (String movieIDs : StarMovie.keySet()) {
            ArrayList<String> cast = StarMovie.get(movieIDs);
            for (String castID: cast){
                if(castID!= null && movieIDs != null) {
                    fileWriter.printf("INSERT INTO moviedb.stars_in_movies VALUES(");
                    fileWriter.printf("\'%s\', ", castID);
                    fileWriter.printf("\'%s\');\n ", movieIDs);
                }
            }
        }
        fileWriter.printf("COMMIT; -- Terminate the ONE transaction");
        fileWriter.close();

    }

    public void getBothIds() throws NamingException, SQLException {
        String url = "jdbc:mysql://localhost:3306/moviedb"; // url for the database
        String user = "mytestuser"; // replace with your MySQL username
        String password = "My6$TestUser"; // replace with your MySQL password

        try {
            // connect to the database using the DriverManager
            Connection conn = DriverManager.getConnection(url, user, password);

            // do whatever you need to do with the database here
            for(Cast cast: myCasts){
                String movieName = cast.getMovie();
                String query = String.format("SELECT id FROM moviedb.movies \n"
                        +"Where title = \"%s\"; ", movieName);
                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(query);
                result.next();
                //System.out.println(movieName);
                try {
                    String movieID = result.getString("id");
                    //System.out.println(movieName + "    " + movieID);
                    if (movieID != null) {
                        movieMap.put(movieName, movieID);
                    }
                }
                catch (SQLException s){
                    //inconstincency: movie does not exist in db
                    try {
                        FileWriter inconWriter = new FileWriter("src/XMLParsing/castInconsistency.txt", true);
                        BufferedWriter bw = new BufferedWriter(inconWriter);
                        PrintWriter out = new PrintWriter(bw);

                        out.println(movieName+ " movie inconsistency does not exist in db. will not be included");
                        out.close();
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    //System.out.println(movieName + " does not exist in db");
                }
                for(String castName: cast.getCast()){
                    if(!starMap.containsKey(castName)){
                        //System.out.println(castName);
                        String starQuery = String.format("SELECT id FROM moviedb.stars \n"
                                +"Where name = \"%s\"; ", castName);
                        Statement starStatement = conn.createStatement();
                        ResultSet starResult = starStatement.executeQuery(starQuery);
                        starResult.next();

                        try {
                            String starID = starResult.getString("id");
                            //System.out.println(starID + "testing");
                            //System.out.println(movieName + "    " + movieID);
                            if (starID != null) {
                                starMap.put(castName, starID);
                            }
                        }
                        catch (SQLException s){
                            //inconstincency star does not exist in database
                            try {
                                FileWriter inconWriter = new FileWriter("src/XMLParsing/castInconsistency.txt", true);
                                BufferedWriter bw = new BufferedWriter(inconWriter);
                                PrintWriter out = new PrintWriter(bw);

                                out.println(castName+ " cast inconsistency does not exist in db. will not be included");
                                out.close();
                            } catch (IOException e) {
                                System.out.println("An error occurred.");
                                e.printStackTrace();
                            }
                            //System.out.println(castName + " does not exist in db");
                        }

                }
                    String movieID = movieMap.get(movieName);
                    if (StarMovie.containsKey(movieID)) {
                        // If it does, add the movie ID to the existing list of movies for that genre
                        ArrayList<String> castList = StarMovie.get(movieMap.get(movieName));
                        //System.out.println(starMap.get(castName));
                        castList.add(starMap.get(castName));
                        StarMovie.put(movieID, castList);
                    } else {
                        // If it doesn't, create a new list and add the movie ID to it
                        ArrayList<String> castList = new ArrayList<>();
                        castList.add(starMap.get(castName));
                        StarMovie.put(movieID, castList);
                    }
            }

                //ArrayList<String> movieCast = cast.getCast();
            }

            conn.close();

            // close the connection when finished
        } catch (SQLException e) {
            // handle any errors that may occur
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();

        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("filmc")) {
            //create a new instance of employee
            tempCast = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        //System.out.println(tempVal);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
            myCasts.add(tempCast);
        } else if (qName.equalsIgnoreCase("a")) {
            tempCast.setCast(tempVal);
            //System.out.println(tempStar.getTitle());
        } else if (qName.equalsIgnoreCase("t")) {
            String currentTitle = tempCast.getMovie();
            if(currentTitle== null){
                tempCast.setMovie(tempVal);
            }



        }
    }

    public static void main(String[] args) throws IOException,NamingException, SQLException{
        CastSaxParser mainParser = new CastSaxParser();
        mainParser.runParse();
    }

}
