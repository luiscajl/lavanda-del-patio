package es.lavanda.tmdb.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaIDTO.Type;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.tmdb.exception.TMDBException;
import es.lavanda.tmdb.model.internal.Tripla;
import es.lavanda.tmdb.model.tmdb.movies.Call1ResultsMovies;
import es.lavanda.tmdb.model.tmdb.movies.Call1SearchMovie;
import es.lavanda.tmdb.model.tmdb.movies.Call2MovieById;
import es.lavanda.tmdb.model.tmdb.movies.Call3MovieByCast;
import es.lavanda.tmdb.model.tmdb.shows.Call1ResultsShows;
import es.lavanda.tmdb.model.tmdb.shows.Call1SeachTVShows;
import es.lavanda.tmdb.model.tmdb.shows.Call2ShowByID;
import es.lavanda.tmdb.model.tmdb.shows.Call3SeasonsShowById;
import es.lavanda.tmdb.model.tmdb.shows.Call4CreditsTVShows;
import es.lavanda.tmdb.model.type.QueueType;
import es.lavanda.tmdb.service.TMDBService;
import es.lavanda.tmdb.service.strategy.TMDBStrategy;
import es.lavanda.tmdb.service.strategy.TMDBStrategyFactory;
import es.lavanda.tmdb.service.strategy.TMDBStrategyFilm;
import es.lavanda.tmdb.service.strategy.TMDBStrategyShow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TMDBServiceImpl implements TMDBService {

	@Autowired
	private TMDBStrategyFactory tmdbStrategyFactoryImpl;

	@Autowired
	private TMDBStrategyFilm tmdbStrategyFilm;

	@Autowired
	private TMDBStrategyShow tmdbStrategyShow;

	@Override
	public void analyze(MediaIDTO mediaDTO, QueueType type) throws TMDBException {
		// https://api.themoviedb.org/3/search/multi?api_key=1012d785312735b8039a9f7f172354cb&language=en-US&query=wonder%20woman&page=1&include_adult=true
		// String uriMultiSearch = getMultiSearchURL(mediaDTO.getCleanTitle());
		// TMDBSearchDTO result =
		// tmdbUtil.multiSearch(mediaDTO.getTitleOriginal().replace("#", ""));
		// log.info(result.toString());
		// TMDBResultDTO resultDTO = result.getResults().stream()
		// .filter(r -> similarity(r.getTitle(), mediaDTO.getTitleOriginal()) >
		// 0.9).findFirst()
		// .orElseThrow(() -> new TMDBException("Not found"));
		Optional<TMDBStrategy> optStrategy;
		if (Objects.nonNull(mediaDTO.getType())) {
			if (Type.FILM.equals(mediaDTO.getType())) {
				optStrategy = Optional.of(tmdbStrategyFilm);
			} else {
				optStrategy = Optional.of(tmdbStrategyShow);
			}
		} else {
			optStrategy = tmdbStrategyFactoryImpl.getFactory(mediaDTO.getTorrentCroppedTitle(),
					mediaDTO.getPossibleType());
		}
		if (optStrategy.isPresent()) {
			optStrategy.get().execute(mediaDTO, type);
		}
		// .findFirst()
		// .orElseThrow(() -> new TMDBException("Not similarity found on " +
		// mediaDTO.getCleanTitle()));
		// tmdbStrategyFactoryImpl.getFactory(result.get)
		// final String uriSearchFilms = TMDB_API_MOVIE_SEARCH + TMDB_API_KEY +
		// TMDB_API_LANGUAGE_ES + "&query="
		// + mediaDTO.getCleanTitle().replace("#", "") + URI_SEARCH_MOVIE_PAGE +
		// mediaDTO.getYear();
		// RestTemplate restTemplate = new RestTemplate();
		// Call1SearchMovie result = restTemplate.getForObject(uriSearchFilms,
		// Call1SearchMovie.class);
		// for (Call1ResultsMovies resultsMovies : result.getResults()) {
		// if (similarity(resultsMovies.getTitle().replace(" ", ""),
		// mediaDTO.getCleanTitle().replace(" ", "")) > 0.9) {
		// System.out.println("Analizando: " + mediaDTO.getCleanTitle());
		// String uriSecondCallMovies = TMDB_API_MOVIE_INIT + resultsMovies.getId() +
		// TMDB_API_KEY
		// + TMDB_API_LANGUAGE_ES;
		// Call2MovieById movieById = restTemplate.getForObject(uriSecondCallMovies,
		// Call2MovieById.class);
		// // filmModel.setBackdrop_path(URI_PHOTOS_BACK_TMDB +
		// movieById.backdrop_path);
		// mediaDTO.setImage(URI_PHOTOS_FRONT_TMDB + movieById.getPosterPath());
		// // filmModel.setResumen(movieById.overview);
		// // filmModel.setDuracion(movieById.runtime);
		// // filmModel.setPuntuacion(String.valueOf(movieById.vote_average));
		// }
		// }
		// log.info("Pelicula no analizada " + mediaDTO.getCleanTitle() + " " +
		// mediaDTO.getYear());
		// throw new TMDBException("Pelicula no analizada: " + mediaDTO.getCleanTitle()
		// + " " + mediaDTO.getYear());
	}

	// @Override
	// public Set<Tripla<ShowBackend, Map<Integer, SeasonBackend>,
	// List<ChapterBackend>>> getShowsOfFile(File showsFile)
	// throws TMDBException {
	// Set<Tripla<ShowBackend, Map<Integer, SeasonBackend>, List<ChapterBackend>>>
	// toReturn = new HashSet<>();
	// try {
	// BufferedReader in = new BufferedReader(new FileReader(showsFile));
	// String line;
	// Set<Serie> series = new HashSet<>();
	// while ((line = in.readLine()) != null) {
	// Pattern pattern = Pattern.compile(
	// "([a-zA-Z0-90-9!¡?¿áÁàÀéèÈÉíìÌÍóòÒÓúùÙÚÜüÑñ\\*()^&-.: ]+)\\
	// ([a-zA-Z0-9]{3})([a-zA-Z0-9]{3})\\.");
	// Matcher generalMatcher = pattern.matcher(line);
	// if (generalMatcher.find()) {
	// Serie x = new Serie(line, generalMatcher.group(1),
	// Integer.parseInt(generalMatcher.group(2).substring(1, 3)),
	// Integer.parseInt(generalMatcher.group(3).substring(1, 3)));
	// series.add(x);
	// }
	// }
	// in.close();
	// for (Entry<String, Set<Serie>> serie : listToMapShows(series).entrySet()) {

	// log.debug("La serie que se va a analizar es: " + serie.getKey() + " y sus
	// capitulos son: ");
	// serie.getValue().forEach(x -> log.debug("Capitulo: " + x.getCapitulo() + "
	// Temporada: "
	// + x.getTemporada() + " y su Link es: " + x.getLink()));
	// toReturn.add(getShowsOfFile(serie.getKey(), serie.getValue()));
	// }
	// return toReturn;
	// } catch (FileNotFoundException e) {
	// log.error("Fichero para parsear las series no encontrado:" +
	// showsFile.getName());
	// throw new TMDBException("Fichero no encontrado:" + showsFile.getName());
	// } catch (IOException e) {
	// log.error("Error en la lectura del fichero :" + showsFile.getName(), e);
	// throw new TMDBException("Error en la lectura del fichero :" +
	// showsFile.getName(), e);
	// }

	// }

	/**
	 * Crea los objetos de serie, temporadas y capitulos para la lista de Serie.
	 * 
	 * @param k(Key)   contiene el nombre de la serie
	 * @param v(Value) contiene una lista con todos los capitulos que estan en el
	 *                 servidor
	 * @return Tripla con los objetos de serie, temporadas y capitulos rellenos con
	 *         los datos de TMDB
	 * @throws TMDBException
	 */
	// public Tripla<ShowBackend, Map<Integer, SeasonBackend>, List<ChapterBackend>>
	// getShowsOfFile(String k, Set<Serie> v)
	// throws TMDBException {
	// ShowBackend nuevaSerie = new ShowBackend(splitYearAndOther(k));
	// Map<Integer, SeasonBackend> listaTemporadas = new HashMap<>();
	// List<ChapterBackend> listaCapitulos = new ArrayList<ChapterBackend>();
	// List<Integer> temporadas = new ArrayList<>(20);
	// for (Serie iterator : v) {
	// if (!temporadas.contains(iterator.getTemporada())) {
	// SeasonBackend nueva = new SeasonBackend(iterator.getTemporada(), nuevaSerie);
	// temporadas.add(iterator.getTemporada());
	// listaTemporadas.put(iterator.getTemporada(), nueva);
	// }
	// }
	// for (Serie iterator : v) {
	// ChapterBackend nuevoCapitulo = new ChapterBackend(iterator.getCapitulo(),
	// null, nuevaSerie,
	// listaTemporadas.get(iterator.getTemporada()), iterator.getLink());
	// listaCapitulos.add(nuevoCapitulo);
	// }
	// return rellenarDeTMDB(nuevaSerie, listaTemporadas, listaCapitulos);
	// }

	// private Tripla<ShowBackend, Map<Integer, SeasonBackend>,
	// List<ChapterBackend>> rellenarDeTMDB(
	// ShowBackend nuevaSerie, Map<Integer, SeasonBackend> listaTemporadas,
	// List<ChapterBackend> listaCapitulos)
	// throws TMDBException {
	// log.debug("Se va a analizar la serie " + nuevaSerie.getNombreSerie());
	// Tripla<ShowBackend, Map<Integer, SeasonBackend>, List<ChapterBackend>> tripla
	// = new Tripla<>();
	// String uriSearchShows = TMDB_API_TV_SEARCH + TMDB_API_KEY +
	// TMDB_API_LANGUAGE_ES + "&query="
	// + nuevaSerie.getNombreSerie() + "&page=1";
	// log.debug("URL para buscar la serie " + nuevaSerie.getNombreSerie() + " : " +
	// uriSearchShows);
	// RestTemplate restTemplate = new RestTemplate();
	// Call1SeachTVShows results = restTemplate.getForObject(uriSearchShows,
	// Call1SeachTVShows.class);
	// boolean analizada = false;
	// for (Call1ResultsShows result : results.getResults()) {
	// log.debug("Se va a comparar la serie en el servidor " +
	// nuevaSerie.getNombreSerie()
	// + " con la de TMDB / NAME:" + result.getName() + " ** ORIGINAL NAME: " +
	// result.getOriginal_name());
	// if ((similarity(result.getName().replace(" ", ""),
	// nuevaSerie.getNombreSerie().replace(" ", "")) > 0.9
	// || similarity(result.getOriginal_name().replace(" ", ""),
	// nuevaSerie.getNombreSerie().replace(" ", "")) > 0.9
	// ||
	// nuevaSerie.getNombreSerie().toLowerCase().contains(result.getName().toLowerCase()))
	// && analizada == false) {
	// log.debug("Serie de TMDB analizando para añadir las imagenes, resumen,
	// año.");
	// nuevaSerie.setBackdrop_path(URI_PHOTOS_BACK_TMDB +
	// result.getBackdrop_path());
	// nuevaSerie.setPoster_path(URI_PHOTOS_FRONT_TMDB + result.getPoster_path());
	// nuevaSerie.setPuntuacion(result.getVote_average());
	// nuevaSerie.setYear(result.getFirst_air_date().substring(0, 4));
	// final String uriCreditsCall = TMDB_API_TV_INIT + result.getId() +
	// TMDB_CREDITS + TMDB_API_KEY;
	// Call4CreditsTVShows credits = restTemplate.getForObject(uriCreditsCall,
	// Call4CreditsTVShows.class);
	// credits.getCast().forEach((actores) ->
	// nuevaSerie.addActor(actores.getName()));

	// String uriSearchShowById = TMDB_API_TV_INIT + result.getId() + TMDB_API_KEY +
	// TMDB_API_LANGUAGE_ES;
	// RestTemplate restTemplate2 = new RestTemplate();
	// Call2ShowByID showResult = restTemplate2.getForObject(uriSearchShowById,
	// Call2ShowByID.class);
	// nuevaSerie.setEn_produccion(showResult.getIn_production());
	// showResult.getGenres().forEach((r) -> {
	// nuevaSerie.addGeneros(r.getName());
	// });
	// nuevaSerie.setResumen(showResult.getOverview());
	// log.debug("Serie " + nuevaSerie.getNombreSerie()
	// + " completada los campos comunes. Procedemos a los capitulos y temporadas");
	// for (Entry<Integer, SeasonBackend> chapterBackend :
	// listaTemporadas.entrySet()) {
	// log.debug(" Se va a añadir para la temporada " + chapterBackend.getKey()
	// + " la imagen para una serie que tiene en TMDB " +
	// showResult.getSeasons().size()
	// + " temporadas");
	// // SeasonBackend forImage= showResult.getSeasons().stream().filter(season->
	// // (season.getSeason_number()==chapterBackend.getKey()).);
	// chapterBackend.getValue()
	// .setPosterTemporada(URI_PHOTOS_FRONT_TMDB + showResult.getSeasons().stream()
	// .filter(season -> season.getSeason_number() == chapterBackend.getKey())
	// .collect(toSingleton()).getPoster_path());
	// log.debug("La imagen puesta a la temporada " + chapterBackend.getKey() + " ha
	// sido: "
	// + chapterBackend.getValue().getPosterTemporada());
	// // get(chapterBackend.getKey()).getPoster_path());
	// String uriSeason = TMDB_API_TV_INIT + result.getId() + "/season/"
	// + showResult.getSeasons().stream()
	// .filter(season -> season.getSeason_number() == chapterBackend.getKey())
	// .collect(toSingleton()).getSeason_number()
	// + TMDB_API_KEY + TMDB_API_LANGUAGE_ES;
	// log.debug("La URL para buscar la temporada es: " + uriSeason);
	// try {
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// RestTemplate restTemplate3 = new RestTemplate();
	// Call3SeasonsShowById seasonsCall = restTemplate3.getForObject(uriSeason,
	// Call3SeasonsShowById.class);
	// listaCapitulos.forEach((t) -> {
	// if (t.getTemporadaalaquepertenece().getNumeroTemporada() ==
	// seasonsCall.getSeason_number()) {
	// seasonsCall.getEpisodes().forEach((h) -> {
	// if (h.getEpisode_number() == t.getNumeroCapitulo()) {
	// t.setNombreCapitulo(h.getName());
	// t.setAir_date(h.getAir_date().substring(0, 4));
	// t.setResumen(h.getOverview());
	// t.setStill_path(URI_PHOTOS_STILL_TMDB + h.getStill_path());
	// t.setVote_average(h.getVote_average());
	// }
	// });
	// }
	// });
	// tripla.setT1(nuevaSerie);
	// tripla.setT2(listaTemporadas);
	// tripla.setT3(listaCapitulos);
	// }
	// analizada = true;
	// }
	// }
	// if (!analizada) {
	// log.info("No se ha encontrado la serie *" + nuevaSerie.getNombreSerie() + "
	// en TMDB");
	// throw new TMDBException(
	// "No se ha analizado la serie *" + nuevaSerie.getNombreSerie() + " en TMDB");
	// }
	// try {
	// Thread.sleep(600);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// if (tripla.getT1() == null || tripla.getT2() == null || tripla.getT3() ==
	// null) {
	// log.info("Serie que no se ha introducido: " + nuevaSerie.getNombreSerie());
	// throw new TMDBException("No se ha introducido la serie *" +
	// nuevaSerie.getNombreSerie());
	// } else {
	// return tripla;
	// }
	// }

	/**
	 * Calcula la diferencia entre 2 strings y lo devuelve en un double de 0 a 1.
	 * 
	 * @param s1 Primer String
	 * @param s2 Segundo String
	 * @return el % de simiilaridad que tienen los 2 strings
	 */
	private double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
		}
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}

	public static <T> Collector<T, ?, T> toSingleton() {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			if (list.size() != 1) {
				throw new IllegalStateException();
			}
			return list.get(0);
		});
	}

	/**
	 * Separa una serie : Cuerpo de elite (La Serie) -> Cuerpo de elite \\Frontier
	 * (2016) -> Frontier
	 * 
	 * @param k
	 * @return
	 */
	private String splitYearAndOther(String k) {
		log.debug("Dividiendo serie " + k);
		Pattern pattern = Pattern.compile("(.*)\\ \\((\\d{4})\\)");
		Pattern pattern2 = Pattern.compile("(.*)\\ \\((.*)\\)");
		Matcher generalMatcher = pattern.matcher(k);
		Matcher generalMatcher2 = pattern2.matcher(k);
		if (generalMatcher.find()) {
			return generalMatcher.group(1);
		} else if (generalMatcher2.find()) {
			return generalMatcher2.group(1);
		} else {
			return k;
		}
	}

	// /**
	// * Una lista de capitulos es pasada a Map de tipo <String(NombreSerie),Series>
	// *
	// * @param series con la temporada el capitulo y el nombre de la serie
	// * @return
	// */
	// private Map<String, Set<Serie>> listToMapShows(Set<Serie> series) {
	// Map<String, Set<Serie>> seri = new HashMap<>();
	// for (Serie iterator : series) {
	// if (seri.containsKey(iterator.getNombreSerie())) {
	// // La serie ya existia, la sacamos y añadimos la temporada y el
	// // capitulo
	// Set<Serie> s = seri.get(iterator.getNombreSerie());
	// s.add(iterator);
	// seri.put(iterator.getNombreSerie(), s);
	// } else {
	// // No existia, la metemos al Set
	// Set<Serie> nueva = new HashSet<>();
	// nueva.add(iterator);
	// seri.put(iterator.getNombreSerie(), nueva);
	// }
	// }
	// return seri;
	// }

	private int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				} else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						}
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0) {
				costs[s2.length()] = lastValue;
			}
		}
		return costs[s2.length()];
	}

	@Override
	public void analyze(TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO) throws TMDBException {
		TMDBStrategy optStrategy;
		if (TelegramFilebotExecutionIDTO.Type.FILM.equals(telegramFilebotExecutionIDTO.getType())) {
			optStrategy = tmdbStrategyFilm;
		} else {
			optStrategy = tmdbStrategyShow;
		}
		optStrategy.execute(telegramFilebotExecutionIDTO);
	}

}
