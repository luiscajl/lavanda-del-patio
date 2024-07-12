import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ElasticTranscoderMediaComponent } from './elastictranscoder-media.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: ElasticTranscoderMediaComponent }
    ])],
    exports: [RouterModule]
})
export class ElasticTranscoderMediaRoutingModule { }
