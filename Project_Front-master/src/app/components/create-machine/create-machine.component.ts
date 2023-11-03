import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MachineService } from "../../services/machine.service";
import { Router } from "@angular/router";
import { CreateRequest } from "../../models";

@Component({
  selector: 'app-create-machine',
  templateUrl: './create-machine.component.html',
  styleUrls: ['./create-machine.component.css']
})
export class CreateMachineComponent implements OnInit {

  createMachineForm: FormGroup;
  createRequest: CreateRequest;

  constructor(
    private machineService: MachineService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.createMachineForm = this.formBuilder.group({
      name: ['', Validators.required]
    });
    this.createRequest = {
      name: '',
      mail: localStorage.getItem("userMail")!
    };
  }

  ngOnInit(): void {}

  createMachine() {
    this.machineService.createMachine(this.createRequest).subscribe(result => {
      this.router.navigate(['/machines']);
      console.log(result);
    });
  }
}
