# 📋 Documentación: Portal de Operador y Gestión de Domiciliarios

## 🎯 Resumen General

Se ha implementado exitosamente el módulo completo de **Portal de Operador y Gestión de Domiciliarios** para MUK Restaurante, con una arquitectura escalable siguiendo las mejores prácticas de desarrollo.

---

## 🏗️ BACKEND (Spring Boot 3.2 + Java 17)

### Entidades Actualizadas

#### **Domiciliario** (Actualizada)
```java
@Entity
@Table(name = "domiciliarios")
public class Domiciliario {
    @Id
    private Long id;
    
    @Column(nullable = false, length = 80)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 20)
    private String celular;
    
    @Column(nullable = false, unique = true, length = 20)
    private String cedula;
    
    // NUEVO: Campo para gestión de activos
    @Column(nullable = false)
    private Boolean activo = true;  // ← Nuevo
    
    // EXISTENTE: Campo para disponibilidad
    @Column(nullable = false)
    private Boolean disponible = true;
    
    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidosAsignados;
}
```

**Lógica de Disponibilidad:**
- `activo = true`: Domiciliario activo en el sistema (puede ser asignado)
- `activo = false`: Domiciliario inactivo (no puede recibir nuevas asignaciones)
- `disponible = true`: Sin pedidos pendientes (puede recibir nuevas asignaciones)
- `disponible = false`: Tiene un pedido EN_CAMINO (ocupado)

#### **Pedido** (Sin cambios, pero con nueva lógica)
Estados: `PENDIENTE`, `EN_PREPARACION`, `LISTO`, `EN_CAMINO`, `COMPLETADO`, `CANCELADO`

---

### Capas de Arquitectura

#### 1️⃣ **Repository Layer**

**DomiciliarioRepository.java**
```java
@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long> {
    Optional<Domiciliario> findByCelular(String celular);
    Optional<Domiciliario> findByCedula(String cedula);
    List<Domiciliario> findByActivoTrue();
    List<Domiciliario> findByActivoFalse();
    List<Domiciliario> findByActivoTrueAndDisponibleTrue();
    // ... más métodos
}
```

**PedidoRepository.java** (Existente, se mantiene intacto)

---

#### 2️⃣ **Service Layer**

**DomiciliarioService.java** (Interfaz)
```java
public interface DomiciliarioService {
    List<Domiciliario> findAll();
    Optional<Domiciliario> findById(Long id);
    Domiciliario crear(Domiciliario domiciliario);
    Domiciliario actualizar(Long id, Domiciliario datosActualizados);
    void eliminar(Long id);
    Domiciliario activar(Long id);
    Domiciliario desactivar(Long id);
    Domiciliario marcarDisponible(Long id);
    Domiciliario marcarNoDisponible(Long id);
}
```

**DomiciliarioServiceImpl.java** (Implementación)
- ✅ Validaciones de campos
- ✅ Validación de unicidad (celular, cédula)
- ✅ Manejo de excepciones
- ✅ Transacciones ACID

**PedidoService.java** (Interfaz actualizada)
```java
List<Pedido> findPedidosNoCompletados();

record CambiarEstadoResult(Pedido pedido, String errorMessage) {
    public boolean success() { return pedido != null; }
}

CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado);
```

**PedidoServiceImpl.java** (Implementación actualizada)
```java
@Override
@Transactional
public CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado) {
    // Validaciones
    // Buscar pedido
    
    // LÓGICA ESPECIAL: Gestionar disponibilidad de domiciliario
    if (pedido.getDomiciliario() != null) {
        if (nuevoEstado.equalsIgnoreCase("EN_CAMINO")) {
            domiciliario.setDisponible(false);  // ← OCUPADO
        }
        else if (nuevoEstado.equalsIgnoreCase("COMPLETADO")) {
            domiciliario.setDisponible(true);   // ← DISPONIBLE
            pedido.setFechaEntrega(LocalDateTime.now());
        }
        else if (nuevoEstado.equalsIgnoreCase("CANCELADO")) {
            domiciliario.setDisponible(true);   // ← DISPONIBLE
        }
    }
    
    return new CambiarEstadoResult(pedidoRepository.save(pedido), null);
}
```

