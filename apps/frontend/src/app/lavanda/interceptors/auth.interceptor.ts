import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const allowedOrigins = ['http://localhost:8080', 'https://api.lavandadelpatio.es',
      'https://files2.lavandadelpatio.es', 'https://api-pre.lavandadelpatio.es', 'https://oauth2.lavandadelpatio.es'];
    // if (allowedOrigins.some(url => request.urlWithParams.includes(url))) {
    //   request = request.clone({
    //     withCredentials: true
    //   });
    //   // console.log("With Crendentials request")
    //   // console.log(request)
    //   return next.handle(request);
    // }
    return next.handle(request);
  }
}
