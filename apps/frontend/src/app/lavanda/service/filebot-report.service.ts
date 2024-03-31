import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';

@Injectable({ providedIn: 'root' })
export class FilebotReportService {
    constructor(private httpClient: HttpClient) {
    }

    getAllByPageable(page: number, pageSize: number): Observable<Pageable> {
        if (environment.testing) {
            return this.httpClient.get<Pageable>('assets/lavanda/data/dummy-filebot-reports.json');
        }
        else {
            return this.httpClient.get<Pageable>(environment.apiUrl + 'filebot?page=' + page + '&size=' + pageSize);
        }
    }
}