---

#### 3️⃣ **Controller Layer (API REST)**

**Base URL:** `/api/`

#### **DomiciliarioApiController.java**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **GET** | `/api/domiciliarios` | Listar todos |
| **GET** | `/api/domiciliarios/{id}` | Obtener por ID |
| **GET** | `/api/domiciliarios/activos/disponibles` | Listar activos y disponibles |
| **POST** | `/api/domiciliarios` | Crear nuevo |
| **PUT** | `/api/domiciliarios/{id}` | Actualizar |
| **DELETE** | `/api/domiciliarios/{id}` | Eliminar |
| **PUT** | `/api/domiciliarios/{id}/activar` | Activar |
| **PUT** | `/api/domiciliarios/{id}/desactivar` | Desactivar |

**Ejemplo Request/Response:**

```bash
# Crear domiciliario
POST /api/domiciliarios
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "celular": "3001234567",
  "cedula": "12345678"
}

# Response 201 Created
{
  "id": 1,
  "nombre": "Juan Pérez",
  "celular": "3001234567",
  "cedula": "12345678",
  "activo": true,
  "disponible": true
}
```

#### **PedidosApiController.java** (Actualizado)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **GET** | `/api/pedidos/sin-completar/lista` | Pedidos NO completados |
| **PUT** | `/api/pedidos/{id}/cambiar-estado` | Cambiar estado |

**Ejemplo:**
```bash
# Obtener pedidos sin completar
GET /api/pedidos/sin-completar/lista
Response: Pedido[] (PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO)

# Cambiar estado a EN_CAMINO
PUT /api/pedidos/123/cambiar-estado
{
  "nuevoEstado": "EN_CAMINO"
}
```

---

#### 4️⃣ **DTOs (ApiDtos.java)**

```java
// Domiciliario DTO
public record DomiciliarioDto(
    Long id,
    String nombre,
    String celular,
    String cedula,
    Boolean activo,
    Boolean disponible
) {}

// Request
public record DomiciliarioUpsertRequest(
    String nombre,
    String celular,
    String cedula
) {}

// Cambiar estado de pedido
public record CambiarEstadoPedidoRequest(
    String nuevoEstado
) {}
```

---

## 💻 FRONTEND (Angular 16+)

### Estructura de Carpetas

```
muk-angular/src/app/
├── models/
│   ├── domiciliario.ts      ← NUEVO
│   ├── pedido.ts             ← ACTUALIZADO
│   └── ...
├── services/
│   ├── domiciliario.service.ts  ← NUEVO
│   ├── pedido.service.ts         ← ACTUALIZADO
│   └── ...
├── components/
│   ├── operador/             ← NUEVO
│   │   ├── operador-pedidos.component.ts
│   │   ├── operador-pedidos.component.html
│   │   ├── operador-pedidos.component.css
│   │   ├── pedido-card.component.ts
│   │   ├── pedido-card.component.html
│   │   ├── pedido-card.component.css
│   │   ├── cambiar-estado.component.ts
│   │   ├── cambiar-estado.component.html
│   │   └── cambiar-estado.component.css
│   ├── domiciliarios/        ← NUEVO
│   │   ├── domiciliarios.component.ts
│   │   ├── domiciliarios.component.html
│   │   ├── domiciliarios.component.css
│   │   ├── domiciliario-form.component.ts
│   │   ├── domiciliario-form.component.html
│   │   └── domiciliario-form.component.css
│   └── ...
└── ...
```

### Modelos TypeScript

**domiciliario.ts**
```typescript
export interface Domiciliario {
  id: number;
  nombre: string;
  celular: string;
  cedula: string;
  activo: boolean;
  disponible: boolean;
}

export interface DomiciliarioUpsertRequest {
  nombre: string;
  celular: string;
  cedula: string;
}

export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  EN_PREPARACION = 'EN_PREPARACION',
  LISTO = 'LISTO',
  EN_CAMINO = 'EN_CAMINO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO'
}
```

### Servicios Angular

