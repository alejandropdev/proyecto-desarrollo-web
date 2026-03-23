import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-admin-page-shell',
  templateUrl: './admin-page-shell.component.html',
  styleUrls: ['./admin-page-shell.component.css']
})
export class AdminPageShellComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() actionLabel = '';
  @Input() actionLink = '';
}
