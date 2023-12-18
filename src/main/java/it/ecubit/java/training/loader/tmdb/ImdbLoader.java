package it.ecubit.java.training.loader.tmdb;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import it.ecubit.java.training.beans.Actor;
import it.ecubit.java.training.beans.Director;
import it.ecubit.java.training.beans.Genre;
import it.ecubit.java.training.beans.Movie;

import java.io.*;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class ImdbLoader {

    private static final String IMDB_TOP_1000_CSV_FILENAME = "imdb_top_50.csv";

    public static List<Movie> loadMovies() {
        // Load from CSV
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(IMDB_TOP_1000_CSV_FILENAME);
        List<Movie> movies = new ArrayList<>();
        List<Genre> genres = loadAllGenres();
        assert genres != null;
        if(is != null) {
            try(
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            ) {
                String[] nextRecord;
                // Read data line by line
                int counter = 0;
                while ((nextRecord = csvReader.readNext()) != null) {
                    // columns format:
                    // Poster,Title,Year,Certificate,Duration,Genre,Rating,Overview,MetaScore,Director,Star 1-4,Votes,Gross
                    counter += 1;
                    System.out.println("Processing movie #" + counter + "...");
                    String title = nextRecord[1];
                    int year = -1;
                    try {
                        year = Integer.parseInt(nextRecord[2]);
                    }
                    catch(NumberFormatException nfExc) {
                        System.err.println("Unable to parse year '" + nextRecord[2] + "' for movie " + title);
                    }
                    String durationInMinutesStr = nextRecord[4];
                    if(durationInMinutesStr.endsWith(" min")) {
                        durationInMinutesStr = durationInMinutesStr.substring(0, durationInMinutesStr.length() - 4);
                    }
                    int duration = Integer.parseInt(durationInMinutesStr);
                    String genreNames = nextRecord[5];
                    float rating = Float.parseFloat(nextRecord[6]);
                    String overview = nextRecord[7];
                    long votes = Long.parseLong(nextRecord [14]);

                    // Search IMDB ID on TMDB with the TMDB search API
                    Optional<String[]> optIdentifiers = UnirestTmdbRetriever.searchImdbIdByMovieTitle(title, year);
                    String id = "";
                    String imdb = "";
                    if(optIdentifiers.isPresent()) {
                        id = optIdentifiers.get()[0];
                        imdb = optIdentifiers.get()[1];
                    }
                    int idAsInt = -1;
                    if(!id.isEmpty()) {
                        idAsInt = Integer.parseInt(id);
                    }
                    Movie movie = new Movie(idAsInt, title, overview, year, imdb, duration, rating,votes);
                    Set<String> genreNamesSet = new HashSet<>();
                    if(genreNames.contains(",")) {
                        String[] genreNamesArray = genreNames.split(" ");
                        for(String s : genreNamesArray) {
                            String genreName = s.trim();
                            if (!genreName.isEmpty()) {
                                genreNamesSet.add(genreName);
                            }
                        }
                    }
                    else {
                        genreNamesSet.add(genreNames.trim());
                    }
                    for(String g : genreNamesSet) {
                        Optional<Genre> optGenre = genres.stream().filter(gr -> gr.getName().equalsIgnoreCase(g)).findFirst();
                        optGenre.ifPresent(genre -> movie.getGenres().add(genre));
                    }
                    String directorName = nextRecord[9];
                    Optional<Director> optDirector = loadDirectorByName(directorName);
                    optDirector.ifPresent(director -> movie.getDirectors().add(director));
                    String actorName1 = nextRecord[10];
                    Optional<Actor> optActor1 = loadActorByName(actorName1);
                    optActor1.ifPresent(actor -> movie.getActors().add(actor));
                    String actorName2 = nextRecord[11];
                    Optional<Actor> optActor2 = loadActorByName(actorName2);
                    optActor2.ifPresent(actor -> movie.getActors().add(actor));
                    String actorName3 = nextRecord[12];
                    Optional<Actor> optActor3 = loadActorByName(actorName3);
                    optActor3.ifPresent(actor -> movie.getActors().add(actor));
                    String actorName4 = nextRecord[13];
                    Optional<Actor> optActor4 = loadActorByName(actorName4);
                    optActor4.ifPresent(actor -> movie.getActors().add(actor));
                    movies.add(movie);
                    try {
                        Thread.sleep(100);
                    }
                    catch(InterruptedException iExc) {
                        // Back to work
                    }
                }
            }
            catch(CsvValidationException | IOException exc) {
                String errorMsg = String.format(
                        "Error loading data in CSV file %s: %s - %s",
                        IMDB_TOP_1000_CSV_FILENAME,
                        exc.getClass().getSimpleName(),
                        exc.getMessage());
                System.err.println(errorMsg);
            }
        }
        return movies;
    }

    public static Set<Director> loadAllDirectors() {
        // Load from CSV
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(IMDB_TOP_1000_CSV_FILENAME);
        Set<Director> directors = new HashSet<>();
        if(is != null) {
            try(
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            ) {
                String[] nextRecord;
                // Read data line by line
                while ((nextRecord = csvReader.readNext()) != null) {
                    String directorName = nextRecord[9];
                    // Search director on IMDB with the TMDB search API
                    Optional<Director> optDirector = loadDirectorByName(directorName);
                    optDirector.ifPresent(directors::add);
                }
            }
            catch(CsvValidationException | IOException exc) {
                String errorMsg = String.format(
                        "Error loading data in CSV file %s: %s - %s",
                        IMDB_TOP_1000_CSV_FILENAME,
                        exc.getClass().getSimpleName(),
                        exc.getMessage());
                System.err.println(errorMsg);
            }
        }
        return directors;
    }

    public static Optional<Director> loadDirectorByName(String directorName) {
        // Search director on IMDB with the TMDB search API
        return UnirestTmdbRetriever.searchDirectorByName(directorName);
    }

    public static Set<Actor> loadAllActors() {
        // Load from CSV
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(IMDB_TOP_1000_CSV_FILENAME);
        Set<Actor> actors = new HashSet<>();
        if(is != null) {
            try(
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            ) {
                String[] nextRecord;
                // Read data line by line
                while ((nextRecord = csvReader.readNext()) != null) {
                    String actorName1 = nextRecord[10];
                    String actorName2 = nextRecord[11];
                    String actorName3 = nextRecord[12];
                    String actorName4 = nextRecord[13];
                    List<String> actorNames = Arrays.asList(actorName1, actorName2, actorName3, actorName4);
                    // Search actors on IMDB with the TMDB search API
                    actorNames.forEach(an -> {
                                Optional<Actor> optActor = loadActorByName(an);
                                optActor.ifPresent(actors::add);
                            }
                    );
                }
            }
            catch(CsvValidationException | IOException exc) {
                String errorMsg = String.format(
                        "Error loading data in CSV file %s: %s - %s",
                        IMDB_TOP_1000_CSV_FILENAME,
                        exc.getClass().getSimpleName(),
                        exc.getMessage());
                System.err.println(errorMsg);
            }
        }
        return actors;
    }

    public static Optional<Actor> loadActorByName(String actorName) {
        // Search actor on IMDB with the TMDB search API
        return UnirestTmdbRetriever.searchActorByName(actorName);
    }

    public static List<Genre> loadAllGenres() {
        // Load entire list with the TMDB genres API
        return UnirestTmdbRetriever.loadAllMovieGenres();
    }

    public static void main(String[] args) {
        long startMillis = System.currentTimeMillis();
        List<Movie> movies = ImdbLoader.loadMovies();
        long endMillis = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        String timeSpentStr = df.format((endMillis - startMillis) / 1000);
        System.out.println("Loaded " + movies.size() + " movies. Time spent: " + timeSpentStr + " sec.");
        movies.forEach(m -> System.out.println(m.toString()));
    }

}
