import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGrafikon } from '../grafikon.model';
import { GrafikonService } from '../service/grafikon.service';

@Component({
  templateUrl: './grafikon-delete-dialog.component.html',
})
export class GrafikonDeleteDialogComponent {
  grafikon?: IGrafikon;

  constructor(protected grafikonService: GrafikonService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.grafikonService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
