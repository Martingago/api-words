import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiTryItService } from '../../../service/api-try-it.service';

@Component({
  selector: 'app-try-it',
  imports: [FormsModule, CommonModule],
  templateUrl: './try-it.component.html',
  styleUrl: './try-it.component.css',
})
export class TryItComponent {
  endpoint = 'http://localhost:8080/api/v1/'; // Endpoint base
  param = 'search/emperador'; // Input del usuario
  responseData: any = null; // Respuesta de la API
  loading = false; // Indicador de carga
  error = ''; // Manejo de errores
  requestTime: Number | null = null; // Tiempo de respuesta de la API

  // Estado de los tooltips de cada botón
  copySuccess: { [key: string]: boolean } = {};

  constructor(private apiTryItService: ApiTryItService) {}

  /**
   * Función que carga un placeholder al lanzar el componente
   */
  ngOnInit() {
    this.apiTryItService
      .getData('/assets/data/placeholder/try-it-placeholder.json')
      .subscribe({
        next: (data) => {
          this.responseData = data;
        },
        error: (err) => {
          console.error('Error cargando el placeholder:', err);
        },
      });
  }

  /**
   * Función para realizar peticiones de prueba a la API
   */
  tryRequest() {
    const startTime = performance.now();
    this.loading = true;
    this.error = '';
    this.responseData = null;

    // Construir la URL con el parámetro
    this.param = this.cleanPath(this.param);
    const fullUrl = `${this.endpoint}${this.param}`;

    this.apiTryItService.getData(fullUrl).subscribe({
      
      next: (data) => {
        this.responseData = data;
        this.loading = false;
        const endtime = performance.now();
        this.requestTime = Math.round(endtime - startTime); //calcula diferencia en ms
      },
      error: (err) => {
        this.error = err.error;
        this.responseData = this.error;
        const endtime = performance.now();
        this.requestTime = Math.round(endtime - startTime); //calcula diferencia en ms
        this.loading = false;
      },
      
    });
  }

  /**
   * Función para limpiar y formatear el path ingresado por el usuario
   * @param path - Ruta ingresada por el usuario
   * @returns Path limpio y formateado
   */
  cleanPath(path: string): string {
    return path
      .trim() // Elimina espacios al inicio y final
      .replace(/^\/+|\/+$/g, '') // Elimina "/" al inicio y final
      .replace(/\s+/g, ' '); // Reemplaza múltiples espacios intermedios por uno solo
  }

  /**
   * Función para copiar el texto al portapapeles
   * @param type Tipo de texto a copiar (response o URL)
   */
  copyToClipboard(type: string) {
    let textToCopy =
      type === 'response'
        ? JSON.stringify(this.responseData, null, 2)
        : `${this.endpoint}${this.param}`;

    navigator.clipboard.writeText(textToCopy).then(() => {
      this.copySuccess[type] = true;
      setTimeout(() => (this.copySuccess[type] = false), 1500);
    });
  }
}
