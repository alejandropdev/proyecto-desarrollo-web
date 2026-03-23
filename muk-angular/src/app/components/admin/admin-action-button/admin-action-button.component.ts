import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-admin-action-button',
  templateUrl: './admin-action-button.component.html'
})
export class AdminActionButtonComponent {
  @Input() label = '';
  @Input() kind: 'view' | 'edit' | 'delete' | 'primary' | 'outline' = 'primary';
  @Input() type: 'button' | 'submit' = 'button';
}
