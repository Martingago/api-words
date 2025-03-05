import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from './components/global/menu/menu.component';
import { CallToActionComponent } from "./components/call-to-action/call-to-action.component";
import { StatsComponent } from './components/sections/stats/stats.component';
import { FaqComponent } from './components/sections/faq/faq.component';
import { UsagesComponent } from "./components/sections/usages/usages.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MenuComponent, CallToActionComponent, StatsComponent, FaqComponent, UsagesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'wordradar';
}
