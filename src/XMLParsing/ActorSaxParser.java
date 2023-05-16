package XMLParsing;

import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class ActorSaxParser extends DefaultHandler {

    List<Star> myStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public ActorSaxParser() {
        myStars = new ArrayList<Star>();
    }

    public void runParse() throws IOException, NamingException, SQLException {
        try {
            File inconFile = new File("src/XMLParsing/actorInconsistency.txt");
            if (inconFile.createNewFile()) {
                //System.out.println("File created: " + inocnFile.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        parseDocument();
        printData();
        importTable();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/XMLParsing/stanford-movies/actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
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
            String query = "SELECT id FROM moviedb.stars\n" +
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
        PrintWriter fileWriter = new PrintWriter("src/XMLParsing/addStars.sql", "UTF-8");
        int largestID = getLargestID();
        //System.out.println(largestID);
        fileWriter.printf("BEGIN; -- START TRANSACTION\n");
        for(Star star: myStars){
            largestID++;
            fileWriter.printf("INSERT INTO moviedb.stars VALUES(" );
            fileWriter.printf("\'nm%s\', ", largestID);
            if(star.getBirth() == 0000){
                fileWriter.printf("\"%s\", ", star.getName());
                fileWriter.print("null); \n");

            }
            else {
                fileWriter.printf("\"%s\", ", star.getName());
                fileWriter.printf("%s);\n ", star.getBirth());
            }
        }
        fileWriter.printf("COMMIT; -- Terminate the ONE transaction");
        fileWriter.close();

    }

    /**
     * Iterate through the list and print
     * the contents
     */

    private void printData() {

        //System.out.println("No of stars '" + myStars.size() + "'.");

//        Iterator<Star> it = myStars.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
        System.out.println("No of actors '" + myStars.size() + "' added to database.");

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempStar = new Star();
            //System.out.println("gay");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        //System.out.println(tempVal);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myStars.add(tempStar);
        } else if (qName.equalsIgnoreCase("stageName")) {
            tempStar.setName(tempVal);
            //System.out.println(tempStar.getTitle());
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                int year = Integer.parseInt(tempVal);
                tempStar.setBirth(year);
            }
            catch(NumberFormatException nfe){
                tempStar.setBirth(0000);

                // inconstincy here
                try {
                    FileWriter inconWriter = new FileWriter("src/XMLParsing/actorInconsistency.txt", true);
                    BufferedWriter bw = new BufferedWriter(inconWriter);
                    PrintWriter out = new PrintWriter(bw);

                    out.println(tempStar.getName() + " inconsistency with birthyear. left as null");
                    out.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                //System.out.println(tempStar.getName() + " not included because year was not in correct format");
            }

        }
    }

    public static void main(String[] args) throws IOException, NamingException, SQLException{
        ActorSaxParser mainParser = new ActorSaxParser();
        mainParser.runParse();
    }

}
