import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';


@Component({ selector: 'app-login', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './login.component.html', styleUrl: './login.component.css' })
export class LoginComponent {
	readonly form = this.formBuilder.nonNullable.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
	submitting = false;
	error = '';

	constructor(private readonly formBuilder: FormBuilder, private readonly authService: AuthService, private readonly router: Router) {}

	submit(): void {
		if (this.form.invalid) return;
		this.submitting = true; this.error = '';
		this.authService.login(this.form.getRawValue()).subscribe({ next: () => void this.router.navigate(['/home']), error: () => { this.error = 'Invalid email or password.'; this.submitting = false; } });
	}
}
