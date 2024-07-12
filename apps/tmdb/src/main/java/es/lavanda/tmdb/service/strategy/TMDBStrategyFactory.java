package es.lavanda.tmdb.service.strategy;

import java.util.Optional;

import es.lavanda.lib.common.model.MediaIDTO.Type;

public interface TMDBStrategyFactory {

    Optional<TMDBStrategy> getFactory(String torrentTitle, Type type);

}
