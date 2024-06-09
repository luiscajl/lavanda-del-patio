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


@Component({
  templateUrl: './bt4g-search.component.html',
  providers: [MessageService]
})
export class Bt4gSearchComponent implements OnInit, AfterViewInit {

  form!: FormGroup;
  status: String[] = [];
  cols: any[] = [];


  searchData: any[] = [];
  bt4gSearchsNames: string[] = [];
  bt4gSearchs: Search[] = [];

  selectedSearch: string = undefined!;
  searchName: string = "";
  constructor(private messageService: MessageService,
    private readonly bt4gservice: Bt4gService,
    private sp: NgxSpinnerService
  ) {
    this.status = Object.keys(FilebotExecutorStatus).filter((v) => isNaN(Number(v)));
    this.status.unshift(undefined!);
  }

  ngOnInit() {
    this.sp.show();
    this.initializeForm();
    // this.bt4gservice.search("name").subscribe(response => {
    //   this.searchData = response;
    // });
    this.bt4gservice.getAllSearch().subscribe(response => {
      this.bt4gSearchs = response;
      this.bt4gSearchsNames = response.map(search => search.name);
      this.sp.hide();
    });
  }

  search() {
    this.sp.show();
    this.bt4gservice.search(this.searchName).subscribe(response => {
      this.searchData = response;
      this.sp.hide();
    });
  }

  initializeForm() {
    this.form = new UntypedFormGroup({
      search: new UntypedFormControl(''),
    });
    this.form = new FormGroup({
      search: new FormControl<string | null>(null),
    });
  }

  selectSearch(event: any) {
    console.log(event);

  }
  onChangeSearch(event: any) {
    this.sp.show();
    this.bt4gservice.getAllByPageable(1, 100, this.selectedSearch).subscribe(response => {
      this.searchData = response.content;
      this.sp.hide();
    });
  }


  ngAfterViewInit() {

  }
  sendToQbittorrent(search: Bt4g) {
    console.log("Send to qbittorrent" + search);

  }

  removeSearch() {
    const selectedObject = this.bt4gSearchs.find(search => search.name === this.selectedSearch);

    this.bt4gservice.deleteBt4gSearch(selectedObject?.id!).subscribe(response => {
      this.bt4gservice.getAllSearch().subscribe(response => {
        // this.selectSearch = response;
        console.log(response);
      });
    });
  }
}
