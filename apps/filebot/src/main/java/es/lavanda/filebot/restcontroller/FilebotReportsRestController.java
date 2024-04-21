package es.lavanda.filebot.restcontroller;

import java.io.IOException;


import es.lavanda.filebot.service.impl.CsvExportService;
import es.lavanda.filebot.service.impl.FilebotReportsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.lavanda.filebot.model.Filebot;
import es.lavanda.filebot.service.FilebotService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = { "http://localhost:4200", "https://lavandadelpatio.es",
        "https://pre.lavandadelpatio.es" }, allowCredentials = "true", exposedHeaders = "*", methods = {
                RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST,
                RequestMethod.PUT }, originPatterns = {})
@RequestMapping("/filebot")
public class FilebotReportsRestController {

    private final FilebotReportsService filebotReportsService;

    private final CsvExportService csvExportService;

    @GetMapping
    public ResponseEntity<Page<Filebot>> getAll(Pageable pageable) {
        return ResponseEntity.ok(filebotReportsService.getAllPageable(pageable));
    }

    @GetMapping("/csv")
    public void getAllEmployeesInCsv(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"employees.csv\"");
        csvExportService.writeFilebotToCSV(servletResponse.getWriter());
    }

}
