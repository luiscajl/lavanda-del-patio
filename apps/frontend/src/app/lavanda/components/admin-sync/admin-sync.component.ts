import { Component, OnDestroy, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { take } from 'rxjs';
import { FilebotExecutor, FilebotExecutorAction, FilebotExecutorCategory } from '../../api/filebot-executor.model';
import { Qbittorrent } from '../../api/qbittorrent.model';
import { FilebotExecutorService } from '../../service/filebot-executor.service';
import { MediaService } from '../../service/media.service';
import { MediaFile, MediaPath } from '../../api/media.model';

interface expandedRows {
  [key: string]: boolean;
}

@Component({
  templateUrl: './admin-sync.component.html',
  providers: [MessageService]
})
export class AdminSyncComponent implements OnInit {

  mediaPathDialog: boolean = false;

  mediaPaths: MediaPath[] = [];

  deleteMediaDialog: boolean = false;

  deleteMediasDialog: boolean = false;

  selectedMedia: MediaPath[] = [];

  submitted: boolean = false;
  mediaPath: MediaPath = {};
  syncDialog: boolean = false;
  mediaSyncFile: MediaFile = {};
  mediaSyncPath: MediaPath = {};
  cols: any[] = [];
  page = 0;

  pageSize: number = 20;
  statuses: any[] = [];
  expandedRows: expandedRows = {};

  totalElements: number = 0;

  rowsPerPageOptions = [10, 20, 30]

  redoEnabled: boolean = false;
  isCreatingNew: boolean = false;
  executorActions = Object.values(FilebotExecutorAction);
  executorCategorys = Object.values(FilebotExecutorCategory);

  // ['MEGA', 'LEGACY-RASPBERRYPI4'];

  constructor(private messageService: MessageService,
    private readonly mediaService: MediaService) { }

  ngOnInit() {
    // this.fillExecutors(this.page, this.pageSize);
    this.mediaService.getAllByPageable(0, 20).pipe(take(1)).subscribe(
      (files) => {
        this.mediaPaths = files;
      }
    );
    // this.executorService.getAllByPageable(1, 20).subscribe(data => {
    //   this.executors = data.content;
    //   data.content.forEach((element: { path: string; }) => {
    //     element.path = element.path.substring(element.path.lastIndexOf('/') + 1);
    //     // console.log(element.path);
    //   });
    // }
    // );
  }


  onPageChange(event: any) {
    this.page = event.page;
    this.pageSize = event.rows;
    // this.fillExecutors(event.page, event.rows);
  }

  fillMedia(page: number, pageSize: number) {
    this.mediaService.getAllByPageable(page, pageSize).subscribe(files => {
      console.log("data: ", files);
      this.mediaPaths = files
      // data.content.forEach((element: { path: string; }) => {
      //   element.path = element.path.substring(element.path.lastIndexOf('/') + 1);
      //   // console.log(element.path);
      // });
      // this.totalElements = data.totalElements;
    });
  }


  createNew() {
    this.mediaPath = {};
    this.submitted = false;
    this.mediaPathDialog = true;
    this.isCreatingNew = true;
    console.log("isCreatingNew", this.isCreatingNew);
  }

  // reload() {
  //   this.fillExecutors(this.page, this.pageSize);
  // }

  // executeManually() {
  //   this.executorService
  //     .manualExecution()
  //     .subscribe(
  //       (data) => {
  //         this.messageService.add({ severity: 'success', detail: 'Manual Execution started', life: 3000 });
  //       });
  // }


  deleteSelectedExecutors() {
    this.deleteMediasDialog = true;
  }

  editExecutor(executor: FilebotExecutor) {
    // this.executor = { ...executor };
    // this.executorDialog = true;
    // this.isCreatingNew = false;
  }

  deleteExecutor(executor: FilebotExecutor) {
    // this.deleteExecutorDialog = true;


    // this.executor = { ...executor };
  }

  confirmDeleteSelected() {
    // this.deleteExecutorsDialog = false;
    // this.executors = this.executors.filter(val => !this.selectedExecutors.includes(val));
    // this.selectedExecutors = [];
  }

  confirmDelete() {
    // this.deleteExecutorDialog = false;
    // this.executors = this.executors.filter(val => val.id !== this.executor.id);
    // this.executorService.delete(this.executor.id!).subscribe(
    //   (data) => {
    //     this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Executor Deleted', life: 3000 });
    //   });
    // this.executor = {};
  }

  hideDialog() {
    // this.executorDialog = false;
    this.submitted = false;
  }

  saveExecutor() {


  }

  findIndexById(id: string): number {
    let index = -1;
    // for (let i = 0; i < this.executors.length; i++) {
    //   if (this.executors[i].id === id) {
    //     index = i;
    //     break;
    //   }
    // }

    return index;
  }

  createId(): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }
  onGlobalFilter(table: Table, event: Event) {
    table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }

  forceAnalysis() {
    this.mediaService.forceAnalysis().subscribe(
      () => {
        this.messageService.add({ severity: 'success', detail: 'Analysis ended!', life: 3000 });
        this.fillMedia(this.page, this.pageSize);
      });
  }
  getProviders(media: MediaPath): string[] {
    const uniqueProviders = new Set<string>();
    if (media.mediaFiles == null) {
      return [];
    }
    else {
      media.mediaFiles!.forEach((mediaFile) => {
        uniqueProviders.add(mediaFile.provider!);
      });
      return Array.from(uniqueProviders);
    }
  }



  checkDropbox(media: MediaPath): boolean {
    return this.getProviders(media).includes('dropbox');
  }

  checkMega(media: MediaPath): boolean {
    return this.getProviders(media).includes('mega');
  }

  checkNas(media: MediaPath): boolean {
    return this.getProviders(media).includes('nas');
  }

  checkDropboxMediaFile(media: MediaFile): boolean {
    return media.provider!.includes('dropbox');
  }

  checkMegaMediaFile(media: MediaFile): boolean {
    return media.provider!.includes('mega');
  }

  checkNasMediaFile(media: MediaFile): boolean {
    return media.provider!.includes('nas');
  }

  secondsToHms(d: number): string {
    const h = Math.floor(d / 3600);
    const m = Math.floor((d % 3600) / 60);
    const s = Math.floor((d % 3600) % 60);

    const hDisplay = h > 0 ? h + (h === 1 ? ' hour ' : ' hours ') : '';
    const mDisplay = m > 0 ? m + (m === 1 ? ' minute ' : ' minutes ') : '';

    return hDisplay + mDisplay;
  }



  activateDeactivateTranscode(mediaFile: MediaFile) {
    mediaFile.selectedForTranscoding = !mediaFile.selectedForTranscoding;
    console.log(this.mediaPath);
    // this.mediaService.update(mediaFile).subscribe();
  }

  openSyncDialog(mediaFile: MediaFile, mediaPath: MediaPath) {
    this.mediaSyncPath = mediaPath
    this.mediaSyncFile = mediaFile;
    this.syncDialog = true;
  }

  sendToNas() {
    this.mediaService.sendToSync(this.mediaSyncFile, this.mediaSyncPath, "NAS").subscribe(
      () => {
        this.syncDialog = false;
        this.messageService.add({ severity: 'success', detail: 'Sync started!', life: 3000 });
      }
    );
  }

  sendToMega() {
    this.mediaService.sendToSync(this.mediaSyncFile, this.mediaSyncPath, "MEGA").subscribe(
      () => {
        this.syncDialog = false;
        this.messageService.add({ severity: 'success', detail: 'Sync started!', life: 3000 });
      }
    );
  }

}
