package es.lavanda.downloader.bt4g.service;

import com.mongodb.MongoBulkWriteException;
import es.lavanda.downloader.bt4g.model.Bt4g;
import es.lavanda.downloader.bt4g.model.Search;
import es.lavanda.downloader.bt4g.repository.Bt4gRepository;
import es.lavanda.downloader.bt4g.repository.SearchRepository;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Bt4gService {

    private final Bt4gRepository bt4gRepository;

    private final SearchRepository searchRepository;

    private final Bt4gCallerService bt4gCallerService;

    private final MagnetService magnetService;

    private final ModelMapper modelMapper = new ModelMapper();

    public List<Bt4gDTO> search(String search) {
        List<Bt4g> bt4gs = bt4gCallerService.callToBT4G(search);
        bt4gs.forEach(this::save);
        return bt4gs.stream().map(bt4g -> modelMapper.map(bt4g, Bt4gDTO.class)).collect(Collectors.toList());
    }

    public void searchBatch(String name) {
        Search search = new Search();
        search.setName(name);
        searchRepository.save(search);
        CompletableFuture.runAsync(() -> {
            try {
                Search searchAlternative = searchRepository.findByName(name);
                List<Bt4g> bt4gs = bt4gCallerService.callToBT4G(name);
                List<Bt4g> bt4gsSaved = new ArrayList<>();
                bt4gs.forEach(bt4g -> {
                    Bt4g savedBt4g = save(bt4g);
                    if (Objects.nonNull(savedBt4g)) {
                        bt4gsSaved.add(savedBt4g);
                    } else {
                        bt4gsSaved.add(bt4gRepository.findByName(bt4g.getName()));
                    }
                });
                searchAlternative.setFinished(true);
                searchAlternative.setBt4gIds(bt4gsSaved.stream().map(Bt4g::getId).collect(Collectors.toList()));
                searchRepository.save(searchAlternative);
            } catch (Exception e) {
                log.error("Error on searchBatch. Going to delete search", e);
                searchRepository.deleteByName(name);
            }
        });
    }

    private Bt4g save(Bt4g bt4g) {
        try {
            return bt4gRepository.save(bt4g);
        } catch (DuplicateKeyException exception) {
            log.error("Can't write to database: ", exception);
            return null;
        }
    }

    public List<Search> getAllSearch() {
        return searchRepository.findAll();
    }

    public void deleteSearch(String id) {
        searchRepository.deleteById(id);
    }

    public Page<Bt4gDTO> getAllPageable(Pageable pageable, String name, Boolean searchList) {
        Page<Bt4g> bt4gs = null;
        if (Objects.nonNull(name) && Boolean.FALSE.equals(searchList)) {
            bt4gs = bt4gRepository.findByNameContainingIgnoreCase(pageable, name);
        } else if (Boolean.TRUE.equals(searchList)) {
            Search search = searchRepository.findByName(name);
            bt4gs = bt4gRepository.findByIdIn(pageable, search.getBt4gIds());
        } else {
            bt4gs = bt4gRepository.findAll(pageable);
        }
        return bt4gs.map(bt4g -> modelMapper.map(bt4g, Bt4gDTO.class));
    }

    private String getLatestMagnet(String hashMagnet, String name) {
        return magnetService.getMagnetWithTrackers(hashMagnet, name);
    }

    public void updateMagnetLink() {
        List<Bt4g> all = bt4gRepository.findAll();
        for (Bt4g bt4g : all) {
            bt4g.setMagnet(getLatestMagnet(bt4g.getMagnetHash(), bt4g.getName()));
            bt4gRepository.save(bt4g);
        }
    }

    public void updateToDownloaded(String id) {
        bt4gRepository.findById(id).ifPresent(bt4g -> {
            bt4g.setDownloaded(true);
            bt4gRepository.save(bt4g);
        });
    }
}
