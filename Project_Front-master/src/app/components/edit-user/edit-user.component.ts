import { Component, OnInit } from '@angular/core';
import { UserService } from "../../services/user.service";
import { ActivatedRoute } from "@angular/router";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Role, User } from "../../models";

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  updateForm: FormGroup;
  userInfo: User;
  readRole: boolean;
  createRole: boolean;
  updateRole: boolean;
  deleteRole: boolean;
  searchRole: boolean;
  startRole: boolean;
  stopRole: boolean;
  restartRole: boolean;
  createMachineRole: boolean;
  destroyRole: boolean;
  scheduleRole: boolean;
  serverRoles: Role[];
  userRoles: Role[];

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {
    this.updateForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      mail: ['', [Validators.required]]
    });

    this.userInfo = {
      id: 0,
      name: '',
      lastName: '',
      mail: '',
      roles: []
    };

    this.serverRoles = [];
    this.userRoles = [];

    this.readRole = false;
    this.createRole = false;
    this.updateRole = false;
    this.deleteRole = false;
    this.searchRole = false;
    this.startRole = false;
    this.stopRole = false;
    this.restartRole = false;
    this.createMachineRole = false;
    this.destroyRole = false;
    this.scheduleRole = false;
  }

  ngOnInit(): void {
    const id: number = parseInt(<string>this.route.snapshot.paramMap.get('id'));
    this.userInfo.id = id;
    this.getServerRoles();
    this.getUser(id);
  }

  getUser(id: number): void {
    this.userService.getUser(id).subscribe(result => {
      this.userInfo.name = result.name;
      this.userInfo.lastName = result.lastName;
      this.userInfo.mail = result.mail;
      this.userRoles = result.roles;

      // Postavljanje statusa checkboxova na osnovu rola
      this.userRoles.forEach(role => {
        if (role.name === 'CAN_READ_USERS') this.readRole = true;
        if (role.name === 'CAN_CREATE_USERS') this.createRole = true;
        if (role.name === 'CAN_UPDATE_USERS') this.updateRole = true;
        if (role.name === 'CAN_DELETE_USERS') this.deleteRole = true;
        // ... Ostale role ...
      });
    });
  }

  updateUser(): void {
    this.userInfo.roles = [];
    if (this.readRole) this.addRoles('CAN_READ_USERS');
    if (this.createRole) this.addRoles('CAN_CREATE_USERS');
    if (this.updateRole) this.addRoles('CAN_UPDATE_USERS');
    if (this.deleteRole) this.addRoles('CAN_DELETE_USERS');
    // ... Ostale role ...
    
    this.userService.updateUser(this.userInfo).subscribe(result => {
      console.log(result);
    });
  }

  addRoles(name: string): void {
    const role = this.serverRoles.find(role => role.name === name);
    if (role) {
      this.userInfo.roles.push(role);
    }
  }

  getServerRoles(): void {
    this.userService.getRoles().subscribe(result => {
      this.serverRoles = result;
    });
  }
}
