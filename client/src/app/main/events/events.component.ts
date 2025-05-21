import {Component, DestroyRef, OnInit, signal} from '@angular/core';
import {Events} from '../../models/main.model';
import {EventsService} from '../../services/events.service';

@Component({
  selector: 'app-events',
  imports: [],
  templateUrl: './events.component.html',
  standalone: true,
  styleUrl: './events.component.css'
})
export class EventsComponent implements OnInit{
  events = signal<Events[] | undefined>(undefined);
  isFetching = signal(false);
  error = signal('');

  constructor(private eventsServices: EventsService, private destroyRef: DestroyRef) {
  }

  ngOnInit() {
    this.isFetching.set(true);
    const subscription = this.eventsServices.getEvents()
      .subscribe({
        next: (events) => {
          this.events.set(events);
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
