import { HlmCardImports } from '@/components/ui/card/src';
import { HlmLabelImports } from '@/components/ui/label/src';
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmFieldImports } from '@spartan-ng/helm/field';
import { HlmInputImports } from '@spartan-ng/helm/input';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import { RegisterRequest } from '@/types/auth.model';
import AuthService from '@/shared/auth.service';
import { anonymousGuard } from '@/guards/anonymous.guard';
import { RouteMeta } from '@analogjs/router';

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
                <h3 hlmCardTitle>Create your account</h3>
                <p hlmCardDescription>Enter your credentials below to signup your account</p>
            </hlm-card-header>

            <div hlmCardContent>
                <form id="signup-form" [formGroup]="signupForm" (ngSubmit)="onSubmit()">
                    <div class="flex flex-col gap-6">
                        <div class="grid gap-2">
                            <label hlmLabel for="identifier">Username</label>
                            <input type="text" id="identifier" placeholder="m" required hlmInput formControlName="username" />
                        </div>

                        <div class="grid gap-2">
                            <label hlmLabel for="email">Email</label>
                            <input type="email" id="email" placeholder="m@example.com" required hlmInput formControlName="email" />
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

            <hlm-card-footer class="flex-col gap-2">
                <button 
                hlmBtn
                [disabled]="signupForm.invalid || isSubmitting()"  [disabled]="isSubmitting()"
                type="submit"
                class="w-full"
                form="signup-form"
                >
                {{ isSubmitting() ? 'Signing you up...' : 'Sign Up' }}
                </button>
              <hlm-field-separator>Already signed up?<a routerLink="/login">Login</a></hlm-field-separator>
            </hlm-card-footer>
        </hlm-card>
    </section>	
	`,
})
export default class SignUpPage {
  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router)

  isSubmitting = signal(false);

  signupForm = this.formBuilder.nonNullable.group({
    username: ['', Validators.required],
    email: ['', Validators.required],
    password: ['', Validators.required]
  })

  onSubmit() {
    if (this.signupForm.invalid) return;

    this.isSubmitting.set(true)
    const formValue: RegisterRequest = this.signupForm.getRawValue()
    this.authService.signUpUser(
      formValue
    ).subscribe({
      next: () => {
        this.isSubmitting.set(false)
        this.router.navigate(['/login'])
      },
      error: (err) => {
        this.isSubmitting.set(false)
        console.error("Failed to save vehicle: ", err)
      }
    })
  }
}