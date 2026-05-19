import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";
import { clearStoredAuth, getStoredAuth, setStoredAuth } from "../auth/authStorage";
import type { AuthResponse, ErrorResponse } from "../types/api";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const httpClient = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json"
  }
});

let onUnauthorized: (() => void) | null = null;
let refreshPromise: Promise<AuthResponse> | null = null;

type RetriableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

export function setUnauthorizedHandler(handler: (() => void) | null) {
  onUnauthorized = handler;
}

httpClient.interceptors.request.use((config) => {
  const auth = getStoredAuth();
  if (auth?.accessToken) {
    config.headers.Authorization = `${auth.tokenType ?? "Bearer"} ${auth.accessToken}`;
  }
  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ErrorResponse>) => {
    const originalRequest = error.config as RetriableRequestConfig | undefined;

    if (!originalRequest || error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    const auth = getStoredAuth();
    if (!auth?.refreshToken || originalRequest.url?.includes("/api/v1/auth/refresh-token")) {
      clearStoredAuth();
      onUnauthorized?.();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      refreshPromise =
        refreshPromise ??
        axios
          .post<AuthResponse>(`${baseURL}/api/v1/auth/refresh-token`, {
            refreshToken: auth.refreshToken
          })
          .then((response) => response.data)
          .finally(() => {
            refreshPromise = null;
          });

      const refreshed = await refreshPromise;
      setStoredAuth(refreshed);
      originalRequest.headers.Authorization = `${refreshed.tokenType ?? "Bearer"} ${refreshed.accessToken}`;
      return httpClient(originalRequest);
    } catch (refreshError) {
      clearStoredAuth();
      onUnauthorized?.();
      return Promise.reject(refreshError);
    }
  }
);
