import { TestBed } from '@angular/core/testing';
import { PlatoService } from './plato.service';

describe('PlatoService', () => {
  let service: PlatoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlatoService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should return only active plates', () => {
    const initial = service.getPlatos();
    service.deletePlato(initial[0].id);
    const next = service.getPlatos();
    expect(next.length).toBe(initial.length - 1);
  });

  it('should create and update a plate', () => {
    const categoria = service.getCategorias()[0];
    const created = service.savePlato({
      nombre: 'Prueba',
      descripcion: 'Descripcion',
      precio: 15000,
      imagenUrl: 'https://example.com/image.jpg',
      categoria
    });

    expect(created.id).toBeGreaterThan(0);

    const updated = service.savePlato({
      id: created.id,
      nombre: 'Prueba editada',
      descripcion: 'Descripcion editada',
      precio: 18000,
      imagenUrl: 'https://example.com/image2.jpg',
      categoria
    });

    expect(updated.nombre).toBe('Prueba editada');
    expect(service.getPlatoById(created.id)?.precio).toBe(18000);
  });
});
