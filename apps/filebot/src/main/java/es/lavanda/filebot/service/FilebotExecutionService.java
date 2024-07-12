package es.lavanda.filebot.service;

import java.util.List;

import es.lavanda.filebot.model.FilebotExecution;
import es.lavanda.filebot.model.QbittorrentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.lavanda.lib.common.model.FilebotExecutionODTO;
import es.lavanda.lib.common.model.FilebotExecutionTestODTO;

public interface FilebotExecutionService {

  Page<FilebotExecution> getAllPageable(Pageable pageable, String status, String name);

  void delete(String id);

  FilebotExecution save(FilebotExecution filebotExecution);

  FilebotExecution getById(String id);

  FilebotExecution editExecution(String id, FilebotExecution filebotExecution);

  List<String> getAllFiles();

  void checkPossiblesNewFilebotExecution();

  void resolutionTelegramBot(FilebotExecutionODTO filebotExecutionODTO);

  void resolutionTelegramBotTest(FilebotExecutionTestODTO filebotExecutionTestODTO);

  void createNewExecution(QbittorrentModel qbittorrentModel);

  void forceExecute();

  void forceExecute(String id);

}
