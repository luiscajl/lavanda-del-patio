package es.lavanda.tmdb.service.impl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;

@Service
@FeignClient(value = "themoviedbFilm", url = "https://api.themoviedb.org/3/")
public interface TMDBServiceFilm {

    @GetMapping(produces = "application/json", value = "search/movie")
    TMDBSearchDTO searchFilm(@RequestParam(name = "query") String query,
            @RequestParam(name = "year") int year);

    @GetMapping(produces = "application/json", value = "search/movie")
    TMDBSearchDTO searchFilm(@RequestParam(name = "query") String query);

}
