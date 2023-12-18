package it.ecubit.java.training.loader.tmdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.ecubit.java.training.beans.Actor;
import it.ecubit.java.training.beans.Director;
import it.ecubit.java.training.beans.Genre;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.*;

import static kong.unirest.ContentType.APPLICATION_JSON;

public class UnirestTmdbRetriever {

    private static final String TMDB_API_KEY = "3498e15129b2beae9092e64a9db52533";
    private static final String TMDB_API_VERSION = "3";
    private static final String TMDB_API_BASE_URL = "https://api.themoviedb.org/" + TMDB_API_VERSION;

    public static Optional<String[]> searchImdbIdByMovieTitle(String movieTitle, int year) {
        // https://api.themoviedb.org/3/search/movie?
        //     api_key=<API KEY>&
        //     language=en-US&
        //     query=The%20Shawshank%20Redemption&
        //     page=1&
        //     year=1994
        Unirest.config().instrumentWith(requestSummary -> {
            long startMillis = System.currentTimeMillis();
            return (responseSummary, exception) -> System.out.printf("REST Call: SEARCH IMDB ID BY MOVIE TITLE, status: %d time spent: %d ms%n",
                    responseSummary.getStatus(), System.currentTimeMillis() - startMillis);
        }).defaultBaseUrl(TMDB_API_BASE_URL);
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("api_key", TMDB_API_KEY);
        parametersMap.put("language", "en-US");
        parametersMap.put("page", "1");
        parametersMap.put("year", year);
        parametersMap.put("query", movieTitle);
        GetRequest getRequest = Unirest.get("/search/movie")
                .accept(APPLICATION_JSON.getMimeType())
                .queryString(parametersMap);
        HttpResponse<String> responseAsString = getRequest.asString();
        if(responseAsString.getStatus() != 200) {
            System.err.printf("Error executing REST call 'searchImdbIdByMovieTitle' with parameters '%s' and %d: response status %d",
                    movieTitle, year, responseAsString.getStatus()
            );
            return Optional.empty();
        }
        else {
            // parse JSON
            JsonObject root = JsonParser.parseString(responseAsString.getBody()).getAsJsonObject();
            String resultsField = "results";
            String movieIdentifier = "";
            String imdbIdentifier = "";
            if(root.has(resultsField)) {
                JsonArray resultsArray = root.getAsJsonArray(resultsField);
                if(resultsArray.size() > 0) {
                    JsonObject firstObj = resultsArray.get(0).getAsJsonObject();
                    String idField = "id";
                    String imdbIdField = "imdb_id";
                    if(firstObj.has(idField)) {
                        movieIdentifier = firstObj.get(idField).getAsString();
                    }
                    if(!movieIdentifier.isEmpty()) {
                        Map<String, Object> secondCallParametersMap = new HashMap<>();
                        secondCallParametersMap.put("api_key", TMDB_API_KEY);
                        parametersMap.put("language", "en-US");
                        GetRequest secondGetRequest = Unirest.get("/movie/" + movieIdentifier)
                                .accept(APPLICATION_JSON.getMimeType())
                                .queryString(parametersMap);
                        HttpResponse<String> secondCallResponseAsString = secondGetRequest.asString();
                        if (secondCallResponseAsString.getStatus() == 200) {
                            JsonObject internalRoot = JsonParser.parseString(secondCallResponseAsString.getBody()).getAsJsonObject();
                            if(internalRoot.has(imdbIdField)) {
                                imdbIdentifier = internalRoot.get(imdbIdField).getAsString();
                            }
                        }
                    }
                }
            }
            if(!movieIdentifier.isEmpty() && !imdbIdentifier.isEmpty()) {
                String[] pair = new String[2];
                pair[0] = movieIdentifier;
                pair[1] = imdbIdentifier;
                return Optional.of(pair);
            }
            else {
                return Optional.empty();
            }
        }
    }

