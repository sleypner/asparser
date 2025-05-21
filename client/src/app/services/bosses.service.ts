import {Injectable} from '@angular/core';
import {FetchService} from './fetch.service';
import {HttpParams} from '@angular/common/http';
import {Events, RaidBosses} from '../models/main.model';

@Injectable({providedIn: 'root'})
export class BossesService {

  constructor(private fetchService: FetchService) {
  }
  getRaidBosses(){
    const params = new HttpParams();
    return this.fetchService.fetch<RaidBosses>('http://localhost:8080/api/raid-bosses','Raid bosses not found',params);
  }
}
