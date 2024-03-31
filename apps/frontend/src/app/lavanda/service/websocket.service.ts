// import { Stomp } from 'stompjs';
import { TranscodeMedia } from './../models/transcodemedia.model';
import { environment } from './../../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject, Observable, BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  // public stompClient: Stomp;
  private transcodeStatus = new Subject<TranscodeMedia[]>();
  public transcodeStatusObs = this.transcodeStatus.asObservable();

  constructor() {
    this.initializeWebSocketConnection();
  }

  initializeWebSocketConnection() {
    // const serverUrl = environment.apiUrl + 'transcodes/ws';
    // const ws = new SockJS(serverUrl);
    // this.stompClient = Stomp.over(ws);
    // this.stompClient.debug = () => { };
    // const that = this;
    // this.stompClient.connect({}, function (frame:any) {
    //   that.stompClient.subscribe('/transcodes', (message:any) => {
    //     if (message.body) {
    //       const jsonParsed: TranscodeMedia[] = JSON.parse(message.body);
    //       that.updateObservable(jsonParsed);
    //     }
    //   });
    // });
  }

  updateObservable(data: TranscodeMedia[]) {
    // this.transcodeStatus.next(data);
  }

  sendMessage(message: string) {
    // this.stompClient.send('/app/send/message', {}, message);
  }
}
