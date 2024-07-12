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
    }


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
