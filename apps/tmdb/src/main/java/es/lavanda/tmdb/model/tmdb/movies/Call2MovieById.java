package es.lavanda.tmdb.model.tmdb.movies;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Call2MovieById {

	@JsonProperty("backdrop_path")
	private String backdropPath;

	private List<Call2Genres> genres;

	@JsonProperty("original_title")
	private String originalTitle;

	private String overview;

	@JsonProperty("poster_path")
	private String posterPath;

	@JsonProperty("release_date")
	private String releaseDate;

	private String runtime;

	@JsonProperty("vote_average")
	private double voteAverage;

	private int id;

	private String title;

}
