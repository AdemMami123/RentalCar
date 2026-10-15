import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LanguageCode, LanguageService } from '../../core/services/language.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  readonly user$ = this.authService.user$;
  menuOpen = false;
  scrolled = false;
  readonly languages: LanguageCode[] = ['en', 'fr', 'ar'];

  constructor(
    private readonly authService: AuthService,
    readonly languageService: LanguageService
  ) {}

  @HostListener('window:scroll') updateScrollState(): void {
    this.scrolled = window.scrollY > 24;
  }

  @HostListener('window:resize') closeMenuOnDesktop(): void {
    if (window.innerWidth > 760) this.menuOpen = false;
  }

  toggleMenu(): void { this.menuOpen = !this.menuOpen; }
  closeMenu(): void { this.menuOpen = false; }
  logout(): void { this.closeMenu(); this.authService.logout(); }
  changeLanguage(language: string): void {
    this.languageService.setLanguage(language as LanguageCode);
  }
}