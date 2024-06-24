package es.lavanda.indexers.controller;

import es.lavanda.indexers.model.Index;
import es.lavanda.indexers.service.Wolfmax4KCallerServiceChatGPTVersion;
import es.lavanda.indexers.service.Wolfmax4kCallerService;
import es.lavanda.indexers.service.Wolfmax4kService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/indexer")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = {"http://localhost:4200", "https://lavandadelpatio.es",
        "https://pre.lavandadelpatio.es"}, allowCredentials = "true", exposedHeaders = "*", methods = {
        RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST,
        RequestMethod.PUT}, originPatterns = {"*"})
public class IndexerController {

    private final Wolfmax4kService wolfmax4kService;

    private final Wolfmax4kCallerService wolfmax4kCallerService;
    private final Wolfmax4KCallerServiceChatGPTVersion wolfmax4KCallerServiceChatGPTVersion;

    @GetMapping("/wolfmax4k/{type}/{quality}")
    public ResponseEntity<?> getAll(@PathVariable("type") Index.Type type, @PathVariable("quality") Index.Quality quality, @RequestParam(required = false) String name, Pageable pageable) {
        try {
            return ResponseEntity.ok(wolfmax4kService.getAllPageable(pageable, type, quality, name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Not found type or quality on this indexer");
        }
    }

    @PostMapping
    public ResponseEntity<?> execute() {
        wolfmax4KCallerServiceChatGPTVersion.getIndexForMainPage();
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/dontorrent/{type}")
//    public ResponseEntity<Page<Index>> getAll(Pageable pageable, @PathVariable("type") DonTorrentType type) {
//        wolfmax4kService.getAllPageable(pageable, type);
//        return ResponseEntity.ok();
//    }

}
