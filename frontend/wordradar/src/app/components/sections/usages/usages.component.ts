import { Component, OnInit } from '@angular/core';
import { UsageComponent } from "../../global/usage/usage.component";
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

interface Usage {
  image: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-usages',
  imports: [UsageComponent,CommonModule],
  templateUrl: './usages.component.html',
  styleUrl: './usages.component.css'
})
export class UsagesComponent implements OnInit{
  usages: Usage[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<{ usages: Usage[] }>('/assets/data/es.json').subscribe(data => {
      this.usages = data.usages;
    });
  }
}
