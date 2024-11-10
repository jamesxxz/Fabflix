import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class Inserter {
    private DataSource dataSource;
    private Set<String> missingMovies;
    private Set<String> missingActors;
    private Set<String[]> invalidData;
    private final String username = "mytestuser";
    private final String password = "My6$Password";
    private final String databaseUrl = "jdbc:mysql://localhost:3306/moviedb";

    public Inserter() {
        try {//deal with the missing data
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            missingMovies = new HashSet<>();
            missingActors = new HashSet<>();
            invalidData = new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMovies(List<Movie> movies) {
        System.out.println("Total movies to add: " + movies.size());
        try (Connection connection = DriverManager.getConnection(databaseUrl, username, password)) {
            connection.setAutoCommit(false);

            String queryMovie = "SELECT count(*) FROM movies WHERE id = ? OR title = ?";
            String insertMovie = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
            String queryGenre = "SELECT id FROM genres WHERE name = ?";
            String insertGenre = "INSERT INTO genres (name) VALUES (?)";
            String linkGenreMovie = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";

            PreparedStatement movieCheckStmt = connection.prepareStatement(queryMovie);
            PreparedStatement movieInsertStmt = connection.prepareStatement(insertMovie);
            PreparedStatement genreCheckStmt = connection.prepareStatement(queryGenre);
            PreparedStatement genreInsertStmt = connection.prepareStatement(insertGenre, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement genreMovieLinkStmt = connection.prepareStatement(linkGenreMovie);

            Set<String> movieBatchIds = new HashSet<>();
            int genreCounter = 0;
            int duplicateCounter = 0;

            for (Movie movie : movies) {
                if (movie.getId() == null || movie.getId().isEmpty()) {
                    invalidData.add(new String[]{" has no ID", " title: " + movie.getTitle()});
                    continue;
                }
                if (movieBatchIds.contains(movie.getId())) {
                    invalidData.add(new String[]{" duplicated", " id: " + movie.getId()});
                    duplicateCounter++;
                    continue;
                }
                if (movie.getTitle().isEmpty()) {
                    invalidData.add(new String[]{" has no title", " id: " + movie.getId()});
                    continue;
                }

                movieCheckStmt.setString(1, movie.getId());
                movieCheckStmt.setString(2, movie.getTitle());
                ResultSet movieRs = movieCheckStmt.executeQuery();

                if (movieRs.next() && movieRs.getInt(1) == 0) {
                    movieInsertStmt.setString(1, movie.getId());
                    movieInsertStmt.setString(2, movie.getTitle());
                    try {
                        movieInsertStmt.setInt(3, Integer.parseInt(movie.getYear()));
                    } catch (NumberFormatException e) {
                        invalidData.add(new String[]{" invalid year", " id: " + movie.getId()});
                        continue;
                    }
                    movieInsertStmt.setString(4, movie.getDirector());
                    movieInsertStmt.addBatch();
                    movieBatchIds.add(movie.getId());

                    for (String genreName : movie.getGenres()) {
                        if (genreName == null || genreName.isEmpty()) {
                            continue;
                        }
                        genreCheckStmt.setString(1, genreName);
                        ResultSet genreRs = genreCheckStmt.executeQuery();

                        int genreId = 0;
                        if (genreRs.next()) {
                            genreId = genreRs.getInt(1);
                        } else {
                            genreInsertStmt.setString(1, genreName);
                            genreInsertStmt.executeUpdate();
                            ResultSet generatedKeys = genreInsertStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                genreId = generatedKeys.getInt(1);
                            }
                            genreCounter++;
                        }
                        String checkLinkSql = "SELECT COUNT(*) FROM genres_in_movies WHERE genreId = ? AND movieId = ?";
                        try (PreparedStatement checkLinkStmt = connection.prepareStatement(checkLinkSql)) {
                            checkLinkStmt.setInt(1, genreId);
                            checkLinkStmt.setString(2, movie.getId());
                            ResultSet linkCheckRs = checkLinkStmt.executeQuery();
                            if (linkCheckRs.next() && linkCheckRs.getInt(1) == 0) {
                                genreMovieLinkStmt.setInt(1, genreId);
                                genreMovieLinkStmt.setString(2, movie.getId());
                                genreMovieLinkStmt.addBatch();
                            }
                        }
                    }
                } else {
                    invalidData.add(new String[]{" inconsistent data", " id: " + movie.getId()});
                }
            }
            movieInsertStmt.executeBatch();
            genreMovieLinkStmt.executeBatch();
            connection.commit();

            System.out.println("Genres added: " + genreCounter);
            System.out.println("Genre-movie links created: " + movieBatchIds.size());
            System.out.println("Duplicate movies found: " + duplicateCounter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addActors(List<List<String>> actors) {
        try (Connection conn = DriverManager.getConnection(databaseUrl, username, password)) {
            conn.setAutoCommit(false);

            String checkActorSql = "SELECT count(*) FROM stars WHERE name = ? OR name = ?";
            String insertActorSql = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement actorCheckStmt = conn.prepareStatement(checkActorSql);
            PreparedStatement actorInsertStmt = conn.prepareStatement(insertActorSql);

            int counter = 0;
            for (List<String> actor : actors) {
                if (actor.get(0).isEmpty() && actor.get(1).isEmpty()) {
                    System.out.println("Actor missing name.");
                    continue;
                }
                actorCheckStmt.setString(1, actor.get(0));
                actorCheckStmt.setString(2, actor.get(1));
                ResultSet rs = actorCheckStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    actorInsertStmt.setString(1, String.valueOf(counter));
                    actorInsertStmt.setString(2, actor.get(1).isEmpty() ? actor.get(0) : actor.get(1));
                    if (actor.get(2).isEmpty() || actor.get(2).equalsIgnoreCase("n.a.")) {
                        actorInsertStmt.setNull(3, Types.INTEGER);
                    } else {
                        try {
                            actorInsertStmt.setInt(3, Integer.parseInt(actor.get(2)));
                        } catch (NumberFormatException e) {
                            actorInsertStmt.setNull(3, Types.INTEGER);
                        }
                    }
                    actorInsertStmt.addBatch();
                    counter++;
                }
            }
            actorInsertStmt.executeBatch();
            conn.commit();

            System.out.println("Actors added: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addActorsToMovies(Map<String, List<String>> actorMovieMap) {
        try (Connection connection = DriverManager.getConnection(databaseUrl, username, password)) {
            int totalLinks = 0;
            connection.setAutoCommit(false);

            String verifyMovie = "SELECT count(*) FROM movies WHERE id = ?";
            String verifyActor = "SELECT id FROM stars WHERE name = ?";
            String addActorMovie = "INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)";

            PreparedStatement movieCheckStmt = connection.prepareStatement(verifyMovie);
            PreparedStatement actorCheckStmt = connection.prepareStatement(verifyActor);
            PreparedStatement linkStmt = connection.prepareStatement(addActorMovie);

            for (Map.Entry<String, List<String>> entry : actorMovieMap.entrySet()) {
                String movieId = entry.getKey();
                List<String> actorNames = entry.getValue();
                if (missingMovies.contains(movieId)) {
                    continue;
                }

                movieCheckStmt.setString(1, movieId);
                ResultSet movieRs = movieCheckStmt.executeQuery();

                if (movieRs.next() && movieRs.getInt(1) > 0) {
                    for (String actorName : actorNames) {
                        if (missingActors.contains(actorName)) {
                            continue;
                        }
                        actorCheckStmt.setString(1, actorName);
                        ResultSet actorRs = actorCheckStmt.executeQuery();
                        if (actorRs.next()) {
                            String actorId = actorRs.getString(1);
                            linkStmt.setString(1, actorId);
                            linkStmt.setString(2, movieId);
                            linkStmt.addBatch();
                        } else {
                            missingActors.add(actorName);
                        }
                    }
                    totalLinks++;
                } else {
                    missingMovies.add(movieId);
                }
            }
            linkStmt.executeBatch();
            connection.commit();
            System.out.println("Actor-movie links created: " + totalLinks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateReport() {
        System.out.println(missingMovies.size() + " movies missing.");
        System.out.println(missingActors.size() + " actors missing.");
        System.out.println(invalidData.size() + " invalid records.");
        System.out.println("finished");

        try (PrintWriter writer = new PrintWriter("errorReport.txt", "UTF-8")) {
            for (String[] error : invalidData) {
                writer.println("Movie" + error[1] + error[0]);
            }
            for (String movieId : missingMovies) {
                writer.println("Movie id " + movieId + " not found.");
            }
            for (String actorId : missingActors) {
                writer.println("Actor name " + actorId + " not found.");
            }
            //writer.println("finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inserting_Movies(List<Movie> movies) {
        addMovies(movies);
    }

    public void inserting_Stars(List<List<String>> actors) {
        addActors(actors);
    }

    public void insertingStarsinMovies(Map<String, List<String>> starsInMovies) {
        addActorsToMovies(starsInMovies);
    }

    public void saveingDatatoFilewhichareNotFound() {
        generateReport();
    }

}

