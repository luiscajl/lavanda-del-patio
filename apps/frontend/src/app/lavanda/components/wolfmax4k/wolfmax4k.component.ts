import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { MessageService } from 'primeng/api';
import { Index } from '../../api/index.model';
import { Bt4gService } from '../../service/bt4g.service';
import { Wolfmax4kService } from '../../service/wolfmax4k.service';
import { DataView } from 'primeng/dataview';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { FormGroup, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

export enum Type {
  TV_SHOW = 'TV_SHOW',
  FILM = 'FILM'
}

export enum Quality {
  HD = 'HD',
  FULL_HD = 'FULL_HD',
  ULTRA_HD = 'ULTRA_HD'
}

@Component({
  templateUrl: './wolfmax4k.component.html',
  styleUrls: ['./wolfmax4k.component.scss'],
  providers: [MessageService]
})
export class Wolfmax4kComponent implements OnInit {


  indexes: Index[] = [];

  pageNumber = 0;
  pageSize: number = 20;
  totalElements: number = 0;
  layout: string = 'grid';
  type: Type = undefined!;
  quality: Quality = undefined!;
  rowsPerPageOptions = [10, 20, 50]
  debounceTime = 500;
  filterName: string = '';
  filterUpdate = new Subject<string>();
  form!: FormGroup;

  constructor(
    private wolfmax4kService: Wolfmax4kService,
    private bt4gService: Bt4gService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private sp: NgxSpinnerService,
    private sanitizer: DomSanitizer
  ) {

  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.type = this.mapToType(params['type']);
      this.quality = this.mapToQuality(params['quality']);
      this.searchData();
    });
    this.initializeForm();
  }

  initializeForm() {
    this.form = new UntypedFormGroup({
      name: new UntypedFormControl(''),
    });
    this.form.valueChanges.pipe(
      debounceTime(this.debounceTime),
    ).subscribe(changes => this.formChanged(changes));
  }

  searchData(name?: string) {
    this.sp.show();
    if (name){
      this.pageNumber = 0;
    }
    this.wolfmax4kService.getAllByPageable(this.pageNumber, this.pageSize, this.type.toString(), this.quality.toString(), name).subscribe(
      response => {
        this.indexes = response.content
        this.totalElements = response.totalElements;
        this.sp.hide();
        if (this.indexes.length === 0) {
          this.messageService.add({ severity: 'info', detail: 'No data found', life: 3000 });
        }
      },
      error => {
        this.sp.hide()
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      }

    );
  }

  mapToType(type: string): Type {
    switch (type) {
      case 'shows':
        return Type.TV_SHOW;
      case 'films':
        return Type.FILM;
      default:
        throw new Error('Invalid type');
    }
  }

  mapToQuality(quality: string): Quality {
    switch (quality) {
      case '720p':
        return Quality.HD;
      case '1080p':
        return Quality.FULL_HD;
      case '2160p':
        return Quality.ULTRA_HD;
      default:
        throw new Error('Invalid quality');
    }
  }

  addToBt4GBatch(index: Index) {
    this.sp.show();
    this.bt4gService.searchBatch(index.indexName!).subscribe(
      (response) => {
        this.sp.hide();
        this.messageService.add({ severity: 'success', detail: 'Added to batch', life: 3000 });
      }, (error) => {
        this.sp.hide();
        this.messageService.add({ severity: 'error', detail: 'Error: ' + error.message, life: 3000 });
      }
    );
  }

  formChanged(currentValue: any) {
    let name = this.form!.get('name')!.value;
    this.searchData(name);
  }

  getSanitizedImageUrl(image: string): SafeUrl {
    if (image.startsWith('http')) {
      return this.sanitizer.bypassSecurityTrustUrl(image);
    } else {
      return this.sanitizer.bypassSecurityTrustUrl('data:image/png;base64,' + image);
    }
  }
  onPageChange(event: any) {
    console.log("Page Change event: ", event);
    this.pageNumber = event.page + 1;
    this.pageSize = event.rows;
    this.searchData();
  }

}
