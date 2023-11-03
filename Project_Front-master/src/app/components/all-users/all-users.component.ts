import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Role, User } from '../../models';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-all-users',
  templateUrl: './all-users.component.html',
  styleUrls: ['./all-users.component.css']
})
export class AllUsersComponent implements OnInit, OnDestroy {
  userList: User[] = [];
  userRoles: Role[] = [];
  canUpdate: boolean = false;

  constructor(private userService: UserService, private router: Router) {}

  ngOnDestroy() {}

  ngOnInit(): void {
    this.fetchUserList();
    this.userRoles = JSON.parse(<string>localStorage.getItem('userRoles'));
    this.canUpdate = this.getPermission('can_update_users');
  }

  fetchUserList(): void {
    this.userService.getAll().subscribe((result) => {
      this.userList = result;
    });
  }

  getPermission(permission: string): boolean {
    return !!localStorage.getItem('userRoles')?.includes(permission);
  }

  deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe(() => {
      this.fetchUserList();
    });
  }

  logOut(): void {
    localStorage.setItem('token', '');
    localStorage.setItem('roles', '');
  }
}
