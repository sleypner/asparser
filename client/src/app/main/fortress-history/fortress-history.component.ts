import {Component, DestroyRef, OnInit, signal} from '@angular/core';
import {FortressTable} from '../../models/main.model';
import {FortressService} from '../../services/fortress.service';

@Component({
  selector: 'app-fortress-history',
  imports: [],
  templateUrl: './fortress-history.component.html',
  standalone: true,
  styleUrl: './fortress-history.component.css'
})
export class FortressHistoryComponent implements OnInit{
  fortress = signal<FortressTable[] | undefined>(undefined);
  isFetching = signal(false);
  error = signal('');

  constructor(private fortressService: FortressService, private destroyRef: DestroyRef) {
  }

  ngOnInit() {
    this.isFetching.set(true);
    const subscription = this.fortressService.getFortress().subscribe({
      next:(fortress) => {
        this.fortress.set(fortress);
      },
      error: (err) => {
        this.error.set(err);
        console.error(err.message);
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
