import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGrafikon, getGrafikonIdentifier } from '../grafikon.model';
import { map } from 'rxjs/operators';

export type EntityResponseType = HttpResponse<IGrafikon>;
export type EntityArrayResponseType = HttpResponse<IGrafikon[]>;

@Injectable({ providedIn: 'root' })
export class GrafikonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/grafikons');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  grafikon(): any {
    return this.http.get(this.resourceUrl).pipe(map(result => result));
  }

  create(grafikon: IGrafikon): Observable<EntityResponseType> {
    return this.http.post<IGrafikon>(this.resourceUrl, grafikon, { observe: 'response' });
  }

  update(grafikon: IGrafikon): Observable<EntityResponseType> {
    return this.http.put<IGrafikon>(`${this.resourceUrl}/${getGrafikonIdentifier(grafikon) as number}`, grafikon, { observe: 'response' });
  }

  partialUpdate(grafikon: IGrafikon): Observable<EntityResponseType> {
    return this.http.patch<IGrafikon>(`${this.resourceUrl}/${getGrafikonIdentifier(grafikon) as number}`, grafikon, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGrafikon>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGrafikon[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGrafikonToCollectionIfMissing(grafikonCollection: IGrafikon[], ...grafikonsToCheck: (IGrafikon | null | undefined)[]): IGrafikon[] {
    const grafikons: IGrafikon[] = grafikonsToCheck.filter(isPresent);
    if (grafikons.length > 0) {
      const grafikonCollectionIdentifiers = grafikonCollection.map(grafikonItem => getGrafikonIdentifier(grafikonItem)!);
      const grafikonsToAdd = grafikons.filter(grafikonItem => {
        const grafikonIdentifier = getGrafikonIdentifier(grafikonItem);
        if (grafikonIdentifier == null || grafikonCollectionIdentifiers.includes(grafikonIdentifier)) {
          return false;
        }
        grafikonCollectionIdentifiers.push(grafikonIdentifier);
        return true;
      });
      return [...grafikonsToAdd, ...grafikonCollection];
    }
    return grafikonCollection;
  }
}
