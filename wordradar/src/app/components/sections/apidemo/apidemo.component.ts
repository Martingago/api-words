import { Component } from '@angular/core';
import { TryItComponent } from "../../global/try-it/try-it.component";
import { H2Component } from "../../global/titles/h2/h2.component";

@Component({
  selector: 'app-apidemo',
  imports: [TryItComponent, H2Component],
  templateUrl: './apidemo.component.html',
  styleUrl: './apidemo.component.css'
})
export class ApidemoComponent {

}
