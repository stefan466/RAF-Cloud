import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateMachinesGuard implements CanActivate {

  router: Router

  constructor(router: Router) {
    this.router = router
  }

  canActivate(route: ActivatedRouteSnapshot,state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (localStorage.getItem("userRoles")?.includes('can_create_machines')){
      return true
    }
    else {
      alert("You don't have the required roles for this action")
      this.router.navigate([''])
      return false
    }

  }
}
