/* ==========================================================
   MedChain — Real backend API client
   Talks to the Spring Boot backend (default: http://localhost:8080)
   ========================================================== */

const API_BASE = "http://localhost:8081";

function getToken() { return localStorage.getItem("mc_token"); }
function getRole() { return localStorage.getItem("mc_role"); }
function getUsername() { return localStorage.getItem("mc_username"); }
function getFullName() { return localStorage.getItem("mc_fullName"); }

async function apiRequest(path, { method = "GET", body, auth = true } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth) {
    const token = getToken();
    if (token) headers["Authorization"] = token;
  }

  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401) {
    // Session expired or invalid — send back to login
    localStorage.removeItem("mc_token");
    localStorage.removeItem("mc_role");
    window.location.href = "login.html";
    throw new Error("Unauthorized");
  }

  if (!res.ok) {
    let msg = "Request failed";
    try { msg = (await res.json()).message || msg; } catch (e) {}
    throw new Error(msg);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

async function apiLogin(username, password, role) {
  return apiRequest("/api/auth/login", {
    method: "POST",
    auth: false,
    body: { username, password, role },
  });
}

async function apiLogout() {
  try { await apiRequest("/api/auth/logout", { method: "POST" }); } catch (e) {}
  localStorage.removeItem("mc_token");
  localStorage.removeItem("mc_role");
  localStorage.removeItem("mc_username");
  localStorage.removeItem("mc_fullName");
  window.location.href = "login.html";
}
