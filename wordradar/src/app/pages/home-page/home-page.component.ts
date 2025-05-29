import { Component } from '@angular/core';
import { CallToActionComponent } from "../../components/call-to-action/call-to-action.component";
import { StatsComponent } from '../../components/sections/stats/stats.component';
import { FaqComponent } from '../../components/sections/faq/faq.component';
import { UsagesComponent } from "../../components/sections/usages/usages.component";
import { ApidemoComponent } from "../../components/sections/apidemo/apidemo.component";

@Component({
  selector: 'app-home-page',
  imports: [CallToActionComponent, StatsComponent, FaqComponent, UsagesComponent, ApidemoComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {

}
