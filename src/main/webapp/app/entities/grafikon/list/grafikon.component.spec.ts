import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GrafikonService } from '../service/grafikon.service';

import { GrafikonComponent } from './grafikon.component';

describe('Grafikon Management Component', () => {
  let comp: GrafikonComponent;
  let fixture: ComponentFixture<GrafikonComponent>;
  let service: GrafikonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [GrafikonComponent],
    })
      .overrideTemplate(GrafikonComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GrafikonComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GrafikonService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.grafikons?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
