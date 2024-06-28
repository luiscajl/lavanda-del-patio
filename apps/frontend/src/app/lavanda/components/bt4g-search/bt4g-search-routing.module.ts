import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Bt4gSearchComponent } from './bt4g-search.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: Bt4gSearchComponent }
    ])],
    exports: [RouterModule]
})
export class Bt4gSearchRoutingModule { }
