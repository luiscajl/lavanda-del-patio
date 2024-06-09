import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { NotfoundComponent } from './lavanda/components/notfound/notfound.component';
import { AppLayoutComponent } from "./layout/app.layout.component";


const routes: Routes = [
  {
    path: '',
    redirectTo: 'admin/executors',
    pathMatch: 'full'
  },
  {
    path: 'admin', component: AppLayoutComponent,
    children: [
      { path: 'executors', loadChildren: () => import('./lavanda/components/admin-executor/admin-executor.module').then(m => m.AdminExecutorModule) },
    ]
  },
  {
    path: 'bt4g', component: AppLayoutComponent,
    children: [
      { path: '', loadChildren: () => import('./lavanda/components/bt4g/bt4g.module').then(m => m.Bt4gModule) },
      { path: 'search', loadChildren: () => import('./lavanda/components/bt4g-search/bt4g-search.module').then(m => m.Bt4gSearchModule) },
    ]
  },
  {
    path: 'wolfmax4k', component: AppLayoutComponent,
    children: [
      { path: 'shows-720p', loadChildren: () => import('./lavanda/components/wolfmax4k/wolfmax4k.module').then(m => m.Wolfmax4kModule) },
      { path: 'shows-1080p', loadChildren: () => import('./lavanda/components/wolfmax4k/wolfmax4k.module').then(m => m.Wolfmax4kModule) },
      { path: 'films-1080p', loadChildren: () => import('./lavanda/components/wolfmax4k/wolfmax4k.module').then(m => m.Wolfmax4kModule) },
    ]
  },
  { path: 'notfound', component: NotfoundComponent },
  { path: '**', redirectTo: '/notfound' },
];


@NgModule({
  imports: [
    RouterModule.forRoot(routes,
      {
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
        onSameUrlNavigation: 'reload'
      })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
