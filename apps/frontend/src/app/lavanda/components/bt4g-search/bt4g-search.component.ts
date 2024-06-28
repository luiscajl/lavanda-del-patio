import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { Subscription, debounceTime, take } from 'rxjs';
import { LayoutService } from 'src/app/layout/service/app.layout.service';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { FilebotExecutor, FilebotExecutorAction, FilebotExecutorCategory, FilebotExecutorStatus } from '../../api/filebot-executor.model';
import { FormControl, FormGroup, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { Bt4gService } from '../../service/bt4g.service';
import { Bt4g } from '../../api/bt4g.model';
import { Search } from '../../api/search.model';
import { NgxSpinnerService } from 'ngx-spinner';
import { QbittorrentService } from '../../service/qbittorrent.service';
import { error } from 'console';


@Component({
  templateUrl: './bt4g-search.component.html',
  providers: [MessageService]
})
export class Bt4gSearchComponent implements OnInit {

  form!: FormGroup;
  cols: any[] = [];


  searchData: any[] = [];
  bt4gSearchsNames: string[] = [];
  bt4gSearchs: Search[] = [];
  rowsPerPageOptions = [10, 20, 30]
  pageNumber = 1;
  pageSize: number = 20;
  totalElements: number = 0;

  selectedSearch: string = undefined!;

  searchName: string = "";

  searchNameBatch: string = "";

  constructor(private messageService: MessageService,
    private readonly bt4gservice: Bt4gService,
    private sp: NgxSpinnerService,
    private qbittorrentService: QbittorrentService
  ) {
  }

  ngOnInit() {
    this.initializeForm();
    this.reloadSearch();
  }

  initializeForm() {
    this.form = new UntypedFormGroup({
      search: new UntypedFormControl(''),
    });
    this.form = new FormGroup({
      search: new FormControl<string | null>(null),
    });
  }

  reloadSearch() {
    this.sp.show();
    this.bt4gservice.getAllSearch().subscribe(response => {
      this.bt4gSearchs = response;
      this.bt4gSearchsNames = response.map(search => search.name);
      this.sp.hide();
      if (this.bt4gSearchs.length === 0) {
        this.messageService.add({ severity: 'info', detail: 'No data found', life: 3000 });
      }
    },
      error => {
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
        this.sp.hide()
      });
  }


  manualSearch() {
    this.sp.show();
    this.selectedSearch = this.searchName;
    this.bt4gservice.search(this.searchName).subscribe(response => {
      this.searchData = response;
      this.sp.hide();
    },
      error => {
        this.sp.hide();
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      }
    );
  }

  batchSearch() {
    this.sp.show();
    this.selectedSearch = this.searchNameBatch;
    this.bt4gservice.searchBatch(this.searchNameBatch).subscribe(response => {
      this.sp.hide();
      this.messageService.add({ severity: 'info', detail: 'Added batch', life: 3000 });
      this.reloadSearch();
    },
      error => {
        this.sp.hide();
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      });
  }


  onChangeSearch(event: any) {
    this.sp.show();
    this.bt4gservice.getAllByPageable(1, 100, this.selectedSearch, true).subscribe(response => {
      this.searchData = response.content;
      this.sp.hide();
    },
      error => {
        this.sp.hide();
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      }
    );
  }


  sendToQbittorrent(search: Bt4g) {
    console.log("Send to qbittorrent" + search);
    this.sp.show();
    this.qbittorrentService.addTorrent(search.magnet!).subscribe(
      response => {
        this.bt4gservice.updateToDownloaded(search.id!).subscribe((response) => {
          this.sp.hide();
          this.messageService.add({ severity: 'info', detail: 'Torrent added', life: 3000 });
          this.updateStatusColor(search);
        })
      },
      error => {
        this.sp.hide()
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      });
  }
  updateStatusColor(search: Bt4g) {
    search.downloaded = true;
  }

  removeSearch() {
    const selectedObject = this.bt4gSearchs.find(search => search.name === this.selectedSearch);
    this.sp.show();
    this.bt4gservice.deleteBt4gSearch(selectedObject?.id!).subscribe(
      response => {
        this.reloadSearch();
      },
      error => {
        this.reloadSearch();
      });
  }

  getClassForDownloadButton(search: Bt4g): string {
    return search.downloaded ? 'p-button-rounded p-button-success mr-2' : 'p-button-rounded p-button-danger mr-2';
  }

  onPageChange(event: any) {
    console.log("Page Change event: ", event);
    this.pageNumber = event.page + 1;
    this.pageSize = event.rows;
    this.onChangeSearch(null);
  }

}
