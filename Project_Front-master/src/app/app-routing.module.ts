import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from "./components/login/login.component";
import {AllUsersComponent} from "./components/all-users/all-users.component";
import {AddUserComponent} from "./components/add-user/add-user.component";
import {EditUserComponent} from "./components/edit-user/edit-user.component";
import {LoginGuard} from "./guards/login.guard";
import {AllGuard} from "./guards/all.guard";
import {AddGuard} from "./guards/add.users.guard";
import {EditGuard} from "./guards/edit.guard";
import {AllMachinesComponent} from "./components/all-machines/all-machines.component";
import {CreateMachineComponent} from "./components/create-machine/create-machine.component";
import {ErrorHistoryComponent} from "./components/error-history/error-history.component";
import {ScheduleComponent} from "./components/schedule/schedule.component";
import {AllMachinesGuard} from "./guards/all.machines.guard";
import {CreateMachinesGuard} from "./guards/create.machine.guard";


const routes: Routes = [
  {
    path: "",
    component: LoginComponent,
    canDeactivate: [LoginGuard]
  },
  {
    path: "machines",
    component: AllMachinesComponent,
    canActivate: [AllMachinesGuard]
  },
  {
    path: "create-machine",
    component: CreateMachineComponent,
    canActivate: [CreateMachinesGuard]
  },
  {
    path: "schedule/:id",
    component: ScheduleComponent,
    canActivate: [AllMachinesGuard]
  },
  {
    path: "errors",
    component: ErrorHistoryComponent,
    canActivate: [AllMachinesGuard]
  },
  {
    path: "all",
    component: AllUsersComponent,
    canActivate: [AllGuard]
  },
  {
    path: "add",
    component: AddUserComponent,
    canActivate: [AddGuard]
  },
  {
    path: "edit/:id",
    component: EditUserComponent,
    canActivate: [EditGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
