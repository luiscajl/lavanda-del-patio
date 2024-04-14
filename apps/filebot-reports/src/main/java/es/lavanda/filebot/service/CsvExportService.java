package es.lavanda.filebot.service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.model.Filebot;
import es.lavanda.filebot.repository.FilebotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvExportService {


    private final FilebotRepository filebotRepository;


    public void writeFilebotToCSV(Writer writer) {
        List<Filebot> filebots = filebotRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Filebot employee : filebots) {
                csvPrinter.printRecord(employee.getId(), employee.getOriginalName(), employee.getNewName(), employee.getNewLocation(), employee.getLastModifiedAt());
            }
        } catch (IOException e) {
            log.error("Error While writing CSV ", e);
        }
    }
}