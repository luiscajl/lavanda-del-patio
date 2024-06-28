import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';
import { FilebotExecutor } from '../api/filebot-executor.model';
import { Qbittorrent } from '../api/qbittorrent.model';

@Injectable({ providedIn: 'root' })
export class FilebotExecutorService {
  constructor(private httpClient: HttpClient) { }


  getAllByPageable(page: number, pageSize: number, path?: string, status?: string): Observable<Pageable> {
    if (environment.testing) {
      return this.httpClient.get<Pageable>('assets/lavanda/data/dummy-filebot-executor.json');

    }
    else {
      let params = new HttpParams();
      // ?page = ' + page + ' & size=' + pageSize + ' & path=' + path + ' & status=' + status
      params = params.set('size', pageSize);
      params = params.set('page', page);

      if (path) {
        params = params.set('path', path);
      }
      if (status) {
        params = params.set('status', status);
      }
      return this.httpClient.get<Pageable>(environment.apiUrl + 'filebot-executor', { params });
    }
  }


  editFilebotExecutor(id: string, filebotExecutor: FilebotExecutor): Observable<FilebotExecutor> {
    return this.httpClient.patch<FilebotExecutor>(environment.apiUrl + 'filebot-executor/' + id, filebotExecutor);
  }

  delete(id: string): Observable<any> {
    return this.httpClient.delete(environment.apiUrl + 'filebot-executor/' + id);
  }

  getAllFilebotExecutor(): Observable<string[]> {
    if (environment.testing) {
      return this.httpClient.get<string[]>('assets/lavanda/data/dummy-filebot-executor-files.json');
    }
    else {
      return this.httpClient.get<string[]>(environment.apiUrl + 'filebot-executor/files');
    }
  }

  createQbittorrent(qbittorrent: Qbittorrent): Observable<Qbittorrent> {
    return this.httpClient.post<Qbittorrent>(environment.apiUrl + 'filebot-executor', qbittorrent);
  }

  reExecutionAll(): Observable<Qbittorrent> {
    return this.httpClient.post<Qbittorrent>(environment.apiUrl + 'filebot-executor/execute', null);
  }


  reExecution(id: string): Observable<Qbittorrent> {
    return this.httpClient.post<Qbittorrent>(environment.apiUrl + 'filebot-executor/execute/' + id, null);
  }

  downloadLog(data: string) {
    const blob = new Blob([data], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    window.open(url);
  }
}
