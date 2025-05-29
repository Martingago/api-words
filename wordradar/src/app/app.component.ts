import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuComponent } from './components/global/menu/menu.component';
import { FooterComponent } from "./components/sections/footer/footer.component";


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MenuComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'wordradar';
}
