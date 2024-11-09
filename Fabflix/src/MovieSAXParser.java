import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieSAXParser extends DefaultHandler {

    private List<Movie> movieList;
    private List<List<String>> actorList;
    private Map<String, List<String>> movieActorsMap;
    private String currentValue;
    private Movie currentMovie;
    private String directorName;
    private String currentFilmId;
    private String firstName, lastName, stageName;

    private MovieCategories movieCategories;

    public MovieSAXParser() {
        movieList = new ArrayList<>();
        movieActorsMap = new HashMap<>();
        actorList = new ArrayList<>();
    }

    public void executeParsing() {
        movieCategories = new MovieCategories();
        initializeParser();
        displayData();
    }

    private void initializeParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {//need to be revised when uploading to aws
            SAXParser parser = factory.newSAXParser();
            //parser.parse在爬xml文件时，每一个xml中的元素都会被startElement,characters, endElement等方法检查。
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/mains243.xml", this);
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/casts124.xml", this);
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/actors63.xml", this);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        // Uncomment if you need to print movies or actor lists
         //System.out.println("Total Movies: " + movieList.size());//没在terminal看到这个消息！
         for (Movie movie : movieList) {
             System.out.println(movie.toString());
         }
         System.out.println(movieActorsMap);
         System.out.println(actorList);
         System.out.println("Total Movies: " + movieList.size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentValue = "";
        if (qName.equalsIgnoreCase("film")) {//当xml文件中的元素标签是film时，starElement方法会被调用
            currentMovie = new Movie();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentValue = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName.toLowerCase()) {
            case "dirname"://这些dirname，film,t等都是xml中元素的标签
                directorName = currentValue;//把文本内容存到directorName中
                break;
            case "film":
                currentMovie.setDirector(directorName);
                movieList.add(currentMovie);
                break;
            case "t":
                currentMovie.setTitle(currentValue);
                break;
            case "year":
                currentMovie.setYear(currentValue);
                break;
            case "cat":
                currentMovie.addGenre(movieCategories.findCategory(currentValue));
                break;
            case "fid":
                currentMovie.setId(currentValue);
                break;
            case "f":
                currentFilmId = currentValue;
                break;
            case "a":
                movieActorsMap.computeIfAbsent(currentFilmId, k -> new ArrayList<>()).add(currentValue);
                break;
            case "stagename":
                stageName = currentValue;
                break;
            case "familyname":
                lastName = currentValue;
                break;
            case "firstname":
                firstName = currentValue;
                break;
            case "dob":
                List<String> actorDetails = new ArrayList<>();
                actorDetails.add(stageName);
                actorDetails.add(firstName + " " + lastName);
                actorDetails.add(currentValue);
                actorList.add(actorDetails);
                break;
        }

    }
    //the following are the three getter
    public List<List<String>> getActorList() {
        return actorList;
    }

    public Map<String, List<String>> getMovieActorsMap() {
        return movieActorsMap;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public static void main(String[] args) {
        MovieSAXParser parserInstance = new MovieSAXParser();
        parserInstance.executeParsing();
        Inserter insertion = new Inserter();
        insertion.inserting_Movies(parserInstance.getMovieList());
        insertion.inserting_Stars(parserInstance.getActorList());
        insertion.insertingStarsinMovies(parserInstance.getMovieActorsMap());
        insertion.saveingDatatoFilewhichareNotFound();
    }
}
