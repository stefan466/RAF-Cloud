import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserLoginInfo, UserLoginResponse} from "../models";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private httpClient: HttpClient)
  {}

  public loginUser(loginInfo: UserLoginInfo): Observable<UserLoginResponse>{
    return this.httpClient.post<UserLoginResponse>(`${environment.apiUserServerUrl}/login`, loginInfo);
  }


}
