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
  param = ''; // Input del usuario
  responseData: any = null; // Respuesta de la API
  loading = false; // Indicador de carga
  error = ''; // Manejo de errores

  constructor(private apiTryItService: ApiTryItService) {}

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
      },
      error: (err) => {
        this.error = 'Error al hacer la petición';
        this.loading = false;
        console.log(err)
      },
    });
  }

  // Copia al portapapeles la información
  copyToClipboard() {
    navigator.clipboard.writeText(JSON.stringify(this.responseData, null, 2));
  }


}
