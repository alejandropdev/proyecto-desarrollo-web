import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-admin-form-field',
  templateUrl: './admin-form-field.component.html',
  styleUrls: ['./admin-form-field.component.css']
})
export class AdminFormFieldComponent {
  @Input() label = '';
  @Input() fieldId = '';
  @Input() hint = '';
}
