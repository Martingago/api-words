import { Component, Output, EventEmitter, Input } from '@angular/core';

@Component({
  selector: 'app-flat-button',
  imports: [],
  templateUrl: './flat-button.component.html',
  styleUrl: './flat-button.component.css',
})
export class FlatButtonComponent {
  @Output() onClick = new EventEmitter<void>(); // Evento al hacer clic
  @Input() targetId?: string; // <-- Nuevo input opcional
  handleClick() {
    this.onClick.emit(); // Emite el evento cuando se hace clic
    if (this.targetId) {
      const element = document.getElementById(this.targetId);
      if (element) {
              const yOffset = -90; // altura del header
      const y = element.getBoundingClientRect().top + window.pageYOffset + yOffset;
      window.scrollTo({ top: y, behavior: 'smooth' });
      }
    }
  }
}
