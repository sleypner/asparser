import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, map, throwError} from 'rxjs';

@Injectable({providedIn: 'root'})
export class FetchService {

  constructor(private http: HttpClient) {
  }

  public fetch<T>(url: string, errorMessage: string, params?: HttpParams) {
    return this.http
      .get<Array<T>>(url, {params})
      .pipe(
        map(data => data),
        catchError((err) => {
          console.log(err);
          return throwError(() => new Error(errorMessage));
        }),
      );
  }
}
