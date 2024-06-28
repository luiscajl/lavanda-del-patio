package es.lavanda.tmdb.model.tmdb.shows;

import java.util.List;

public class Call2ShowByID {
	String backdrop_path;
	String poster_path;
	String first_air_date;
	String last_air_date;
	Long id;
	List<Call2Genres> genres;
	Boolean in_production;
	String name;
	String original_name;
	List<Call2SeasonsTvShowById> seasons;
	List<Call2CreatedBy> created_by;
	int number_of_episodes;
	int number_of_seasons;
	String overview;
	String status;
	double vote_average;

	public Call2ShowByID() {
	}

	public String getBackdrop_path() {
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getFirst_air_date() {
		return first_air_date;
	}

	public void setFirst_air_date(String first_air_date) {
		this.first_air_date = first_air_date;
	}

	public String getLast_air_date() {
		return last_air_date;
	}

	public void setLast_air_date(String last_air_date) {
		this.last_air_date = last_air_date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Call2Genres> getGenres() {
		return genres;
	}

	public void setGenres(List<Call2Genres> genres) {
		this.genres = genres;
	}

	public Boolean getIn_production() {
		return in_production;
	}

	public void setIn_production(Boolean in_production) {
		this.in_production = in_production;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginal_name() {
		return original_name;
	}

	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}

	public List<Call2SeasonsTvShowById> getSeasons() {
		return seasons;
	}

	public void setSeasons(List<Call2SeasonsTvShowById> seasons) {
		this.seasons = seasons;
	}

	public List<Call2CreatedBy> getCreated_by() {
		return created_by;
	}

	public void setCreated_by(List<Call2CreatedBy> created_by) {
		this.created_by = created_by;
	}

	public int getNumber_of_episodes() {
		return number_of_episodes;
	}

	public void setNumber_of_episodes(int number_of_episodes) {
		this.number_of_episodes = number_of_episodes;
	}

	public int getNumber_of_seasons() {
		return number_of_seasons;
	}

	public void setNumber_of_seasons(int number_of_seasons) {
		this.number_of_seasons = number_of_seasons;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getVote_average() {
		return vote_average;
	}

	public void setVote_average(double vote_average) {
		this.vote_average = vote_average;
	}

}
