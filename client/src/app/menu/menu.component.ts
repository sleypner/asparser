import {Component} from '@angular/core';

export interface MenuItem {
  title: string;
  link: string;
}
@Component({
  selector: 'app-menu',
  imports: [],
  templateUrl: './menu.component.html',
  standalone: true,
  styleUrl: './menu.component.css'
})
export class MenuComponent {
  menu: MenuItem[] = [
    {
      title: "Home",
      link: "/",
    },
    {
      title: "News",
      link: "/news",
    },
    {
      title: "Online",
      link: "/online",
    },
    {
      title: "Fortress",
      link: "/fortress",
    },
    {
      title: "Fortress history",
      link: "/fortress-history",
    },
    {
      title: "Raid Bosses",
      link: "/raid-bosses",
    },
    {
      title: "Events",
      link: "/events",
    },
    {
      title: "Roulette",
      link: "/roulette",
    },

  ];
}
