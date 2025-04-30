import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu',
  imports: [MatToolbarModule, MatIconModule, MatButtonModule, CommonModule],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  menuOpen = false; // Estado del menú (abierto/cerrado)
  isMobile = false; // Indica si la pantalla es móvil o tablet

  constructor(private breakpointObserver: BreakpointObserver) {}

  ngOnInit() {
    // Detecta el tamaño de la pantalla (móvil y tablet)
    this.breakpointObserver.observe([Breakpoints.XSmall, Breakpoints.Small])
      .subscribe(result => {
        console.log(result); // Muestra el resultado de la detección
        this.isMobile = result.matches; // Actualiza el estado de isMobile
        if (!this.isMobile) {
          this.menuOpen = true; // Siempre muestra el menú en escritorio
        } else {
          this.menuOpen = false; // Oculta el menú en móvil y tablet por defecto
        }
      });
  }

  // Función para alternar el menú
  toggleMenu() {
    console.log("toggle menu. Mobile: " + this.isMobile);
    if (this.isMobile) {
      this.menuOpen = !this.menuOpen;
    }
  }
}