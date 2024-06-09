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

  constructor(private wolfmax4kService: Wolfmax4kService, private bt4gService: Bt4gService) { }


  ngOnInit() {
    this.wolfmax4kService.getAllByPageable(this.pageNumber, this.pageSize).subscribe(data => this.indexes = data.content);
    //  "https://wolfmax4k.com/assets/u/p/f/thumbs/nefarious-cuando-habla-el-diablo--2023---BluRay-1080p_29_1029.jpg"

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
