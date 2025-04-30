import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-link',
  imports: [],
  templateUrl: './link.component.html',
  styleUrl: './link.component.css'
})
export class LinkComponent {
  @Input() nombre: string = ''; // Texto del enlace
  @Input() path: string = '/'; // URL del enlace
}
