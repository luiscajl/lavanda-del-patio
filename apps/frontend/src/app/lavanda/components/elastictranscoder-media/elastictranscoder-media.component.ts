import { Component, OnInit, OnDestroy } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { Product } from '../../api/product';
import { ProductService } from '../../service/product.service';
import { AfterViewInit } from '@angular/core';
import { Subscription, debounceTime, take } from 'rxjs';
import { LayoutService } from 'src/app/layout/service/app.layout.service';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { FilebotExecutor, FilebotExecutorAction, FilebotExecutorCategory, FilebotExecutorStatus } from '../../api/filebot-executor.model';
import { FilebotExecutorService } from '../../service/filebot-executor.service';
import { Qbittorrent } from '../../api/qbittorrent.model';
import { FormControl, FormGroup, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MediaService } from '../../service/media.service';
import { MediaFile, MediaPath } from '../../api/media.model';

interface expandedRows {
  [key: string]: boolean;
}
@Component({
  templateUrl: './elastictranscoder-media.component.html',
  providers: [MessageService]

})
export class ElasticTranscoderMediaComponent implements OnInit, OnDestroy {


  executorDialog: boolean = false;
  executors: MediaPath[] = [];
  deleteExecutorDialog: boolean = false;
  deleteExecutorsDialog: boolean = false;
  selectedExecutors: MediaPath[] = [];
  submitted: boolean = false;
  executor: any = {};
  cols: any[] = [];
  pageNumber = 0;
  pageSize: number = 20;
  totalElements: number = 0;
  executorStatus: any[] = [];
  expandedRows: expandedRows = {};
  rowsPerPageOptions = [10, 20, 30]
  redoEnabled: boolean = false;
  isCreatingNew: boolean = false;
  executorActions = Object.values(FilebotExecutorAction);
  executorCategorys = Object.values(FilebotExecutorCategory);
  allFiles: string[] = [];
  debounceTime = 500;
  searchInput: string = null!;
  statusSelected: string = null!;
  form!: FormGroup;
  status: String[] = [];
  subscription: Subscription | undefined;
  constructor(private messageService: MessageService,
    private readonly mediaService: MediaService) {
    this.status = Object.keys(FilebotExecutorStatus).filter((v) => isNaN(Number(v)));
    this.status.unshift(undefined!);
  }

  ngOnInit() {
    this.fillExecutors(this.pageNumber, this.pageSize);
    this.getAllFiles();
    this.initializeForm();
  }

  initializeForm() {
    this.form = new UntypedFormGroup({
      status: new UntypedFormControl(''),
      search: new UntypedFormControl(''),
    });
    this.form = new FormGroup({
      search: new FormControl<string | null>(null),
      status: new FormControl<string | null>(null)
    });
  }

  getAllFiles() {

  }
  ngAfterViewInit() {
    this.form.valueChanges.pipe(
      debounceTime(this.debounceTime),
    ).subscribe(changes => this.formChanged(changes));
  }

  onPageChange(event: any) {
    console.log("Page Change event: ", event);
    this.pageNumber = event.page;
    this.pageSize = event.rows;
    this.reload();
  }

  normalizeStatus(status: string) { return status.replace(/_/g, ' '); }

  fillExecutors(page: number, pageSize: number) {
    this.mediaService.getAllByPageable(page, pageSize, this.searchInput, this.statusSelected).subscribe(data => {
      // console.log("data: ", data);
      this.executors = data;
      // data.content.forEach((element: { path: string; }) => {
      //   element.path = element.path.substring(element.path.lastIndexOf('/') + 1);
      //   // console.log(element.path);
      // });
      // this.totalElements = data.totalElements;
    });
  }


  createNew() {
    this.executor = {};
    this.submitted = false;
    this.executorDialog = true;
    this.isCreatingNew = true;
    console.log("isCreatingNew", this.isCreatingNew);
  }

  reload() {
    console.log("Reload");
    this.fillExecutors(this.pageNumber, this.pageSize);
  }

  executeManually() {
    // this.executorService
    //   .reExecutionAll()
    //   .subscribe(
    //     (data) => {
    //       this.messageService.add({ severity: 'success', detail: 'Full reexecution throwed', life: 3000 });
    //       this.reload();
    //     });
  }

  editExecutor(executor: FilebotExecutor) {
    this.executor = { ...executor };
    this.executorDialog = true;
    this.isCreatingNew = false;
  }

  deleteExecutor(executor: FilebotExecutor) {
    this.deleteExecutorDialog = true;
    this.executor = { ...executor };
  }

  deleteSelectedExecutors() {
    this.deleteExecutorsDialog = true;
  }

  downloadLog(executor: FilebotExecutor) {
    // this.executorService
    //   .downloadLog(executor.log!);
  }

  reExecution(executor: FilebotExecutor) {
    // this.executorService.reExecution(executor.id!).subscribe(
    //   (data) => {
    //     this.messageService.add({ severity: 'success', detail: 'Reset execution throwed', life: 3000 });
    //     this.reload();
    //   });
  }

  confirmDeleteSelected() {
    this.deleteExecutorsDialog = false;
    this.executors = this.executors.filter(val => !this.selectedExecutors.includes(val));
    // this.selectedExecutors.forEach((executor) => {
    //   this.executorService.delete(executor.id!).subscribe(
    //     (data) => {
    //       this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Executor Deleted', life: 1000 });
    //       this.reload();
    //     });
    // });
    this.selectedExecutors = [];
  }

  confirmDelete() {
    this.deleteExecutorDialog = false;
    this.executors = this.executors.filter(val => val.id !== this.executor.id);
    // this.executorService.delete(this.executor.id!).subscribe(
    //   (data) => {
    //     this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Executor Deleted', life: 3000 });
    //   });
    this.executor = {};
  }

  hideDialog() {
    this.executorDialog = false;
    this.submitted = false;
  }


  saveExecutor() {
    if (!this.redoEnabled) {
      this.executor.command = null;
    }
    if (this.isCreatingNew) {
      let qbittorrent: Qbittorrent = {
        id: this.createId(),
        category: this.executor.category!,
        name: this.executor.path!,
        action: this.executor.action!
      };
      // this.executorService.createQbittorrent(qbittorrent).pipe(take(1)).subscribe((res) => {
      //   this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Executor Created', life: 3000 });
      //   this.reload();
      // });
    } else {
      // this.executorService.editFilebotExecutor(this.executor.id!, this.executor).pipe(take(1)).subscribe((res) => {
      //   this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Executor Updated', life: 3000 });
      //   this.reload();
      // }
      // );
    }
    this.submitted = true;
    this.isCreatingNew = false;
    this.executors = [...this.executors];
    this.executorDialog = false;
    this.executor = {};

  }

  createId(): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  formChanged(currentValue: any) {
    const fields = ['search', 'status'];
    this.statusSelected = this.form!.get('status')!.value;
    this.searchInput = this.form!.get('search')!.value;
    this.reload();
  }
  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
