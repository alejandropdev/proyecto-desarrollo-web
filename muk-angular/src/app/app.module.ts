import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

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
import { AdicionesListComponent } from './pages/admin/adiciones/adiciones-list/adiciones-list.component';
import { AdicionFormComponent } from './pages/admin/adiciones/adicion-form/adicion-form.component';
import { AdicionDetailComponent } from './pages/admin/adiciones/adicion-detail/adicion-detail.component';

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
    AdicionesListComponent,
    AdicionFormComponent,
    AdicionDetailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }