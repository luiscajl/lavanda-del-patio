package es.lavanda.downloader.bt4g.controller;

import es.lavanda.downloader.bt4g.model.Search;
import es.lavanda.downloader.bt4g.model.SearchIDTO;
import es.lavanda.downloader.bt4g.service.Bt4gService;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bt4g")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = {"http://localhost:4200", "https://lavandadelpatio.es",
        "https://pre.lavandadelpatio.es"}, allowCredentials = "true", exposedHeaders = "*", methods = {
        RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST,
        RequestMethod.PUT}, originPatterns = {"*"})
public class Bt4gController {

    private final Bt4gService bt4gService;

    @GetMapping
    public ResponseEntity<Page<Bt4gDTO>> getAll(Pageable pageable, @RequestParam(required = false) String name, @RequestParam(required = false) Boolean searchList) {
        return ResponseEntity.ok(bt4gService.getAllPageable(pageable, name, searchList));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Search>> getAll() {
        return ResponseEntity.ok(bt4gService.getAllSearch());
    }

    @PostMapping("/search")
    public ResponseEntity<List<Bt4gDTO>> search(@RequestBody SearchIDTO search) {
        return ResponseEntity.ok(bt4gService.search(search.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBt4gToDownloaded(@PathVariable String id) {
        bt4gService.updateToDownloaded(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search/batch")
    public ResponseEntity<Void> searchBatch(@RequestBody SearchIDTO search) {
        bt4gService.searchBatch(search.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/search/{id}")
    public ResponseEntity<Void> deleteSearch(@PathVariable String id) {
        bt4gService.deleteSearch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
