import api from "./api";

const authService = {
  async csrf() {
    const { data } = await api.get("/csrf");
    return data;
  },
  async register(payload) {
    const { data } = await api.post("/auth/register", payload);
    return data;
  },
  async login(payload) {
    const { data } = await api.post("/auth/login", payload);
    return data;
  },
  async me() {
    const { data } = await api.get("/auth/me");
    return data;
  },
  async logout() {
    const { data } = await api.post("/auth/logout");
    return data;
  },
};

export default authService;
