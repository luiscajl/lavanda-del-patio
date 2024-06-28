package es.lavanda.tmdb.model.tmdb.shows;

import java.util.List;

public class Call3SeasonsShowById {
	List<Call3EpisodesByID> episodes;
	int season_number;
	String poster_path;
	String overview;
	String name;

	protected Call3SeasonsShowById() {
	}

	public List<Call3EpisodesByID> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<Call3EpisodesByID> episodes) {
		this.episodes = episodes;
	}

	public int getSeason_number() {
		return season_number;
	}

	public void setSeason_number(int season_number) {
		this.season_number = season_number;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
