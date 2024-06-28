import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Bt4gComponent } from './bt4g.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: Bt4gComponent }
    ])],
    exports: [RouterModule]
})
export class Bt4gRoutingModule { }
