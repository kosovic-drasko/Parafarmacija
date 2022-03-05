import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GrafikonComponent } from '../list/grafikon.component';
import { GrafikonDetailComponent } from '../detail/grafikon-detail.component';
import { GrafikonUpdateComponent } from '../update/grafikon-update.component';
import { GrafikonRoutingResolveService } from './grafikon-routing-resolve.service';

const grafikonRoute: Routes = [
  {
    path: '',
    component: GrafikonComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GrafikonDetailComponent,
    resolve: {
      grafikon: GrafikonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GrafikonUpdateComponent,
    resolve: {
      grafikon: GrafikonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GrafikonUpdateComponent,
    resolve: {
      grafikon: GrafikonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(grafikonRoute)],
  exports: [RouterModule],
})
export class GrafikonRoutingModule {}
