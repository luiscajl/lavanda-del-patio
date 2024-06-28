export interface Pageable {
    content: any;
    // "pageable": {
    //     "sort": {
    //         "sorted": false,
    //         "unsorted": true,
    //         "empty": true
    //     },
    //     "offset": 0,
    //     "pageNumber": 0,
    //     "pageSize": 20,
    //     "paged": true,
    //     "unpaged": false
    // },
    last: boolean,
    totalPages: number,
    totalElements: number,
    size: number,
    number: number,
    //  "sort":{
    //     "sorted":false,
    //     "unsorted":true,
    //     "empty":true
    //  },
    numberOfElements: number,
    first: boolean,
    empty: boolean
}