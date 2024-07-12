import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

  model: any[] = [];

  constructor(public layoutService: LayoutService) {
  }

  ngOnInit() {
    this.model = [
      {
        label: 'Home',
        items: [
          { label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/'] }
        ]
      },
      {
        label: 'Administration',
        items: [
          { label: 'Executor', icon: 'pi pi-fw pi-flag-fill', routerLink: ['/admin/executors'] },
        ]
      },
      {
        label: 'Bt4g',
        items: [
          { label: 'Saved on Database', icon: 'pi pi-fw pi-database', routerLink: ['/bt4g'] },
          { label: 'Search on Bt4g Web', icon: 'pi pi-fw pi-search', routerLink: ['/bt4g/search'] },
        ]
      },
      {
        label: 'Wolfmax4k',
        items: [
          { label: 'Shows 720p', icon: 'pi pi-fw pi-arrow-right', routerLink: ['/wolfmax4k/shows/720p'] },
          { label: 'Shows 1080p', icon: 'pi pi-fw pi-arrow-right', routerLink: ['/wolfmax4k/shows/1080p'] },
          { label: 'Shows 2160p', icon: 'pi pi-fw pi-arrow-right', routerLink: ['/wolfmax4k/shows/2160p'] },
          { label: 'Films 1080p', icon: 'pi pi-fw pi-arrow-right', routerLink: ['/wolfmax4k/films/1080p'] },
          { label: 'Films 2160p', icon: 'pi pi-fw pi-arrow-right', routerLink: ['/wolfmax4k/films/2160p'] },
        ]
      },
    ];
  }
}
