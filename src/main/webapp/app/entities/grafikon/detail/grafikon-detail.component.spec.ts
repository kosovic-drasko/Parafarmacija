import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GrafikonDetailComponent } from './grafikon-detail.component';

describe('Grafikon Management Detail Component', () => {
  let comp: GrafikonDetailComponent;
  let fixture: ComponentFixture<GrafikonDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GrafikonDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ grafikon: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GrafikonDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GrafikonDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load grafikon on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.grafikon).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
