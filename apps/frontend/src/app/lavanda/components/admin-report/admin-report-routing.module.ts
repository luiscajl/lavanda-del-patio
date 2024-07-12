import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminReportComponent } from './admin-report.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: AdminReportComponent }
    ])],
    exports: [RouterModule]
})
export class AdminReportRoutingModule { }
