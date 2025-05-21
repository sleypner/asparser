import {Component, DestroyRef, OnInit, signal} from '@angular/core';
import {RaidBosses} from '../../models/main.model';
import {BossesService} from '../../services/bosses.service';

@Component({
  selector: 'app-bosses',
  imports: [],
  templateUrl: './bosses.component.html',
  standalone: true,
  styleUrl: './bosses.component.css'
})
export class BossesComponent implements OnInit{
  bosses = signal<RaidBosses[] | undefined>(undefined);
  isFetching = signal(false);
  error = signal('');

  constructor(private bossesService: BossesService, private destroyRef: DestroyRef) {
  }

  ngOnInit() {
    this.isFetching.set(true);
    const subscription = this.bossesService.getRaidBosses()
      .subscribe({
        next: (bosses) => {
          this.bosses.set(bosses);
        },
        error: (err) => {
          console.log(err);
          this.error.set(err.message);
        },
        complete: () => {
          this.isFetching.set(false);
        }
      });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
