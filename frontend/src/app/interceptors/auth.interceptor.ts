import AuthService from "@/shared/auth.service";
import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { BehaviorSubject, catchError, filter, switchMap, take, throwError, timer } from "rxjs";

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<boolean>(false);

export const authInterceptor: HttpInterceptorFn = (
    req: HttpRequest<unknown>, next: HttpHandlerFn
) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const authReq = req.clone({
        withCredentials: true
    })

    if (
        req.url.includes('/auth/refresh') || 
        req.url.includes('/auth/login') ||
        req.url.includes('/auth/register')
    ) {
        return next(authReq)
    }

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401){
                if (!isRefreshing){
                    isRefreshing = true;
                    refreshTokenSubject.next(false);

                    return authService.refreshToken().pipe(
                        switchMap(() => timer(0)),
                        switchMap(() => {
                            isRefreshing = false;
                            refreshTokenSubject.next(true)
                            return next(authReq)
                        }),
                        catchError((refreshErr) => {
                            isRefreshing = false;
                            refreshTokenSubject.next(false);

                            router.navigate(['/login']);
                            return throwError(() => refreshErr)
                        })
                    )
                }
                else {
                    return refreshTokenSubject.pipe(
                        filter((isDone) => isDone === true),
                        take(1),
                        switchMap(() => next(authReq))
                    )
                }
            }

            return throwError(() => error)
        })
    );
};
