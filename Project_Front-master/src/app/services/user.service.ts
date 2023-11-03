import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Role, User, UserLoginInfo, UserLoginResponse} from "../models";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private headers

  constructor(private httpClient: HttpClient){
    this.headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('Access-Control-Allow-Origin', '*')
      .set('Authorization', `Bearer ${localStorage.getItem("token")}` )
  }

  public getAll(): Observable<User[]>{
    return this.httpClient.get<User[]>(`${environment.apiUserServerUrl}/get`, { headers: this.headers });
  }

  public getRoles(): Observable<Role[]>{
    return this.httpClient.get<Role[]>(`${environment.apiUserServerUrl}/get/roles`, { headers: this.headers });
  }

  public getUser(id: number): Observable<User>{
    return this.httpClient.get<User>(`${environment.apiUserServerUrl}/get/${id}`, { headers: this.headers });
  }

  public updateUser(user: User): Observable<User>{
    return this.httpClient.put<User>(`${environment.apiUserServerUrl}/update`, user, { headers: this.headers });
  }

  public addUser(user: User): Observable<User>{
    return this.httpClient.post<User>(`${environment.apiUserServerUrl}/add`, user, { headers: this.headers });
  }

  public deleteUser(id: number): Observable<any>{
    return this.httpClient.delete<any>(`${environment.apiUserServerUrl}/delete/${id}`, { headers: this.headers });
  }

}
