package es.lavanda.tmdb.model.tmdb.shows;

import java.util.ArrayList;
import java.util.List;

public class Call4CreditsTVShows {
	private List<Call4CastShows> cast = new ArrayList<>();
	private List<Call4CrewShows> crew = new ArrayList<>();

	protected Call4CreditsTVShows() {
	}

	public List<Call4CastShows> getCast() {
		return cast;
	}

	public void setCast(List<Call4CastShows> cast) {
		this.cast = cast;
	}

	public List<Call4CrewShows> getCrew() {
		return crew;
	}

	public void setCrew(List<Call4CrewShows> crew) {
		this.crew = crew;
	}


}
