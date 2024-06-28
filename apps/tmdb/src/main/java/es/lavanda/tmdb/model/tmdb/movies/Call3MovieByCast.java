package es.lavanda.tmdb.model.tmdb.movies;

import java.util.List;

import lombok.Data;

@Data
public class Call3MovieByCast {

	private List<Call3Cast> cast;
	
	private List<Call3Crew> crew;

}