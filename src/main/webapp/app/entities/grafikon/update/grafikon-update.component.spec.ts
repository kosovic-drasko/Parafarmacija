import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GrafikonService } from '../service/grafikon.service';
import { IGrafikon, Grafikon } from '../grafikon.model';

import { GrafikonUpdateComponent } from './grafikon-update.component';

describe('Grafikon Management Update Component', () => {
  let comp: GrafikonUpdateComponent;
  let fixture: ComponentFixture<GrafikonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let grafikonService: GrafikonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GrafikonUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GrafikonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GrafikonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    grafikonService = TestBed.inject(GrafikonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const grafikon: IGrafikon = { id: 456 };

      activatedRoute.data = of({ grafikon });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(grafikon));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Grafikon>>();
      const grafikon = { id: 123 };
      jest.spyOn(grafikonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grafikon });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: grafikon }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(grafikonService.update).toHaveBeenCalledWith(grafikon);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Grafikon>>();
      const grafikon = new Grafikon();
      jest.spyOn(grafikonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grafikon });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: grafikon }));
      saveSubject.complete();

      // THEN
      expect(grafikonService.create).toHaveBeenCalledWith(grafikon);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Grafikon>>();
      const grafikon = { id: 123 };
      jest.spyOn(grafikonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grafikon });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(grafikonService.update).toHaveBeenCalledWith(grafikon);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
