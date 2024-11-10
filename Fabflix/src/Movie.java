import java.util.ArrayList;
import java.util.List;

public class Movie {

    private String director;
    private String title;
    private String year;
    private List<String> genreList;
    private String id;
    private String isbn;
    private String publisher;

    public Movie() {
        this.genreList = new ArrayList<>();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genreList;
    }

    public void addGenre(String genre) {
        this.genreList.add(genre);
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }
    @Override
    public String toString() {
        return String.format("Movie - Director: %s, Title: %s, Year: %s, Genres: %s, ID: %s, ISBN: %s, Publisher: %s",
                getDirector(), getTitle(), getYear(), String.join(", ", getGenres()), getId(), getIsbn(), getPublisher());
    }
}
