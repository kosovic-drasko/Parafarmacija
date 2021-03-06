import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GrafikonComponent } from './list/grafikon.component';
import { GrafikonDetailComponent } from './detail/grafikon-detail.component';
import { GrafikonUpdateComponent } from './update/grafikon-update.component';
import { GrafikonDeleteDialogComponent } from './delete/grafikon-delete-dialog.component';
import { GrafikonRoutingModule } from './route/grafikon-routing.module';
import { JhMaterialModule } from '../../shared/jh-material.module';

@NgModule({
  imports: [SharedModule, GrafikonRoutingModule, JhMaterialModule],
  declarations: [GrafikonComponent, GrafikonDetailComponent, GrafikonUpdateComponent, GrafikonDeleteDialogComponent],
  entryComponents: [GrafikonDeleteDialogComponent],
})
export class GrafikonModule {}