    public static Optional<Director> searchDirectorByName(String directorName) {
        // https://api.themoviedb.org/3/search/person?
        //    api_key=<API KEY>&
        //    language=en-US&
        //    page=1&
        //    query=Marco%20Bellocchio
        Unirest.config().instrumentWith(requestSummary -> {
            long startMillis = System.currentTimeMillis();
            return (responseSummary, exception) -> System.out.printf("REST Call: SEARCH DIRECTOR BY NAME, status: %d time spent: %d ms%n",
                    responseSummary.getStatus(), System.currentTimeMillis() - startMillis);
        }).defaultBaseUrl(TMDB_API_BASE_URL);
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("api_key", TMDB_API_KEY);
        parametersMap.put("language", "en-US");
        parametersMap.put("page", "1");
        parametersMap.put("query", directorName);
        GetRequest getRequest = Unirest.get("/search/person")
                .accept(APPLICATION_JSON.getMimeType())
                .queryString(parametersMap);
        HttpResponse<String> responseAsString = getRequest.asString();
        if(responseAsString.getStatus() != 200) {
            System.err.printf("Error executing REST call 'searchDirectorByName' with parameter %s: response status %d",
                    directorName, responseAsString.getStatus()
            );
            return Optional.empty();
        }
        else {
            // parse JSON
            Director result = new Director();
            JsonObject root = JsonParser.parseString(responseAsString.getBody()).getAsJsonObject();
            String resultsField = "results";
            if(root.has(resultsField)) {
                JsonArray resultsArray = root.getAsJsonArray(resultsField);
                Iterator<JsonElement> resultsIterator = resultsArray.iterator();
                boolean found = false;
                while(resultsIterator.hasNext() && !found) {
                    JsonObject directorObject = resultsIterator.next().getAsJsonObject();
                    String nameField = "name";
                    String idField = "id";
                    String departmentField = "known_for_department";
                    if(
                            directorObject.has(nameField) &&
                            directorObject.get(nameField).getAsString().equalsIgnoreCase(directorName) &&
                            directorObject.has(departmentField) &&
                            (
                                directorObject.get(departmentField).getAsString().equalsIgnoreCase("Directing") ||
                                directorObject.get(departmentField).getAsString().equalsIgnoreCase("Writing")
                            )
                    ) {
                        found = true;
                        result.setName(directorObject.get(nameField).getAsString());
                        result.setId(directorObject.get(idField).getAsInt());
                        result.setImdb("nm" + directorObject.get(idField).getAsInt());
                    }
                }
            }
            if(result.getId() > 0 && result.getName() != null) {
                return Optional.of(result);
            }
            else {
                return Optional.empty();
            }
        }
    }

    public static Optional<Actor> searchActorByName(String actorName) {
        // https://api.themoviedb.org/3/search/person?
        //     api_key=<API KEY>&
        //     language=en-US&
        //     page=1&
        //     query=Tim%20Robbins
        Unirest.config().instrumentWith(requestSummary -> {
            long startMillis = System.currentTimeMillis();
            return (responseSummary, exception) -> System.out.printf("REST Call: SEARCH ACTOR BY NAME, status: %d time spent: %d ms%n",
                    responseSummary.getStatus(), System.currentTimeMillis() - startMillis);
        }).defaultBaseUrl(TMDB_API_BASE_URL);
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("api_key", TMDB_API_KEY);
        parametersMap.put("language", "en-US");
        parametersMap.put("page", "1");
        parametersMap.put("query", actorName);
        GetRequest getRequest = Unirest.get("/search/person")
                .accept(APPLICATION_JSON.getMimeType())
                .queryString(parametersMap);
        HttpResponse<String> responseAsString = getRequest.asString();
        if(responseAsString.getStatus() != 200) {
            System.err.printf("Error executing REST call 'searchActorByName' with parameter %s: response status %d",
                    actorName, responseAsString.getStatus()
            );
            return Optional.empty();
        }
        else {
            // parse JSON
            Actor result = new Actor();
            JsonObject root = JsonParser.parseString(responseAsString.getBody()).getAsJsonObject();
            String resultsField = "results";
            if(root.has(resultsField)) {
                JsonArray resultsArray = root.getAsJsonArray(resultsField);
                Iterator<JsonElement> resultsIterator = resultsArray.iterator();
                boolean found = false;
                while(resultsIterator.hasNext() && !found) {
                    JsonObject actorObject = resultsIterator.next().getAsJsonObject();
                    String nameField = "name";
                    String idField = "id";
                    String departmentField = "known_for_department";
                    if(
                            actorObject.has(nameField) &&
                            actorObject.get(nameField).getAsString().equalsIgnoreCase(actorName) &&
                            actorObject.has(departmentField) &&
                            actorObject.get(departmentField).getAsString().equalsIgnoreCase("Acting")
                    ) {
                        found = true;
                        result.setName(actorObject.get(nameField).getAsString());
                        result.setId(actorObject.get(idField).getAsInt());
                        result.setImdb("nm" + actorObject.get(idField).getAsInt());
                    }
                }
            }
            if(result.getId() > 0 && result.getName() != null) {
                return Optional.of(result);
            }
            else {
                return Optional.empty();
            }
        }
    }

