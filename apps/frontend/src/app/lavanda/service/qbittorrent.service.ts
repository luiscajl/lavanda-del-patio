import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';

@Injectable({ providedIn: 'root' })
export class QbittorrentService {
  constructor(private httpClient: HttpClient) {
  }

  addTorrent(magnet: string): Observable<any> {
    let body = { "url": magnet }
    return this.httpClient.post<any>(environment.apiUrl + 'qbittorrent/add', body);
  }

}
