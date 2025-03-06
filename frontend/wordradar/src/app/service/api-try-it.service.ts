import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiTryItService {

  constructor(private http: HttpClient) {}

    // Método genérico para hacer peticiones GET a la API
    getData(endpoint: string): Observable<any> {
      return this.http.get(endpoint);
    }
}
