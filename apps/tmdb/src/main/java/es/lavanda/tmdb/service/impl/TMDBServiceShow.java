package es.lavanda.tmdb.service.impl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;

@Service
@FeignClient(value = "themoviedbShow", url = "https://api.themoviedb.org/3/")
public interface TMDBServiceShow {

    @GetMapping(produces = "application/json", value = "search/tv")
    TMDBSearchDTO searchShow(@RequestParam(name = "query") String query,
            @RequestParam(name = "first_air_date_year", required = false) Integer year);

}
