package es.lavanda.tmdb.model.tmdb.shows;

public class Call2SeasonsTvShowById{
	String air_date;
	int episode_count;
	int id;
	String poster_path;
	int season_number;
	String overview;

	protected Call2SeasonsTvShowById(){
	}

	public String getAir_date(){
		return air_date;
	}

	public void setAir_date(String air_date){
		this.air_date = air_date;
	}

	public int getEpisode_count(){
		return episode_count;
	}

	public void setEpisode_count(int episode_count){
		this.episode_count = episode_count;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getPoster_path(){
		return poster_path;
	}

	public void setPoster_path(String poster_path){
		this.poster_path = poster_path;
	}

	public int getSeason_number(){
		return season_number;
	}

	public void setSeason_number(int season_number){
		this.season_number = season_number;
	}

	public String getOverview(){
		return overview;
	}

	public void setOverview(String overview){
		this.overview = overview;
	}
}
