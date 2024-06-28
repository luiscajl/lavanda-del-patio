import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';
import { FilebotExecutor } from '../api/filebot-executor.model';
import { Qbittorrent } from '../api/qbittorrent.model';
import { Bt4g } from '../api/bt4g.model';
import { Search } from '../api/search.model';



@Injectable({ providedIn: 'root' })
export class Bt4gService {
  constructor(private httpClient: HttpClient) { }


  getAllByPageable(page: number, pageSize: number, name?: string, searchList?: boolean): Observable<Pageable> {
    let params = new HttpParams();
    params = params.set('size', pageSize);
    params = params.set('page', page);
    if (name) {
      params = params.set('name', name);
    }
    if (searchList) {
      params = params.set('searchList', searchList);
    }
    return this.httpClient.get<Pageable>(environment.apiUrl + 'bt4g', { params });
  }

  getAllSearch(): Observable<Search[]> {
    return this.httpClient.get<Search[]>(environment.apiUrl + 'bt4g/search');
  }

  deleteBt4gSearch(id: string): Observable<any> {
    return this.httpClient.delete<any>(environment.apiUrl + 'bt4g/search/' + id);
  }

  search(name: string): Observable<Bt4g[]> {
    let bt4g = { name }
    return this.httpClient.post<Bt4g[]>(environment.apiUrl + 'bt4g/search', bt4g);
  }

  searchBatch(name: string): Observable<any> {
    let bt4g = { name }
    return this.httpClient.post<any>(environment.apiUrl + 'bt4g/search/batch', bt4g);
  }

  updateToDownloaded(id: string): Observable<any> {
    return this.httpClient.put<any>(environment.apiUrl + 'bt4g/' + id, {});
  }

}
