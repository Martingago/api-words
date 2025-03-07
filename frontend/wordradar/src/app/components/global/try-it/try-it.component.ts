import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiTryItService } from '../../../service/api-try-it.service';

@Component({
  selector: 'app-try-it',
  imports: [FormsModule, CommonModule],
  templateUrl: './try-it.component.html',
  styleUrl: './try-it.component.css'
})
export class TryItComponent {
  endpoint = 'http://localhost:8080/api/v1/'; // Endpoint base
  param = 'search/emperador'; // Input del usuario
  responseData: any = null; // Respuesta de la API
  loading = false; // Indicador de carga
  error = ''; // Manejo de errores

  // Estado de los tooltips de cada botón
  copySuccess: { [key: string]: boolean } = {};

  constructor(private apiTryItService: ApiTryItService) {}

  // Cargar el placeholder desde un JSON local al iniciar el componente
  ngOnInit() {
    this.apiTryItService.getData('/assets/data/placeholder/try-it-placeholder.json').subscribe({
      next: (data) => {
        this.responseData = data;
      },
      error: (err) => {
        console.error('Error cargando el placeholder:', err);
      }
    });
  }


  /**
   * Realiza las peticiones al servidor
   */
  tryRequest() {
    this.loading = true;
    this.error = '';
    this.responseData = null;

    // Construir la URL con el parámetro
    const fullUrl = `${this.endpoint}${this.param}`;

    this.apiTryItService.getData(fullUrl).subscribe({
      next: (data) => {
        this.responseData = data;
        this.loading = false;
        console.log(data);
      },
      error: (err) => {
        this.error = err.error;
        this.responseData = this.error;
        this.loading = false;
        console.log(  err.error)
      },
    });
  }

  copyToClipboard(type: string) {
    let textToCopy = type === 'response' 
      ? JSON.stringify(this.responseData, null, 2) 
      : `${this.endpoint}${this.param}`;

    navigator.clipboard.writeText(textToCopy).then(() => {
      this.copySuccess[type] = true;
      setTimeout(() => this.copySuccess[type] = false, 1500);
    });
  }


}
