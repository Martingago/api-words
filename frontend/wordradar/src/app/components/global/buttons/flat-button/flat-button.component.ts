import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-flat-button',
  imports: [],
  templateUrl: './flat-button.component.html',
  styleUrl: './flat-button.component.css'
})
export class FlatButtonComponent {
  @Output() onClick = new EventEmitter<void>(); // Evento al hacer clic
  handleClick() {
    this.onClick.emit(); // Emite el evento cuando se hace clic
  }

}
