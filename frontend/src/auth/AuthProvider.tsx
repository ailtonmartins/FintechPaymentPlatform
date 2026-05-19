import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../api/services";
import { setUnauthorizedHandler } from "../api/httpClient";
import { clearStoredAuth, getStoredAuth, setStoredAuth, StoredAuth } from "./authStorage";

type AuthContextValue = {
  auth: StoredAuth | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (body: { name: string; email: string; password: string }) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<StoredAuth | null>(() => getStoredAuth());
  const navigate = useNavigate();

  const logout = () => {
    clearStoredAuth();
    setAuth(null);
    navigate("/login", { replace: true });
  };

  useEffect(() => {
    setUnauthorizedHandler(() => logout);
    return () => setUnauthorizedHandler(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      auth,
      isAuthenticated: Boolean(auth?.accessToken),
      login: async (email, password) => {
        const result = await authApi.login({ email, password });
        setStoredAuth(result);
        setAuth(result);
      },
      register: async (body) => {
        await authApi.register(body);
      },
      logout
    }),
    [auth]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de AuthProvider");
  }
  return context;
}
