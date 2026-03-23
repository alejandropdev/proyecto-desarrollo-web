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

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'menu', component: MenuComponent },
  { path: 'desafios', component: DesafiosComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'admin/platos', component: PlatosListComponent },
  { path: 'admin/platos/nuevo', component: PlatoFormComponent },
  { path: 'admin/platos/:id/editar', component: PlatoFormComponent },
  { path: 'admin/platos/:id', component: PlatoDetailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }