import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home-page/home-page.component').then(
        (m) => m.HomePageComponent
      ),
    title: 'Inicio',
  },
  {
    path: 'servicios',
    loadComponent: () =>
      import('./pages/contact/contact.component').then(
        (m) => m.ContactComponent
      ),
    title: 'Servicios',
  },
  {
    path: 'acerca-de',
    loadComponent: () =>
      import('./pages/contact/contact.component').then(
        (m) => m.ContactComponent
      ),
    title: 'Acerca de',
  },
  {
    path: 'contacto',
    loadComponent: () =>
      import('./pages/contact/contact.component').then(
        (m) => m.ContactComponent
      ),
    title: 'Contacto',
  },
  { path: '**', redirectTo: 'inicio' },
];
