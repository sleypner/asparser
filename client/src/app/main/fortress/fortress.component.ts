import {Component, DestroyRef, OnInit, signal} from '@angular/core';
import {FortressTable} from '../../models/main.model';
import {FortressService} from '../../services/fortress.service';

@Component({
  selector: 'app-fortress',
  imports: [],
  templateUrl: './fortress.component.html',
  standalone: true,
  styleUrl: './fortress.component.css'
})
export class FortressComponent implements OnInit {
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
        console.log(err.message);
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
