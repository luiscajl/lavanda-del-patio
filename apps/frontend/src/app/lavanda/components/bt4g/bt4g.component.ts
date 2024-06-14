import { Component, OnInit } from '@angular/core';
import { FormGroup, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';
import { MessageService } from 'primeng/api';
import { debounceTime } from 'rxjs';
import { Bt4g } from '../../api/bt4g.model';
import { FilebotExecutorStatus } from '../../api/filebot-executor.model';
import { Bt4gService } from '../../service/bt4g.service';


@Component({
  templateUrl: './bt4g.component.html',
  providers: [MessageService]
})
export class Bt4gComponent implements OnInit {

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
    this.search();
  }

  initializeForm() {
    this.form = new UntypedFormGroup({
      name: new UntypedFormControl(''),
    });
    this.form.valueChanges.pipe(
      debounceTime(this.debounceTime),
    ).subscribe(changes => this.formChanged(changes));
  }

  search(name?: string) {
    this.sp.show();
    this.bt4gservice.getAllByPageable(this.pageNumber, this.pageSize, name).subscribe(
      response => {
        this.data = response.content;
        this.totalElements = response.totalElements;
        this.sp.hide();
        if (this.data.length === 0) {
          this.messageService.add({ severity: 'info', detail: 'No data found', life: 3000 });
        }
      },
      error => {
        this.sp.hide()
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      });
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
