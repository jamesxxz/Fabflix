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

        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/mains243.xml", this);
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/casts124.xml", this);
            parser.parse("/Users/darius/Desktop/2024-fall-cs-122b-cpdd/Fabflix/xml/actors63.xml", this);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        // Uncomment if you need to print movies or actor lists
        // System.out.println("Total Movies: " + movieList.size());
        // for (Movie movie : movieList) {
        //     System.out.println(movie.toString());
        // }
        // System.out.println(movieActorsMap);
        // System.out.println(actorList);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentValue = "";
        if (qName.equalsIgnoreCase("film")) {
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
            case "dirname":
                directorName = currentValue;
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
        Insertion insertion = new Insertion();
        insertion.insertMovies(parserInstance.getMovieList());
        insertion.insertStars(parserInstance.getActorList());
        insertion.insertStarsInMovies(parserInstance.getMovieActorsMap());
        insertion.saveNotFoundDataToFile();
    }
}