    public static List<Genre> loadAllMovieGenres() {
        // https://api.themoviedb.org/3/genre/movie/list?api_key=<API KEY>&language=en-US
        List<Genre> genres = new ArrayList<>();
        Unirest.config().instrumentWith(requestSummary -> {
            long startMillis = System.currentTimeMillis();
            return (responseSummary, exception) -> System.out.printf("REST Call: RETRIEVE GENRES LIST, status: %d time spent: %d ms%n",
                    responseSummary.getStatus(), System.currentTimeMillis() - startMillis);
        }).defaultBaseUrl(TMDB_API_BASE_URL);
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("api_key", TMDB_API_KEY);
        parametersMap.put("language", "en-US");
        GetRequest getRequest = Unirest.get("/genre/movie/list")
                .accept(APPLICATION_JSON.getMimeType())
                .queryString(parametersMap);
        HttpResponse<String> responseAsString = getRequest.asString();
        if(responseAsString.getStatus() != 200) {
            System.err.printf("Error executing REST call 'loadAllMovieGenres': response status %d", responseAsString.getStatus());
        }
        else {
            // parse JSON
            JsonObject root = JsonParser.parseString(responseAsString.getBody()).getAsJsonObject();
            String genresField = "genres";
            if(root.has(genresField)) {
                JsonArray genresArray = root.getAsJsonArray(genresField);
                Iterator<JsonElement> genresIterator = genresArray.iterator();
                while(genresIterator.hasNext()) {
                    JsonObject genreObject = genresIterator.next().getAsJsonObject();
                    String nameField = "name";
                    String idField = "id";
                    if(genreObject.has(nameField) && genreObject.has(idField)) {
                        Genre genre = new Genre(genreObject.get(idField).getAsInt(), genreObject.get(nameField).getAsString());
                        genres.add(genre);
                    }
                }
            }
        }
        return genres;
    }

    public static void main(String[] args) {
        String directorName = "Frank Darabont";
        Optional<Director> optDirector = UnirestTmdbRetriever.searchDirectorByName(directorName);
        if(optDirector.isPresent()) {
            System.out.println("Director: " + optDirector.get().toString());
        }
        else {
            System.out.println("No director found with name '" + directorName + "'");
        }
        String actorName = "Tim Robbins";
        Optional<Actor> optActor = UnirestTmdbRetriever.searchActorByName(actorName);
        if(optActor.isPresent()) {
            System.out.println("Actor: " + optActor.get().toString());
        }
        else {
            System.out.println("No actor found with name '" + actorName + "'");
        }
        String movieTitle = "The Shawshank Redemption";
        Optional<String[]> optIds = UnirestTmdbRetriever.searchImdbIdByMovieTitle(movieTitle, 1994);
        if(optIds.isPresent()) {
            System.out.println("Movie ID: " + optIds.get()[0]);
            System.out.println("IMDB ID: " + optIds.get()[1]);
        }
        else {
            System.out.println("No movie found with title '" + movieTitle + "'");
        }
        List<Genre> genres = UnirestTmdbRetriever.loadAllMovieGenres();
        if(genres.size() > 0) {
            genres.forEach(g -> System.out.println(g.toString()));
        }
        else {
            System.out.println("No genres retrieved using the TMDB APIs");
        }
    }

}
