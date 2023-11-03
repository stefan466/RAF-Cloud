import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllMachinesComponent } from './all-machines.component';

describe('AllMachinesComponent', () => {
  let component: AllMachinesComponent;
  let fixture: ComponentFixture<AllMachinesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllMachinesComponent]
    });
    fixture = TestBed.createComponent(AllMachinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
