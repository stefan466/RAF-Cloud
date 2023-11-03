import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateMachineComponent } from './create-machine.component';

describe('CreateMachineComponent', () => {
  let component: CreateMachineComponent;
  let fixture: ComponentFixture<CreateMachineComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateMachineComponent]
    });
    fixture = TestBed.createComponent(CreateMachineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
