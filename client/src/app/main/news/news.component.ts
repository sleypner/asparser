import {Component, DestroyRef, OnInit, signal} from '@angular/core';
import {News} from '../../models/main.model';
import {NewsService} from '../../services/news.service';

@Component({
  selector: 'app-news',
  imports: [],
  templateUrl: './news.component.html',
  standalone: true,
  styleUrl: './news.component.css'
})
export class NewsComponent implements OnInit{
  news = signal<News[] | undefined>(undefined);
  isFetching = signal(false);
  error = signal('');

  constructor(private newsService: NewsService, private destroyRef: DestroyRef) {
  }

  ngOnInit() {
    this.isFetching.set(true);
    const subscription = this.newsService.getNews()
      .subscribe({
        next: (news) => {
          this.news.set(news);
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
