import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-usage',
  imports: [],
  templateUrl: './usage.component.html',
  styleUrl: './usage.component.css'
})
export class UsageComponent {
  @Input() iconPath : String = '';
  @Input() title: string = 'Title';
  @Input() description: string = 'Lorem ipsum dolor sit amet consectetur, adipisicing elit. Rerum placeat voluptatibus';
}
