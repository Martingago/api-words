import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiTryItService {

  constructor(private http: HttpClient) {}

    private readonly API_BASE_URL = 'http://localhost:8080/api/v1/';

    // Método genérico para hacer peticiones GET a la API
    getData(endpoint: string): Observable<any> {
      return this.http.get(endpoint);
    }

    /**
     * Obtiene las estadísticas de la API
     * @returns 
     */
    getStats(): Observable<any>{
      return this.http.get(this.API_BASE_URL + 'stats');
    }

    getLocalData():Observable<any>{
      return this.http.get("/assets/data/es.json");
    }
}
