import { HlmCardImports } from '@/components/ui/card/src';
import { HlmLabelImports } from '@/components/ui/label/src';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmFieldImports } from '@spartan-ng/helm/field';
import { HlmInputImports } from '@spartan-ng/helm/input';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import { LoginRequest } from '@/types/auth.model';
import AuthService from '@/shared/auth.service';
import { RouteMeta } from '@analogjs/router';
import { anonymousGuard } from '@/guards/anonymous.guard';

export const routeMeta: RouteMeta = {
  canActivate: [anonymousGuard]
}

@Component({
	selector: 'spartan-field-separator-preview',
	imports: [
    HlmFieldImports,
    HlmButtonImports,
    HlmInputImports,
    HlmCardImports,
    HlmLabelImports,
    ReactiveFormsModule,
    RouterLink
  ],
	host: {
		class: "w-full",
	},
	template: `
    <section class="section flex flex-col items-center justify-center">
      <hlm-card class="w-full max-w-sm">
        <hlm-card-header>
          <h3 hlmCardTitle>Login to your account</h3>
          <p hlmCardDescription>Enter your credentials below to login to your account</p>
        </hlm-card-header>

        <div hlmCardContent>
          <form id="login-form" [formGroup]="loginForm" (ngSubmit)="onSubmit()">
            <div class="flex flex-col gap-6">
              <div class="grid gap-2">
                <label hlmLabel for="identifier">Identifier</label>
                <input type="text" id="identifier" placeholder="m@example.com" required hlmInput formControlName="identifier" />
              </div>

              <div class="grid gap-2">
                <div class="flex items-center">
                  <label hlmLabel for="password">Password</label>
                  <a href="#" class="ml-auto inline-block text-sm underline-offset-4 hover:underline">
                    Forgot your password?
                  </a>
                </div>
                <input type="password" id="password" hlmInput formControlName="password"/>
              </div>
            </div>
          </form>
        </div>

        <hlm-card-footer class="flex flex-col gap-6">
          <button 
            hlmBtn
            [disabled]="loginForm.invalid || isSubmitting()"
            type="submit"
            class="w-full"
            form="login-form"
            >
            {{ isSubmitting() ? 'Signing in...' : 'Login' }}
          </button>
          <hlm-field-separator>Don't have an account? <a routerLink="/signup">Sign up</a></hlm-field-separator>
        </hlm-card-footer>
      </hlm-card>
    </section>
	`,
})
export default class LoginPage {
  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router)

  isSubmitting = signal(false);

  loginForm = this.formBuilder.nonNullable.group({
    identifier: ['', Validators.required],
    password: ['', Validators.required]
  })

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isSubmitting.set(true)
    const formValue: LoginRequest = this.loginForm.getRawValue()
    this.authService.loginUser(
      formValue
    ).subscribe({
      next: (user) => {
        this.isSubmitting.set(false)
        console.log('Logged in user:', user);
        this.router.navigate(['/'])
      },
      error: (err) => {
        this.isSubmitting.set(false)
        console.error("Failed to save vehicle: ", err)
      }
    })
  }
}