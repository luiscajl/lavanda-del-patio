import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminExecutorComponent } from './admin-executor.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: AdminExecutorComponent }
    ])],
    exports: [RouterModule]
})
export class AdminExecutorRoutingModule { }
