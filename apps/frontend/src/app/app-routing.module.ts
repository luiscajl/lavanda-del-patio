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
      { path: 'reports', loadChildren: () => import('./lavanda/components/admin-report/admin-report.module').then(m => m.AdminReportModule) },
      { path: 'sync', loadChildren: () => import('./lavanda/components/admin-sync/admin-sync.module').then(m => m.AdminSyncModule) },
    ]
  },
  {
    path: 'elastictranscoder', component: AppLayoutComponent,
    children: [
      { path: 'media', loadChildren: () => import('./lavanda/components/elastictranscoder-media/elastictranscoder-media.module').then(m => m.ElasticTranscoderMediaModule) },
      { path: 'progress', loadChildren: () => import('./lavanda/components/elastictranscoder-progress/elastictranscoder-progress.module').then(m => m.ElasticTranscoderProgressModule) },
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
