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
import { NgxSpinnerService } from 'ngx-spinner';


@Component({
  templateUrl: './bt4g.component.html',
  providers: [MessageService]
})
export class Bt4gComponent implements OnInit, AfterViewInit {

  form!: FormGroup;
  status: String[] = [];
  cols: any[] = [];
  pageNumber = 1;
  pageSize: number = 20;
  totalElements: number = 0;
  debounceTime = 500;
  rowsPerPageOptions = [10, 20, 30]
  data: any[] = [];

  constructor(private messageService: MessageService,
    private readonly bt4gservice: Bt4gService,
    private sp: NgxSpinnerService
  ) {
    this.status = Object.keys(FilebotExecutorStatus).filter((v) => isNaN(Number(v)));
    this.status.unshift(undefined!);
  }

  ngOnInit() {
    this.initializeForm();

    this.bt4gservice.getAllByPageable(this.pageNumber, this.pageSize).subscribe(response => {
      this.data = response.content;
      this.totalElements = response.totalElements
    });
    this.form = new UntypedFormGroup({
      name: new UntypedFormControl(''),
    });
    this.form.valueChanges.pipe(
      debounceTime(this.debounceTime),
    ).subscribe(changes => this.formChanged(changes));
  }



  initializeForm() {
    this.form = new UntypedFormGroup({
      search: new UntypedFormControl(''),
    });
    this.form = new FormGroup({
      search: new FormControl<string | null>(null),
    });
  }
  search(name?: string) {
    this.sp.show();
    this.bt4gservice.getAllByPageable(this.pageNumber, this.pageSize, name).subscribe(response => {
      this.data = response.content;
      this.sp.hide();
    });
  }


  ngAfterViewInit() {

  }
  sendToQbittorrent(search: Bt4g) {
    console.log("Send to qbittorrent" + search);

  }



  formChanged(currentValue: any) {
    let name = this.form!.get('name')!.value;
    this.search(name);
  }

  onPageChange(event: any) {
    console.log("Page Change event: ", event);
    this.pageNumber = event.page + 1;
    this.pageSize = event.rows;
    this.search();
  }

}
