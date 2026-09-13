import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';


@Component({ selector: 'app-register', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './register.component.html', styleUrl: './register.component.css' })
export class RegisterComponent {
	readonly form = this.formBuilder.nonNullable.group({ firstName: ['', Validators.required], lastName: ['', Validators.required], email: ['', [Validators.required, Validators.email]], password: ['', [Validators.required, Validators.minLength(8)]] });
	submitting = false; error = '';
	constructor(private readonly formBuilder: FormBuilder, private readonly authService: AuthService, private readonly router: Router) {}
	submit(): void { if (this.form.invalid) return; this.submitting = true; this.error = ''; this.authService.register(this.form.getRawValue()).subscribe({ next: () => void this.router.navigate(['/login']), error: error => { this.error = error.status === 409 ? 'An account with this email already exists.' : 'Registration failed. Please check your details.'; this.submitting = false; } }); }
}
