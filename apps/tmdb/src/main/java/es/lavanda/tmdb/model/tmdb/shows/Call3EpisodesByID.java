package es.lavanda.tmdb.model.tmdb.shows;

public class Call3EpisodesByID {
	String air_date;
	int episode_number;
	String name;
	String overview;
	int season_number;
	String still_path;
	double vote_average;

	protected Call3EpisodesByID() {
		// TODO Auto-generated constructor stub
	}

	public String getAir_date() {
		return air_date;
	}

	public void setAir_date(String air_date) {
		this.air_date = air_date;
	}

	public int getEpisode_number() {
		return episode_number;
	}

	public void setEpisode_number(int episode_number) {
		this.episode_number = episode_number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public int getSeason_number() {
		return season_number;
	}

	public void setSeason_number(int season_number) {
		this.season_number = season_number;
	}

	public String getStill_path() {
		return still_path;
	}

	public void setStill_path(String still_path) {
		this.still_path = still_path;
	}

	public double getVote_average() {
		return vote_average;
	}

	public void setVote_average(double vote_average) {
		this.vote_average = vote_average;
	}

}
