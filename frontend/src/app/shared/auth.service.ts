import { LoginRequest, UserSessionResponse } from "@/types/auth.model";
import { UserInformationResponse } from "@/types/user.auth";
import { RegisterRequest } from "@/types/auth.model";
import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { catchError, map, Observable, of, switchMap, tap } from "rxjs";

@Injectable({
    providedIn: "root",
})
export default class AuthService {
    private http = inject(HttpClient);
    private baseUrl = "https://localhost:8443/api/v1";

    readonly isAuthenticated = signal<boolean | null>(null);
    public readonly currentUser = signal<UserInformationResponse | null>(null);

    checkIfUserLoggedIn(): Observable<boolean> {
        if (this.isAuthenticated() !== null) {
            return of(this.isAuthenticated() as boolean);
        }
        return this.http.get<UserSessionResponse>(
            `${this.baseUrl}/auth/me`).pipe(
            tap((response) => {
                this.isAuthenticated.set(response.isAuthenticated ?? false);
                this.currentUser.set(response.isAuthenticated === false ? null : response.user ?? null);
            }),
            map((response) => {
                return response.isAuthenticated ?? false;
            }),
            catchError(() => {
                this.isAuthenticated.set(false);
                this.currentUser.set(null);
                return of(false);
            }),
        );
    }

    loginUser(userData: LoginRequest): Observable<UserInformationResponse | null> {
        return this.http.post<void>(
            `${this.baseUrl}/auth/login`,
            userData
        ).pipe(
            switchMap(() => this.http.get<UserSessionResponse>(`${this.baseUrl}/auth/me`)),
            map((res) => {
                const isAuth = res.isAuthenticated ?? false
                const user = isAuth ? (res.user ?? null) : null

                this.isAuthenticated.set(isAuth)
                this.currentUser.set(user)

                return user
            })
        )
    }

    refreshToken(): Observable<void> {
        return this.http.post<void>(
            `${this.baseUrl}/auth/refresh`, {},
            { withCredentials: true }
        );
    }
    
    logOut(): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/auth/logout`, {}).pipe(
            tap(() => {
                this.setLogOut();
            }),
        );
    }
    
    setLogOut() {
        this.isAuthenticated.set(false);
        this.currentUser.set(null);
    }

    signUpUser(userData: RegisterRequest): Observable<void> {
        return this.http.post<void>(
            `${this.baseUrl}/auth/register`,
            userData,
        );
    }

    logOutAll(): Observable<void> {
        return this.http.post<void>(
            `${this.baseUrl}/auth/logout-all`, {}
        ).pipe(
            tap(() => {
                this.setLogOut();
            }),
        );
    }

    getAllSessions(): Observable<UserSessionResponse[]> {
        return this.http.get<UserSessionResponse[]>(
            `${this.baseUrl}/auth/active-sessions`
        )
    }
}
