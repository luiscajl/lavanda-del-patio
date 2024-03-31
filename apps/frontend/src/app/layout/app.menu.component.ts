import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';

@Component({
    selector: 'app-menu',
    templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

    model: any[] = [];

    constructor(public layoutService: LayoutService) { }

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
                    { label: 'Reports', icon: 'pi pi-fw pi-file-o', routerLink: ['/admin/reports'] },
                    { label: 'Sync', icon: 'pi pi-fw pi-sync', routerLink: ['/admin/sync'] },
                ]
            },
            {
                label: 'Elastictranscoder',
                items: [
                    { label: 'Media', icon: 'pi pi-fw pi-database', routerLink: ['/elastictranscoder/media'] },
                    { label: 'Progress', icon: 'pi pi-fw pi-forward', routerLink: ['/elastictranscoder/progress'] },
                ]
            }
        ];
    }
}
