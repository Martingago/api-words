import { Component } from '@angular/core';
import { WebStatComponent } from '../../global/stats/web-stat/web-stat.component';

@Component({
  selector: 'app-stats',
  imports: [WebStatComponent],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent {

}
