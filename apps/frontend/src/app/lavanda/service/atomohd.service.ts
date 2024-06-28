import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Pageable } from '../api/pageable.model';

@Injectable({ providedIn: 'root' })
export class AtomohdService {
    constructor(private httpClient: HttpClient) { }


    getAllByPageable(page: number, pageSize: number): Observable<Pageable> {
        return this.httpClient.get<Pageable>(environment.apiUrl + 'atomohd?page=' + page + '&size=' + pageSize);
    }

}
