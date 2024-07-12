package es.lavanda.tmdb.model.tmdb.movies;

import java.util.List;

import lombok.Data;

@Data
public class Call1SearchMovie {

	private List<Call1ResultsMovies> results;

}
