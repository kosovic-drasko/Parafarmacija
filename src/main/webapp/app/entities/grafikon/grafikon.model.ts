export interface IGrafikon {
  id?: number;
  region?: string | null;
  promet?: number | null;
}

export class Grafikon implements IGrafikon {
  constructor(public id?: number, public region?: string | null, public promet?: number | null) {}
}

export function getGrafikonIdentifier(grafikon: IGrafikon): number | undefined {
  return grafikon.id;
}
