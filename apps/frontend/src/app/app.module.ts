import { NgModule } from '@angular/core';
import { HashLocationStrategy, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AppLayoutModule } from './layout/app.layout.module';
import { NotfoundComponent } from './lavanda/components/notfound/notfound.component';
import { CountryService } from './lavanda/service/country.service';
import { CustomerService } from './lavanda/service/customer.service';
import { EventService } from './lavanda/service/event.service';
import { IconService } from './lavanda/service/icon.service';
import { NodeService } from './lavanda/service/node.service';
import { PhotoService } from './lavanda/service/photo.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './lavanda/interceptors/auth.interceptor';
import { SpinnerInterceptor } from './lavanda/interceptors/spinner.interceptor';
import { NgxSpinnerModule } from 'ngx-spinner';

@NgModule({
  declarations: [
    AppComponent, NotfoundComponent
  ],
  imports: [
    AppRoutingModule,
    AppLayoutModule
  ],
  providers: [
    { provide: LocationStrategy, useClass: PathLocationStrategy },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: SpinnerInterceptor, multi: true },
    CountryService, CustomerService, EventService, IconService, NodeService,
    PhotoService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
