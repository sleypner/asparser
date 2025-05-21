import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, Observable, throwError} from 'rxjs';
import {FortressTable} from '../models/main.model';
import {FetchService} from './fetch.service';

@Injectable({providedIn: 'root'})
export class FortressService{
  constructor(private fetchService: FetchService) {
  }

  getFortress(): Observable<FortressTable[]> {
    const params = new HttpParams();
    return this.fetchService.fetch<FortressTable>('http://localhost:8080/api/fortress','Fortress not found', params);
  }
  getFortressHistory(): Observable<FortressTable[]> {
    const params = new HttpParams();
    return this.fetchService.fetch<FortressTable>('http://localhost:8080/api/fortress-history','FortressHistory not found', params);
  }
}
