import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ElasticTranscoderProgressComponent } from './elastictranscoder-progress.component';
import { ChartModule } from 'primeng/chart';
import { MenuModule } from 'primeng/menu';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { StyleClassModule } from 'primeng/styleclass';
import { PanelMenuModule } from 'primeng/panelmenu';
import { ElasticTranscoderProgressRoutingModule } from './elastictranscoder-progress-routing.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ChartModule,
        MenuModule,
        TableModule,
        StyleClassModule,
        PanelMenuModule,
        ButtonModule,
        ElasticTranscoderProgressRoutingModule
    ],
    declarations: [ElasticTranscoderProgressComponent]
})
export class ElasticTranscoderProgressModule { }
