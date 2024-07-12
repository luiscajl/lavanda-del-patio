import { Component, OnInit, OnDestroy } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { Product } from '../../api/product';
import { ProductService } from '../../service/product.service';
import { Subscription } from 'rxjs';
import { LayoutService } from 'src/app/layout/service/app.layout.service';
import { FilebotReport } from '../../api/filebot-report.model';
import { FilebotReportService } from '../../service/filebot-report.service';
import { Table } from 'primeng/table';

@Component({
    templateUrl: './admin-report.component.html',
    providers: [MessageService]
})
export class AdminReportComponent implements OnInit {


    reports: FilebotReport[] = [];

    selectedExecutors: FilebotReport[] = [];

    submitted: boolean = false;
    report: FilebotReport = {};

    cols: any[] = [];

    statuses: any[] = [];

    page = 0;

    pageSize: number = 20;

    totalElements: number = 0;

    rowsPerPageOptions = [10, 20, 30]

    constructor(private messageService: MessageService,
        private readonly filebotReportService: FilebotReportService) { }

    ngOnInit() {
        this.fillReport(this.page, this.pageSize);
    }
    onPageChange(event: any) {
        this.fillReport(event.page, event.rows);
    }

    fillReport(page: number, pageSize: number) {
        this.filebotReportService.getAllByPageable(page, pageSize).subscribe(data => {
            console.log("data: ", data);
            this.reports = data.content;
            this.totalElements=data.totalElements;
        });
    }

    reload() {
        this.ngOnInit();
    }

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
    }

}
