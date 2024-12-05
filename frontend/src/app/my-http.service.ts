import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Token } from './token';

@Injectable({
  providedIn: 'root'
})
export class MyHttpService {

  token: string = "";

  // Base URL de la API, ajusta esta URL si cambia tu balanceador de carga. http://localhost:8080
  private readonly baseUrl: string = "http://ALB-micro-1831133009.us-east-1.elb.amazonaws.com";

  constructor(private http: HttpClient) { }

  get(url: string): any {
    return this.http.get(this.baseUrl + url);
  }

  getPrivate(url: string): any {
    let headers = {};
    if (this.token) {
      headers = { headers: new HttpHeaders({"Authorization": "Bearer " + this.token}) };
    }
    return this.http.get(this.baseUrl + url, headers);
  }

  getToken(code: string): Observable<boolean> {
    return this.http.get<Token>(`${this.baseUrl}/auth/callback?code=${code}`, {observe: "response"})
      .pipe(map((response: HttpResponse<Token>) => {
        if (response.status === 200 && response.body !== null) {
          this.token = response.body.token;
          return true;
        } else {
          return false;
        }
      }));
  }
}
