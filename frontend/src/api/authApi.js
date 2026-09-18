import http from "./http";

export const authApi = {
  login: (username, password) =>
    http.post("/auth/login", { username, password }).then((r) => r.data)
};

export default authApi;
