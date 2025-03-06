import { TestBed } from '@angular/core/testing';

import { ApiTryItService } from './api-try-it.service';

describe('ApiTryItService', () => {
  let service: ApiTryItService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiTryItService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
