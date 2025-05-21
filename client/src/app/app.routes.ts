import { Routes } from '@angular/router';
import {NewsStartComponent} from './main/news-start/news-start.component';
import {NewsComponent} from './main/news/news.component';
import {FortressComponent} from './main/fortress/fortress.component';
import {FortressHistoryComponent} from './main/fortress-history/fortress-history.component';
import {BossesComponent} from './main/bosses/bosses.component';
import {EventsComponent} from './main/events/events.component';
import {RouletteComponent} from './main/roulette/roulette.component';

export const routes: Routes = [
  {
    path: '',
    component: NewsStartComponent,
  },
  {
    path: 'news',
    component: NewsComponent
  },
  {
    path: 'fortress',
    component: FortressComponent
  },
  {
    path: 'fortress-history',
    component: FortressHistoryComponent
  },
  {
    path: 'raid-bosses',
    component: BossesComponent
  },
  {
    path: 'events',
    component:EventsComponent
  },
  {
    path: 'roulette',
    component: RouletteComponent
  }
];