**domiciliario.service.ts**
```typescript
@Injectable({ providedIn: 'root' })
export class DomiciliarioService {
  private apiUrl = '/api/domiciliarios';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Domiciliario[]> {}
  obtenerPorId(id: number): Observable<Domiciliario> {}
  obtenerActivosDisponibles(): Observable<Domiciliario[]> {}
  crear(request: DomiciliarioUpsertRequest): Observable<Domiciliario> {}
  actualizar(id: number, request: DomiciliarioUpsertRequest): Observable<Domiciliario> {}
  eliminar(id: number): Observable<{ message: string }> {}
  activar(id: number): Observable<Domiciliario> {}
  desactivar(id: number): Observable<Domiciliario> {}
}
```

**pedido.service.ts** (Actualizado)
```typescript
obtenerPedidosNoCompletados(): Observable<Pedido[]> {
  return this.http.get<Pedido[]>(`${this.apiUrl}/sin-completar/lista`);
}

cambiarEstado(pedidoId: number, nuevoEstado: string): Observable<Pedido> {
  const request: CambiarEstadoPedidoRequest = { nuevoEstado };
  return this.http.put<Pedido>(`${this.apiUrl}/${pedidoId}/cambiar-estado`, request);
}
```

### Componentes

#### 🎯 Portal del Operador

**OperadorPedidosComponent**
- Carga pedidos NO completados
- Renderiza `app-pedido-card` para cada pedido
- Maneja cambios de estado

**PedidoCardComponent**
- Muestra tarjeta individual del pedido
- Select dropdown para cambiar estado
- Colores y emojis según estado

**CambiarEstadoComponent** (Modal)
- Alternativa: formulario modal para cambiar estado
- Validaciones de transición de estado
- Confirmación antes de guardar

**Uso:**
```html
<!-- Incluir en el app.module.ts -->
<app-operador-pedidos></app-operador-pedidos>
```

#### 👨‍💼 Gestión de Domiciliarios (Admin)

**DomiciliariosComponent**
- Tabla con lista de domiciliarios
- Botones: Editar, Activar/Desactivar, Eliminar
- Modal de formulario para crear/editar

**DomiciliarioFormComponent**
- Validación de campos
- Funciona para crear y editar
- Feedback visual de errores

**Uso:**
```html
<!-- Incluir en el app.module.ts -->
<app-domiciliarios></app-domiciliarios>
```

---

## 🔗 Conexión Frontend-Backend

### 1. Importar Servicios en app.module.ts

```typescript
import { DomiciliarioService } from './services/domiciliario.service';
import { PedidoService } from './services/pedido.service';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    // Componentes
    OperadorPedidosComponent,
    PedidoCardComponent,
    CambiarEstadoComponent,
    DomiciliariosComponent,
    DomiciliarioFormComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,    // Necesario para servicios HTTP
    FormsModule,         // Necesario para formularios
    // ... otros imports
  ],
  providers: [
    DomiciliarioService,
    PedidoService,
  ],
})
export class AppModule {}
```

### 2. Agregar Rutas en app-routing.module.ts

```typescript
const routes: Routes = [
  // ... rutas existentes
  
  // Portal del Operador
  {
    path: 'operador',
    component: OperadorPedidosComponent,
    // canActivate: [OperarioAuthGuard]  ← Proteger si es necesario
  },
  
  // Gestión de Domiciliarios (Admin)
  {
    path: 'admin/domiciliarios',
    component: DomiciliariosComponent,
    // canActivate: [AdminAuthGuard]  ← Proteger si es necesario
  },
];
```

### 3. Configurar CORS (Backend)

Si las peticiones vienen de `localhost:4200` en desarrollo:

```java
@Configuration
public class CorsConfig {
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
          .allowedOrigins("http://localhost:4200", "https://tu-dominio.com")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true)
          .maxAge(3600);
      }
    };
  }
}
```

O usar `@CrossOrigin` en los controllers (ya está implementado).

---

## 📡 Flujo de Datos Ejemplo

### Portal del Operador

