import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'inicio',
    loadComponent: () => import('./app.component').then(m => m.AppComponent)
  },
  {
    path: 'servicios',
    loadComponent: () => import('./pages/contact/contact.component').then(m => m.ContactComponent)
  },
  {
    path: 'acerca-de',
    loadComponent: () => import('./pages/contact/contact.component').then(m => m.ContactComponent)
  },
  {
    path: 'contacto',
    loadComponent: () => import('./pages/contact/contact.component').then(m => m.ContactComponent)
  },
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: '**', redirectTo: 'inicio' }
];

