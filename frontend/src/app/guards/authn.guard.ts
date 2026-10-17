import AuthService from "@/shared/auth.service";
import { isPlatformBrowser } from "@angular/common";
import { inject, PLATFORM_ID } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { catchError, map, of } from "rxjs";

export const authnGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.checkIfUserLoggedIn().pipe(
        map((isLoggedIn) => {
            if (isLoggedIn) {
                return true;
            };
            return router.createUrlTree(["/login"]);
        }),
        catchError(() => {
            return of(router.createUrlTree(["/login"]));
        })
    );
};
