package it.ecubit.java.training.collections.test;

import it.ecubit.java.training.beans.Actor;
import it.ecubit.java.training.beans.Director;
import it.ecubit.java.training.beans.Movie;
import it.ecubit.java.training.loader.tmdb.ImdbLoader;

import java.util.*;

public class JavaCollectionsTest {

    public static void main(String[] args) {
        // Loading of all the top 1000 movies can take up to 10 minutes (needs to call the TMDB APIs for retrieving all the data)
        List<Movie> top1000Movies = ImdbLoader.loadMovies();

        // Exercise 1: Sort the movies by release year (from the most recent to the less recent)
        // and print the results with a counter before the movie info, one for each row
        // (i. e. '1) <MOVIE INFO>'\n'2) <MOVIE INFO>', ...)

        // Exercise 2: Sort the movies lexicographically by title
        // and print the results with a counter before the movie info, one for each row
        // (i. e. '1) <MOVIE INFO>'\n'2) <MOVIE INFO>', ...)

        // Exercise 3: How many movies has been directed by 'Peter Jackson'? Print all of them, one by line.

        // Exercise 4: How many movies did 'Orlando Bloom' star in as an actor? Print all of them, one by line.

        // Exercise 5: Sort the movies by rating (ascending, from the less rated to the most rated)
        // and by movie title (lexicographically) as a secondary sort criterion
        // and print the results with a counter before the movie info, one for each row

        // Exercise 6: Sort the movies by duration (ascending, from the shortest to the longest oned)
        // and by release year (ascending, from the less recent to the most recent one) as a secondary sort criterion
        // and print the results with a counter before the movie info, one for each row

        // Exercise 7: Group movies by actor, i.e. produce a map with actor name as key and a list of movies as values;
        // the list should contain the films in which the actor starred in (no duplicates)
        // and print the map with a counter before the map entry, one for each row

        // Exercise 8: Group movies by director, i.e. produce a map with director name as key and a list of movies as values;
        // the list should contain the films in which the director took care of the direction (no duplicates)
        // and print the map with a counter before the map entry, one for each row

        // Exercise 9: Add the film's box office total income to the movie loading process (field 'Gross' in the CSV)
        // and print the first 20 films who earned most money ever, one for each row, from the first to the 20th

        // Exercise 10: Add the number of votes received on the Social Media for each film (field 'No_of_Votes' in the CSV)
        // and print the first 20 films who received most votes, one for each row, from the first to the 20th

    }
}