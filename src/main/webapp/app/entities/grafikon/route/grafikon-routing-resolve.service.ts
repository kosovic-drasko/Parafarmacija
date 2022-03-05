import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGrafikon, Grafikon } from '../grafikon.model';
import { GrafikonService } from '../service/grafikon.service';

@Injectable({ providedIn: 'root' })
export class GrafikonRoutingResolveService implements Resolve<IGrafikon> {
  constructor(protected service: GrafikonService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGrafikon> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((grafikon: HttpResponse<Grafikon>) => {
          if (grafikon.body) {
            return of(grafikon.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Grafikon());
  }
}
