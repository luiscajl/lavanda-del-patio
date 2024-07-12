import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Wolfmax4kComponent } from './wolfmax4k.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: Wolfmax4kComponent }
    ])],
    exports: [RouterModule]
})
export class Wolfmax4kRoutingModule { }
