import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FetchService} from './fetch.service';
import {Events} from '../models/main.model';

@Injectable({providedIn: 'root'})
export class EventsService {

  constructor(private fetchService: FetchService) {}

  getEvents(){
    const params = new HttpParams();
    return this.fetchService.fetch<Events>('http://localhost:8080/api/events','Events not found',params);
  }
}
