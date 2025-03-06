import { Component } from '@angular/core';
import { WebStatComponent } from '../../global/stats/web-stat/web-stat.component';
import { H2Component } from "../../global/titles/h2/h2.component";

@Component({
  selector: 'app-stats',
  imports: [WebStatComponent, H2Component],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent {

}
