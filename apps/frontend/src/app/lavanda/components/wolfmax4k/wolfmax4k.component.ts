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
import { Wolfmax4kService } from '../../service/wolfmax4k.service';
import { Index } from '../../api/index.model';
import { ActivatedRoute } from '@angular/router';

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
  providers: [MessageService]
})
export class Wolfmax4kComponent implements OnInit, AfterViewInit {


  indexes: Index[] = [];
  sortOptions: any[] = [];
  sortOrder: number = 0;
  sortField: string = '';
  sourceCities: any[] = [];
  targetCities: any[] = [];
  orderCities: any[] = [];
  pageNumber = 0;
  pageSize: number = 20;
  totalElements: number = 0;
  layout: string = 'grid';

  constructor(
    private wolfmax4kService: Wolfmax4kService,
    private bt4gService: Bt4gService,
    private route: ActivatedRoute
  ) {

  }


  ngOnInit() {
    this.route.params.subscribe(params => {

      const type = this.mapToType(params['type']);
      const quality = this.mapToQuality(params['quality']);

      this.wolfmax4kService.getAllByPageable(this.pageNumber, this.pageSize, type.toString(), quality.toString()).subscribe(data => this.indexes = data.content);
    });

    this.sourceCities = [
      { name: 'San Francisco', code: 'SF' },
      { name: 'London', code: 'LDN' },
      { name: 'Paris', code: 'PRS' },
      { name: 'Istanbul', code: 'IST' },
      { name: 'Berlin', code: 'BRL' },
      { name: 'Barcelona', code: 'BRC' },
      { name: 'Rome', code: 'RM' }];

    this.targetCities = [];

    this.orderCities = [
      { name: 'San Francisco', code: 'SF' },
      { name: 'London', code: 'LDN' },
      { name: 'Paris', code: 'PRS' },
      { name: 'Istanbul', code: 'IST' },
      { name: 'Berlin', code: 'BRL' },
      { name: 'Barcelona', code: 'BRC' },
      { name: 'Rome', code: 'RM' }];

    this.sortOptions = [
      { label: 'Price High to Low', value: '!price' },
      { label: 'Price Low to High', value: 'price' }
    ];
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
      case '4k':
        return Quality.ULTRA_HD;
      default:
        throw new Error('Invalid quality');
    }
  }
  onSortChange(event: any) {
    const value = event.value;

    if (value.indexOf('!') === 0) {
      this.sortOrder = -1;
      this.sortField = value.substring(1, value.length);
    } else {
      this.sortOrder = 1;
      this.sortField = value;
    }
  }

  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.');
  }

  // onFilter(dv: DataView, event: Event) {
  //   dv.filter((event.target as HTMLInputElement).value);
  // }

  addToBt4GBatch(index: Index) {
    this.bt4gService.searchBatch(index.indexName!).subscribe(() => {
      console.log('searchBatch success')
    });
  }

}
