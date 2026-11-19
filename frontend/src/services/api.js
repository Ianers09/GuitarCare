import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
  timeout: 15000,
});

export function getApiError(error) {
  if (error.response?.data?.message) return error.response.data.message;
  if (error.code === "ECONNABORTED") return "The server took too long to respond. Please try again.";
  if (!error.response) return "Cannot reach GuitarCare server. Make sure the backend is running.";
  return "Something went wrong. Please try again.";
}

export default api;
