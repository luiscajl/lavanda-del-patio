import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';
import { FilebotExecutor } from '../api/filebot-executor.model';
import { Qbittorrent } from '../api/qbittorrent.model';
import { Bt4g } from '../api/bt4g.model';



@Injectable({ providedIn: 'root' })
export class Wolfmax4kService {
  constructor(private httpClient: HttpClient) { }


  getAllByPageable(page: number, pageSize: number, type: string, quality: string, name?: string): Observable<Pageable> {
    let params = new HttpParams();
    params = params.set('size', pageSize);
    params = params.set('page', page);
    if (name) {
      params = params.set('name', name);
    }
    return this.httpClient.get<Pageable>(environment.apiUrl + 'indexer/wolfmax4k/' + type + '/' + quality, { params });
  }
}
