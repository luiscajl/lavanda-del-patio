package es.lavanda.downloader.bt4g.service;

import es.lavanda.downloader.bt4g.model.Bt4g;
import es.lavanda.downloader.bt4g.model.Search;
import es.lavanda.downloader.bt4g.repository.Bt4gRepository;
import es.lavanda.downloader.bt4g.repository.SearchRepository;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        bt4gRepository.saveAll(bt4gs);
        return bt4gs.stream().map(bt4g -> modelMapper.map(bt4g, Bt4gDTO.class)).collect(Collectors.toList());
    }

    public void searchBatch(String name) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                Search search = new Search();
                search.setName(name);
                search = searchRepository.save(search);
                List<Bt4g> bt4gs = bt4gCallerService.callToBT4G(search.getName());
                search.setFinished(true);
                searchRepository.save(search);
                bt4gRepository.saveAll(bt4gs);
            });
            executor.shutdown();
        } catch (Exception e) {
            log.error("Error on searchBatch. Going to delete search");
            searchRepository.deleteByName(name);
        }
    }

    public List<Search> getAllSearch() {
        return searchRepository.findAll();
    }

    public void deleteSearch(String id) {
        searchRepository.deleteById(id);
    }

    public Page<Bt4gDTO> getAllPageable(Pageable pageable, String name) {
        Page<Bt4g> bt4gs = null;
        if (Objects.nonNull(name)) {
            bt4gs = bt4gRepository.findByNameContainingIgnoreCase(pageable, name);
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
