import type { AuthResponse } from "../types/api";

const AUTH_KEY = "fintech.auth";

export type StoredAuth = Pick<AuthResponse, "userId" | "accessToken" | "refreshToken" | "refreshTokenExpiresAt" | "tokenType">;

export function getStoredAuth(): StoredAuth | null {
  const value = window.localStorage.getItem(AUTH_KEY);
  if (!value) {
    return null;
  }

  try {
    return JSON.parse(value) as StoredAuth;
  } catch {
    clearStoredAuth();
    return null;
  }
}

export function setStoredAuth(auth: StoredAuth) {
  window.localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function clearStoredAuth() {
  window.localStorage.removeItem(AUTH_KEY);
}
