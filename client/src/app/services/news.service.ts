import {News} from '../models/main.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {FetchService} from './fetch.service';

@Injectable({
  providedIn: 'root',
})
export class NewsService {

  constructor(private fetchService: FetchService) {
  }

  getNews(): Observable<News[]> {
    const params = new HttpParams().append('number','0');
    return this.fetchService.fetch<News>('http://localhost:8080/api/articles','Articles not found',params);
  }
}
