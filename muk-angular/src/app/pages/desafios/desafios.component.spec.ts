import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesafiosComponent } from './desafios.component';

describe('DesafiosComponent', () => {
  let component: DesafiosComponent;
  let fixture: ComponentFixture<DesafiosComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DesafiosComponent]
    });
    fixture = TestBed.createComponent(DesafiosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
