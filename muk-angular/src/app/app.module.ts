import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import localeEsCo from '@angular/common/locales/es-CO';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';

import { HomeComponent } from './pages/home/home.component';
import { MenuComponent } from './pages/menu/menu.component';
import { DesafiosComponent } from './pages/desafios/desafios.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroComponent } from './pages/registro/registro.component';

import { AdminNavbarComponent } from './components/admin/admin-navbar/admin-navbar.component';
import { AdminPageShellComponent } from './components/admin/admin-page-shell/admin-page-shell.component';
import { AdminEmptyStateComponent } from './components/admin/admin-empty-state/admin-empty-state.component';
import { AdminActionButtonComponent } from './components/admin/admin-action-button/admin-action-button.component';
import { AdminFormFieldComponent } from './components/admin/admin-form-field/admin-form-field.component';

import { PlatosListComponent } from './pages/admin/platos/platos-list/platos-list.component';
import { PlatoFormComponent } from './pages/admin/platos/plato-form/plato-form.component';
import { PlatoDetailComponent } from './pages/admin/platos/plato-detail/plato-detail.component';
import { AdminLoginComponent } from './pages/admin/login/admin-login.component';
import { OperadoresComponent } from './pages/admin/operadores/operadores.component';
import { CategoriasComponent } from './pages/admin/categorias/categorias.component';
import { ProductosComponent } from './pages/admin/productos/productos.component';

import { AdicionesListComponent } from './pages/admin/adiciones/adiciones-list/adiciones-list.component';
import { AdicionFormComponent } from './pages/admin/adiciones/adicion-form/adicion-form.component';
import { AdicionDetailComponent } from './pages/admin/adiciones/adicion-detail/adicion-detail.component';
import { AdminPedidosComponent } from './pages/admin/pedidos/admin-pedidos.component';

import { DomiciliarioFormComponent } from './components/domiciliarios/domiciliario-form.component';
import { DomiciliariosComponent } from './components/domiciliarios/domiciliarios.component';

import { ComidaComponent } from './pages/comida/comida.component';
import { UbicacionComponent } from './pages/ubicacion/ubicacion.component';
import { PerfilComponent } from './pages/clientes/perfil/perfil.component';

import { CrearPedidoComponent } from './pages/pedidos/crear-pedido/crear-pedido.component';
import { DetallePedidoComponent } from './pages/pedidos/detalle-pedido/detalle-pedido.component';
import { MisPedidosComponent } from './pages/pedidos/mis-pedidos/mis-pedidos.component';

import { PedidoPortalComponent } from './pages/operario/pedidos/pedido-portal.component';
import { OperarioLoginComponent } from './pages/operario/login/operario-login.component';

import { NotFoundComponent } from './pages/not-found/not-found.component';

registerLocaleData(localeEsCo);

import { authInterceptorProviders } from './services/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,

    HomeComponent,
    MenuComponent,
    DesafiosComponent,
    LoginComponent,
    RegistroComponent,

    AdminNavbarComponent,
    AdminPageShellComponent,
    AdminEmptyStateComponent,
    AdminActionButtonComponent,
    AdminFormFieldComponent,

    PlatosListComponent,
    PlatoFormComponent,
    PlatoDetailComponent,
    AdminLoginComponent,
    OperadoresComponent,
    CategoriasComponent,
    ProductosComponent,

    AdicionesListComponent,
    AdicionFormComponent,
    AdicionDetailComponent,
    AdminPedidosComponent,

    DomiciliarioFormComponent,
    DomiciliariosComponent,

    ComidaComponent,
    UbicacionComponent,
    PerfilComponent,

    CrearPedidoComponent,
    DetallePedidoComponent,
    MisPedidosComponent,

    PedidoPortalComponent,
    OperarioLoginComponent,

    NotFoundComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    AppRoutingModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'es-CO' },
    authInterceptorProviders
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}