import { Component } from '@angular/core';
import { FlatButtonComponent } from "../global/buttons/flat-button/flat-button.component";
import { StrokedButtonComponent } from '../global/buttons/stroked-button/stroked-button.component';

@Component({
  selector: 'app-call-to-action',
  imports: [FlatButtonComponent, StrokedButtonComponent],
  templateUrl: './call-to-action.component.html',
  styleUrl: './call-to-action.component.css'
})
export class CallToActionComponent {
  
  // Abre la URL en una nueva pestaña
  abrirEnlace(url: string) {
    window.open(url, '_blank'); 
  }
}
