import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminSyncComponent } from './admin-sync.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: AdminSyncComponent }
    ])],
    exports: [RouterModule]
})
export class AdminSyncRoutingModule { }
