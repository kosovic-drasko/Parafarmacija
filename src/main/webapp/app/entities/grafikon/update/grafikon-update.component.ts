import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGrafikon, Grafikon } from '../grafikon.model';
import { GrafikonService } from '../service/grafikon.service';

@Component({
  selector: 'jhi-grafikon-update',
  templateUrl: './grafikon-update.component.html',
})
export class GrafikonUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    region: [],
    promet: [],
  });

  constructor(protected grafikonService: GrafikonService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ grafikon }) => {
      this.updateForm(grafikon);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const grafikon = this.createFromForm();
    if (grafikon.id !== undefined) {
      this.subscribeToSaveResponse(this.grafikonService.update(grafikon));
    } else {
      this.subscribeToSaveResponse(this.grafikonService.create(grafikon));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGrafikon>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(grafikon: IGrafikon): void {
    this.editForm.patchValue({
      id: grafikon.id,
      region: grafikon.region,
      promet: grafikon.promet,
    });
  }

  protected createFromForm(): IGrafikon {
    return {
      ...new Grafikon(),
      id: this.editForm.get(['id'])!.value,
      region: this.editForm.get(['region'])!.value,
      promet: this.editForm.get(['promet'])!.value,
    };
  }
}
