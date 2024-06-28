import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ElasticTranscoderProgressComponent } from './elastictranscoder-progress.component';

@NgModule({
    imports: [RouterModule.forChild([
        { path: '', component: ElasticTranscoderProgressComponent }
    ])],
    exports: [RouterModule]
})
export class ElasticTranscoderProgressRoutingModule { }
