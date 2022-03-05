import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGrafikon } from '../grafikon.model';

@Component({
  selector: 'jhi-grafikon-detail',
  templateUrl: './grafikon-detail.component.html',
})
export class GrafikonDetailComponent implements OnInit {
  grafikon: IGrafikon | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ grafikon }) => {
      this.grafikon = grafikon;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
