import { User } from './../models/user.model';
import { environment } from './../../../environments/environment';
import { Transcode } from './../models/transcode-selected.model';
import { TranscodeApi } from './../models/transcode.model';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  user: User = { preferredUsername: 'Localhost' };
  constructor(private httpClient: HttpClient) {}

  getUserName(): User {
    // this.httpClient.get("https://oauth2.lavandadelpatio.es/oauth2/userinfo").subscribe(
    //   (user: User) => {
    //     this.user = user;
    //   },
    //   (error) => {
    //     console.log(error);
    //   }
    // );
    return this.user;
  }
  isLogged(): boolean {
    return this.user !== null ? true : false;
  }
}
