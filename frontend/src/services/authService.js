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
  async profile() { const { data } = await api.get("/account/profile"); return data; },
  async updateProfile(payload) { const { data } = await api.put("/account/profile", payload); return data; },
  async changePassword(payload) { const { data } = await api.post("/account/change-password", payload); return data; },
  async forgotPassword(payload) { const { data } = await api.post("/account/forgot-password", payload); return data; },
  async resetPassword(payload) { const { data } = await api.post("/account/reset-password", payload); return data; },
  async logout() {
    const { data } = await api.post("/auth/logout");
    return data;
  },
};

export default authService;
