import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  readonly user$ = this.authService.user$;
  menuOpen = false;

  constructor(private readonly authService: AuthService) {}

  @HostListener('window:resize') closeMenuOnDesktop(): void {
    if (window.innerWidth > 760) this.menuOpen = false;
  }

  toggleMenu(): void { this.menuOpen = !this.menuOpen; }
  closeMenu(): void { this.menuOpen = false; }
  logout(): void { this.closeMenu(); this.authService.logout(); }
}