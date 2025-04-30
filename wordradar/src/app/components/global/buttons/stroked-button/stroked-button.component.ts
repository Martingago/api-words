import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-stroked-button',
  imports: [],
  templateUrl: './stroked-button.component.html',
  styleUrl: './stroked-button.component.css'
})
export class StrokedButtonComponent {
  @Output() onClick = new EventEmitter<void>(); // Evento al hacer clic
  handleClick() {
    this.onClick.emit(); // Emite el evento cuando se hace clic
  }
}
