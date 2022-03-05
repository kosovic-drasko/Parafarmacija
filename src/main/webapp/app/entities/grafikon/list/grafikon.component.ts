import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGrafikon } from '../grafikon.model';
import { GrafikonService } from '../service/grafikon.service';
import { GrafikonDeleteDialogComponent } from '../delete/grafikon-delete-dialog.component';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'jhi-grafikon',
  templateUrl: './grafikon.component.html',
  styleUrls: ['./grfikon.component.css'],
})
export class GrafikonComponent implements OnInit {
  grafikons?: IGrafikon[];
  isLoading = false;
  chart: any = [];
  constructor(protected grafikonService: GrafikonService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.grafikonService.query().subscribe({
      next: (res: HttpResponse<IGrafikon[]>) => {
        this.isLoading = false;
        this.grafikons = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ucitajGrafikon(): void {
    this.grafikonService.grafikon().subscribe((res: any[]) => {
      const region = res.map((res: { region: string }) => res.region);
      const promet = res.map((res: { promet: number }) => res.promet);
      // eslint-disable-next-line no-console
      console.log(region);
      // eslint-disable-next-line no-console
      console.log(promet);
      this.chart = new Chart('canvas', {
        // type: 'bar',

        type: 'pie',
        options: {
          // horizontalna varijanta
          indexAxis: 'y',
          plugins: {
            legend: {
              display: true,
              labels: {
                color: 'rgb (255, 99, 132)',
              },
            },
          },
          scales: {
            y: {
              beginAtZero: true,
            },
          },
        },

        data: {
          labels: region,
          datasets: [
            {
              data: promet,
              backgroundColor: ['red', 'yellow'],
              borderColor: '#3cba9f',
              borderWidth: 1,
            },
          ],
        },
      });
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.ucitajGrafikon();
  }
  print(): any {
    window.print();
  }
  trackId(index: number, item: IGrafikon): number {
    return item.id!;
  }

  delete(grafikon: IGrafikon): void {
    const modalRef = this.modalService.open(GrafikonDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.grafikon = grafikon;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
