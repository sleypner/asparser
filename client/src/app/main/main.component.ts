import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-main',
  imports: [
    RouterOutlet
  ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.css'
})
export class MainComponent {

}
