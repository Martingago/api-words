import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WebStatComponent } from './web-stat.component';

describe('WebStatComponent', () => {
  let component: WebStatComponent;
  let fixture: ComponentFixture<WebStatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WebStatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WebStatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
