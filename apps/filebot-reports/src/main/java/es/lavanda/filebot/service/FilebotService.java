package es.lavanda.filebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.model.Filebot;
import es.lavanda.filebot.repository.FilebotRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FilebotService {

    @Autowired
    private FilebotRepository filebotRepository;

    public Page<Filebot> getAllPageable(Pageable pageable) {
        log.info("getAllPageable");
        return filebotRepository.findAllByOrderByLastModifiedAtDesc(pageable);
    }

}