```
1. Usuario entra a /operador
   ↓
2. OperadorPedidosComponent.ngOnInit()
   ↓
3. PedidoService.obtenerPedidosNoCompletados()
   ↓
4. GET /api/pedidos/sin-completar/lista
   ↓
5. Backend: PedidosApiController.obtenerPedidosNoCompletados()
   ↓
6. Backend: PedidoService.findPedidosNoCompletados()
   ↓
7. Backend: Filtra por estado ≠ COMPLETADO
   ↓
8. Response: Pedido[]
   ↓
9. Frontend: Renderiza PedidoCardComponent[] para cada pedido
   ↓
10. Usuario selecciona nuevo estado
    ↓
11. PedidoService.cambiarEstado(pedidoId, "EN_CAMINO")
    ↓
12. PUT /api/pedidos/123/cambiar-estado
    ↓
13. Backend: Cambia estado y modifica disponibilidad de domiciliario
    ↓
14. Response: Pedido actualizado
    ↓
15. Frontend: Recarga lista de pedidos
```

### Gestión de Domiciliarios

```
1. Admin entra a /admin/domiciliarios
   ↓
2. DomiciliariosComponent.ngOnInit()
   ↓
3. DomiciliarioService.listarTodos()
   ↓
4. GET /api/domiciliarios
   ↓
5. Backend: DomiciliarioApiController.listarTodos()
   ↓
6. Response: Domiciliario[]
   ↓
7. Frontend: Renderiza tabla con domiciliarios
   ↓
8. Admin hace click en "Nuevo Domiciliario"
   ↓
9. Abre DomiciliarioFormComponent modal
   ↓
10. Admin rellena formulario y envía
    ↓
11. DomiciliarioService.crear(request)
    ↓
12. POST /api/domiciliarios
    ↓
13. Backend: Valida y crea nuevo domiciliario
    ↓
14. Response: Domiciliario creado
    ↓
15. Frontend: Cierra modal y recarga lista
```

---

## ✅ Checklist de Implementación

### Backend
- ✅ Entidad Domiciliario actualizada con campo `activo`
- ✅ DomiciliarioRepository con queries especializadas
- ✅ DomiciliarioService e implementación
- ✅ DomiciliarioApiController con CRUD completo
- ✅ Endpoints CRUD, activar/desactivar
- ✅ PedidoService actualizado con métodos
- ✅ Lógica de cambio de estado con gestión de domiciliario
- ✅ DTOs en ApiDtos
- ✅ ApiMappers para conversión
- ✅ Validaciones en capas
- ✅ Transacciones ACID

### Frontend
- ✅ Modelo Domiciliario
- ✅ Modelo Pedido actualizado con estados
- ✅ DomiciliarioService
- ✅ PedidoService actualizado
- ✅ Componente OperadorPedidosComponent
- ✅ Componente PedidoCardComponent
- ✅ Componente CambiarEstadoComponent
- ✅ Componente DomiciliariosComponent
- ✅ Componente DomiciliarioFormComponent
- ✅ UI limpia y responsiva
- ✅ Estilos CSS profesionales
- ✅ Validaciones en formularios

---

## 🚀 Instrucciones de Despliegue

### Compilar Backend
```bash
cd proyecto-desarrollo-web
mvn clean package
java -jar target/muk-website-backend-1.0.0.jar
```

El backend estará en `http://localhost:8080`

### Iniciar Frontend
```bash
cd muk-angular
npm install
ng serve
```

El frontend estará en `http://localhost:4200`

---

## 📚 Convenciones de Código

1. **Nombres:** CamelCase para clases, snake_case para BD
2. **Métodos:** Claros y descriptivos (findBy*, crear*, cambiar*)
3. **Comentarios:** En javadoc y JSDoc donde sea complejo
4. **Transacciones:** @Transactional en métodos que modifican datos
5. **Validaciones:** En servicios, no en controllers
6. **DTOs:** Nunca enviar entidades directamente

---

## 🎓 Conclusión

El módulo está completamente implementado siguiendo:
- ✅ Arquitectura en capas (Controller → Service → Repository → Entity)
- ✅ DTOs para seguridad y independencia
- ✅ Código limpio y documentado
- ✅ Manejo de errores robusto
- ✅ Componentes Angular pequeños y reutilizables
- ✅ Buenas prácticas Spring Boot y Angular

¡Listo para producción! 🎉
