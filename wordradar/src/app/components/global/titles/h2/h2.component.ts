import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-h2',
  imports: [CommonModule],
  templateUrl: './h2.component.html',
  styleUrl: './h2.component.css'
})
export class H2Component {
  @Input() classList : String ='';
}
