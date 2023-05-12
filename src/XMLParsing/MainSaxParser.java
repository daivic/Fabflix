package XMLParsing;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MainSaxParser extends DefaultHandler {

    List<Movies> myMovies;
    String currDir;

    private String tempVal;

    //to maintain context
    private Movies tempMov;

    public MainSaxParser() {
        myMovies = new ArrayList<Movies>();
    }

    public void runParse() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/XMLParsing/mains243.xml", this);

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

        System.out.println("No of movies '" + myMovies.size() + "'.");

        Iterator<Movies> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
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
            tempMov.setYear(tempVal);

    } else if (qName.equalsIgnoreCase("cats")) {
        tempMov.setGenres(tempVal);
    }
    }

    public static void main(String[] args) {
         MainSaxParser mainParser = new MainSaxParser();
        mainParser.runParse();
    }

}
