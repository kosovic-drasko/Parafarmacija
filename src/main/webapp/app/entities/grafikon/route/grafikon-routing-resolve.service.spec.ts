import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IGrafikon, Grafikon } from '../grafikon.model';
import { GrafikonService } from '../service/grafikon.service';

import { GrafikonRoutingResolveService } from './grafikon-routing-resolve.service';

describe('Grafikon routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GrafikonRoutingResolveService;
  let service: GrafikonService;
  let resultGrafikon: IGrafikon | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(GrafikonRoutingResolveService);
    service = TestBed.inject(GrafikonService);
    resultGrafikon = undefined;
  });

  describe('resolve', () => {
    it('should return IGrafikon returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGrafikon = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGrafikon).toEqual({ id: 123 });
    });

    it('should return new IGrafikon if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGrafikon = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGrafikon).toEqual(new Grafikon());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Grafikon })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGrafikon = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGrafikon).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
