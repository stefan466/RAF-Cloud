import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Machine, MachineSearchParameters, Role } from '../../models';
import { MachineService } from '../../services/machine.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Stomp, CompatClient, Message } from '@stomp/stompjs';
import {  StompSubscription } from '@stomp/stompjs';

@Component({
  selector: 'app-all-machines',
  templateUrl: './all-machines.component.html',
  styleUrls: ['./all-machines.component.css']
})

export class AllMachinesComponent implements OnInit, OnDestroy {
  searchForm: FormGroup;
  machineSearchParameters: MachineSearchParameters;
  someSubscription: any;
  machineList: any;
  runningStatus: boolean;
  stoppedStatus: boolean;
  userRoles: Role[];
  private stompClient: CompatClient | undefined;
  private subscriptions: StompSubscription[] = [];



  constructor(
    private machineService: MachineService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.router = router;
    this.userRoles = [];
    this.machineList = [];
    this.runningStatus = false;
    this.stoppedStatus = false;

    this.stompClient = Stomp.client('ws://localhost:8080/ws'); // Promenite adresu i port prema vašem bekendu
    this.stompClient.connect({}, () => {
      this.subscribeToMachineStatus();
    });

    this.searchForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      dateFrom: ['', [Validators.required]],
      dateTo: ['', [Validators.required]],
    });

    this.machineSearchParameters = {
      name: '',
      dateFrom: null,
      dateTo: null
    };

    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };
    this.someSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.router.navigated = false;
      }
    });
  }

  
  private subscribeToMachineStatus(): void {
    if (this.stompClient) {
      const topic = '/topic/machine-status';
      const subscription = this.stompClient.subscribe(topic, (message: Message) => {
        // Ovde obradite primljenu poruku
        const machineStatus = JSON.parse(message.body);
        console.log('Received machine status update:', machineStatus);
  
        const machineToUpdate: any = this.machineList.find((machine: any) => machine.id === machineStatus.id);

        if (machineToUpdate) {
          // Ažuriramo status mašine
          machineToUpdate.status = machineStatus.status; // Pretpostavljamo da je status u primljenoj poruci
          this.machineList = [...this.machineList]; // Ažurirajte listu kako bi Angular primetio promenu i osvežio prikaz
        }
        
      });
      this.subscriptions.push(subscription);
    }
  }

  ngOnDestroy(): void {
    this.someSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.loadUserRoles();
    this.loadMachines();
  }

  private loadUserRoles(): void {
    this.userRoles = JSON.parse(localStorage.getItem('userRoles') || '[]');
  }

  private loadMachines(): void {
    const userMail = localStorage.getItem('userMail');
    if (userMail) {
      this.machineService.getAll(userMail).subscribe(result => {
        this.machineList = result.filter(machine => machine.active);
      });
    }
  }

  hasPermission(permission: string): boolean {
    return this.userRoles.some(role => role.name === permission);
  }

  search(): void {
    let statusString: string | null = null;
    if (this.runningStatus && this.stoppedStatus) {
      statusString = 'RUNNING,STOPPED';
    } else if (!this.runningStatus && !this.stoppedStatus) {
      statusString = null;
    } else {
      statusString = '';
      if (this.runningStatus) {
        statusString += 'RUNNING';
      }
      if (this.stoppedStatus) {
        statusString += 'STOPPED';
      }
    }

    const userMail = localStorage.getItem('userMail');
    if (userMail) {
      this.machineService.searchMachines(
        userMail,
        this.machineSearchParameters.name,
        statusString,
        this.machineSearchParameters.dateFrom,
        this.machineSearchParameters.dateTo
      ).subscribe(result => {
        this.machineList = result;
      });
    }
  }

  startMachine(id: number): void {
    this.machineService.startMachine(id).subscribe(() => {
      this.loadMachines();
    });
  }

  stopMachine(id: number): void {
    this.machineService.stopMachine(id).subscribe(() => {
      this.loadMachines();
    });
  }

  restartMachine(id: number): void {
    this.machineService.restartMachine(id).subscribe(() => {
      this.loadMachines();
    });
  }

  destroyMachine(id: number): void {
    this.machineService.destroyMachine(id).subscribe(() => {
      this.loadMachines();
    });
  }

  logOut(): void {
    localStorage.setItem('token', '');
    localStorage.setItem('roles', '');
  }
}
