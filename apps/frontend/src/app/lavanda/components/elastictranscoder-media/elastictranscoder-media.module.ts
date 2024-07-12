import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ElasticTranscoderMediaComponent } from './elastictranscoder-media.component';
import { ChartModule } from 'primeng/chart';
import { MenuModule } from 'primeng/menu';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { StyleClassModule } from 'primeng/styleclass';
import { PanelMenuModule } from 'primeng/panelmenu';
import { ElasticTranscoderMediaRoutingModule } from './elastictranscoder-media-routing.module';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { DropdownModule } from 'primeng/dropdown';
import { PaginatorModule } from 'primeng/paginator';
import { DialogModule } from 'primeng/dialog';

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
    ElasticTranscoderMediaRoutingModule,
    TooltipModule,
    ReactiveFormsModule,
    ToastModule,
    ToolbarModule,
    DropdownModule,
    PaginatorModule,
    DialogModule
  ],
  declarations: [ElasticTranscoderMediaComponent]
})
export class ElasticTranscoderMediaModule { }
