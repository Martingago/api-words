import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebStatComponent } from '../../global/stats/web-stat/web-stat.component';
import { H2Component } from "../../global/titles/h2/h2.component";
import { ApiTryItService } from '../../../service/api-try-it.service';

interface Stats {
  atribute: string;
  count: number;
  animatedCount: number;
  animatedString: string;
}

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule, WebStatComponent, H2Component],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent implements OnInit {

  title: string = "";
  paragraph: string = "";
  stats: Stats[] = [];

  constructor(private apiService: ApiTryItService) {}

  ngOnInit() {
    // Cargar valores iniciales desde el JSON local en forma de placeholders
    this.apiService.getLocalData().subscribe(localData => {
      this.title = localData.stats_section.title;
      this.paragraph = localData.stats_section.paragraphs[0].content;
      this.stats = localData.stats_section.stats.map((stat: any) => ({
        atribute: stat.atribute,
        count: Number(stat.count), //valor final de la estadisticas
        animatedCount: Number(stat.count), //valores animados que cambian hasta llegar a count
        animatedString: Number(stat.count).toLocaleString() //Mismos valores animados pero convertidos a un string con formato de miles.
      }));

      
      // Luego, intentamos actualizar con datos en tiempo real de la API
      this.apiService.getStats().subscribe(data => {
        if (data.status) {
          this.stats[0].count = data.responseObject.wordsCount;
          this.stats[1].count = data.responseObject.wordsDefinitionsCount;
          this.stats[2].count = data.responseObject.wordsSynonymsCount;
          this.stats[3].count = data.responseObject.wordsExamplesCount;

          // Reiniciar animación con los nuevos valores
          this.animateNumbers();
        }
      });
    });
  }

  /**
   * Realiza una animacion en los números de las estadisticas desde un placeholder (ya establecido) hasta el valor real.
   */
  animateNumbers() {
    this.stats.forEach(stat => {
      const step = Math.ceil(stat.count / 8); // Cuántos números sumamos por iteración
      let currentValue = 0;

      const interval = setInterval(() => {
        currentValue += step;
        stat.animatedCount = Math.min(currentValue, stat.count); // Evitar que sobrepase el valor real
        stat.animatedString = stat.animatedCount.toLocaleString(); //Convertir el valor animado a string con formato de miles
        if (stat.animatedCount >= stat.count) {
          clearInterval(interval);
        }
      }, 30);
    });
  }

}
