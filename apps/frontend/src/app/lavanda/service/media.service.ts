import { environment } from './../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MediaFile, MediaPath } from '../api/media.model';

@Injectable({ providedIn: 'root' })
export class MediaService {



  constructor(private httpClient: HttpClient) { }


  getAllByPageable(page: number, pageSize: number, path?: string, status?: string): Observable<MediaPath[]> {
    if (environment.testing) {
      return this.httpClient.get<MediaPath[]>('assets/lavanda/data/dummy-media-short.json');

    }
    else {
      let params = new HttpParams();
      // ?page = ' + page + ' & size=' + pageSize + ' & path=' + path + ' & status=' + status
      params = params.set('pageSize', pageSize);
      params = params.set('page', page);

      if (path) {
        params = params.set('path', path);
      }
      if (status) {
        params = params.set('status', status);
      }

      return this.httpClient.get<MediaPath[]>(environment.apiUrl + 'media', { params });
    }
  }

  sendToSync(mediaSyncFile: MediaFile, mediaSyncPath: MediaPath, provider: string): Observable<MediaFile> {

    let params = new HttpParams();
    // ?page = ' + page + ' & size=' + pageSize + ' & path=' + path + ' & status=' + status
    // params = params.set('pageSize', pageSize);
    // params = params.set('page', page);

    return this.httpClient.post<MediaPath>(environment.apiUrl + 'media/sync/' + mediaSyncPath.id + '/' + mediaSyncFile.id + '/' + provider, { params });
  }

  forceAnalysis(): Observable<any> {
    let params = new HttpParams();
    // ?page = ' + page + ' & size=' + pageSize + ' & path=' + path + ' & status=' + status
    // params = params.set('pageSize', pageSize);
    // params = params.set('page', page);

    return this.httpClient.post<MediaPath>(environment.apiUrl + 'media', { params });
  }

  // getAllMediaByPageable(page: number, size: number): Observable<any> {
  //     return this.httpClient.get<any>(environment.apiUrl + 'media' + '?page=' + String(page) + '&size=' + size);
  // }

  // getTranscodes(flatMediaId: string): Observable<TranscodeMedia[]> {
  //     return this.httpClient.get<TranscodeMedia[]>(environment.apiUrl + 'media/' + flatMediaId + '/transcodes');
  // }

  // getTranscodesByType(type: string): Observable<TranscodeApi> {
  //     return this.httpClient.get<TranscodeApi>(environment.apiUrl + 'transcodes/' + type);
  // }

  // createTranscodes(flatMediId: string, transcodes: Transcode[]): Observable<any> {
  //     return this.httpClient.post(environment.apiUrl + 'transcodes/' + flatMediId, transcodes);
  // }

  // downloadById(id: string) {
  //     const url = 'https://files.lavandadelpatio.es/' + 'files' + '/' + id;
  //     //window.open(url);
  // }

  // watchById(id: string): string {
  //     return 'https://files.lavandadelpatio.es/' + 'files/player/' + id;
  // }

  // deleteFlat(id: string): Observable<any> {
  //     const url = environment.apiUrl + 'media/' + id;
  //     return this.httpClient.delete(url);
  // }
  // deleteTranscode(id: string): Observable<any> {
  //     const url = environment.apiUrl + 'media/transcodes/' + id;
  //     return this.httpClient.delete(url);
  // }
}
