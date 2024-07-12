package es.lavanda.tmdb.model.tmdb.movies;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Call1ResultsMovies {

	private int id;

	@JsonProperty("original_title")
	private String originalTitle;

	private String title;

}
