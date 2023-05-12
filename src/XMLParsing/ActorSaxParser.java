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

public class ActorSaxParser extends DefaultHandler {

    List<Star> myStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public ActorSaxParser() {
        myStars = new ArrayList<Star>();
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
            sp.parse("src/XMLParsing/actors63.xml", this);

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

        System.out.println("No of stars '" + myStars.size() + "'.");

        Iterator<Star> it = myStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of stars '" + myStars.size() + "'.");

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
            tempStar.setBirth(tempVal);

        }
    }

    public static void main(String[] args) {
        ActorSaxParser mainParser = new ActorSaxParser();
        mainParser.runParse();
    }

}
