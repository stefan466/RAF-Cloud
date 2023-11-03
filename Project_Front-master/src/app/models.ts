
export interface UserLoginInfo{
    mail: string;
    password: string;
  }
  
  export interface Role{
    id: number
    name: string
  }
  
  export interface UserLoginResponse {
    jwt: string
    roles: Role[]
  }
  
  export interface User{
    id: number
    lastName: string
    mail: string
    name: string
    roles: Role[]
  }
  
  export interface NewUser{
    id: number
    lastName: string
    mail: string
    password: string
    name: string
    roles: Role[]
  }
  
  export interface Machine {
    id: number
    name: string
    creationDate: Date
    status: Status
    active: boolean
    user: User
  }
  
  export interface MachineSearchParameters{
    name: string
    dateFrom: any
    dateTo: any
  }
  
  export interface ScheduleParameters{
    id: number
    date: string
    time: string
    action: string
  }
  
  export enum Status {
    STOPPED,
    RUNNING
  }
  
  export interface ErrorMessage {
    id: number
    message: string
    action: string
    date: Date
    machine: Machine
  }
  
  export interface CreateRequest{
    name: string
    mail: string
  }
  
  
  
  