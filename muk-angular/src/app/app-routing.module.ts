import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { MenuComponent } from './pages/menu/menu.component';
import { DesafiosComponent } from './pages/desafios/desafios.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroComponent } from './pages/registro/registro.component';
import { PlatoDetailComponent } from './pages/admin/platos/plato-detail/plato-detail.component';
import { PlatoFormComponent } from './pages/admin/platos/plato-form/plato-form.component';
import { PlatosListComponent } from './pages/admin/platos/platos-list/platos-list.component';
import { ComidaComponent } from './pages/comida/comida.component';
import { UbicacionComponent } from './pages/ubicacion/ubicacion.component';
import { PerfilComponent } from './pages/clientes/perfil/perfil.component';
import { AdminLoginComponent } from './pages/admin/login/admin-login.component';
import { OperadoresComponent } from './pages/admin/operadores/operadores.component';
import { CategoriasComponent } from './pages/admin/categorias/categorias.component';
import { ProductosComponent } from './pages/admin/productos/productos.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'menu', component: MenuComponent },
  { path: 'comida/:id', component: ComidaComponent },
  { path: 'ubicacion', component: UbicacionComponent },
  { path: 'desafios', component: DesafiosComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'clientes/perfil', component: PerfilComponent },
  { path: 'admin/login', component: AdminLoginComponent },
  { path: 'admin/platos', component: PlatosListComponent },
  { path: 'admin/platos/nuevo', component: PlatoFormComponent },
  { path: 'admin/platos/:id/editar', component: PlatoFormComponent },
  { path: 'admin/platos/:id', component: PlatoDetailComponent },
  { path: 'operadores', component: OperadoresComponent },
  { path: 'categorias', component: CategoriasComponent },
  { path: 'productos', component: ProductosComponent },
  { path: 'not-found', component: NotFoundComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }