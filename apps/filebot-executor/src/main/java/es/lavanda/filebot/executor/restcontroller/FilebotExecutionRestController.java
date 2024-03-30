package es.lavanda.filebot.executor.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.lavanda.filebot.executor.model.FilebotExecution;
import es.lavanda.filebot.executor.model.QbittorrentModel;
import es.lavanda.filebot.executor.service.FilebotExecutionService;
import es.lavanda.filebot.executor.service.FilebotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filebot-executor")
@Slf4j
@CrossOrigin(allowedHeaders = "*", origins = { "http://localhost:4200", "https://lavandadelpatio.es",
        "https://pre.lavandadelpatio.es" }, allowCredentials = "true", exposedHeaders = "*", methods = {
                RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST,
                RequestMethod.PUT }, originPatterns = { "*" })
public class FilebotExecutionRestController {

    @Autowired
    private FilebotExecutionService filebotExecutorService;

    @GetMapping
    public ResponseEntity<Page<FilebotExecution>> getAll(Pageable pageable,
            @RequestParam(required = false) String status, @RequestParam(required = false) String path) {
        return ResponseEntity.ok(filebotExecutorService.getAllPageable(pageable, status, path));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilebotExecution> getById(@PathVariable String id) {
        return ResponseEntity.ok(filebotExecutorService.getById(id));
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getListOfPaths() {
        return ResponseEntity.ok(filebotExecutorService.getAllFiles());
    }

    @PostMapping
    public ResponseEntity<FilebotExecution> createNewExecution(@RequestBody QbittorrentModel qbittorrentModel) {
        log.info("Received qbittorrentModel: {}", qbittorrentModel);
        filebotExecutorService.createNewExecution(qbittorrentModel);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/execute")
    public ResponseEntity<?> execute() {
        log.info("Manual execution");
        filebotExecutorService.forceExecute();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/execute/{id}")
    public ResponseEntity<?> execute(@PathVariable String id) {
        log.info("Manual execution for ID: {}", id);
        filebotExecutorService.forceExecute(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FilebotExecution> editExecution(@PathVariable String id,
            @RequestBody FilebotExecution filebotExecution) {
        log.info("Received edit for ID: {}", id);
        return ResponseEntity.ok(filebotExecutorService.editExecution(id, filebotExecution));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Received delete for ID: {}", id);
        filebotExecutorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
