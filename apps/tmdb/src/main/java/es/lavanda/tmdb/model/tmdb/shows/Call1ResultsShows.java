package es.lavanda.tmdb.model.tmdb.shows;

public class Call1ResultsShows {
	private String original_name;
	private int id;
	private String name;
	private double vote_count;
	private double vote_average;
	private String poster_path;
	private String first_air_date;
	private double popularity;
	private int[] genre_ids;
	private String original_language;
	private String backdrop_path;
	private String overview;
	private String[] origin_country;

	protected Call1ResultsShows() {
	}

	public String getOriginal_name() {
		return original_name;
	}

	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getVote_count() {
		return vote_count;
	}

	public void setVote_count(double vote_count) {
		this.vote_count = vote_count;
	}

	public double getVote_average() {
		return vote_average;
	}

	public void setVote_average(double vote_average) {
		this.vote_average = vote_average;
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

	public double getPopularity() {
		return popularity;
	}

	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}

	public int[] getGenre_ids() {
		return genre_ids;
	}

	public void setGenre_ids(int[] genre_ids) {
		this.genre_ids = genre_ids;
	}

	public String getOriginal_language() {
		return original_language;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}

	public String getBackdrop_path() {
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String[] getOrigin_country() {
		return origin_country;
	}

	public void setOrigin_country(String[] origin_country) {
		this.origin_country = origin_country;
	}

}
