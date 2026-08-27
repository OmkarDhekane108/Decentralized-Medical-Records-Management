// Point this at your Spring Boot backend.
const API_ORIGIN = "http://localhost:8081";
const API_BASE = API_ORIGIN + "/api/auth";

function getToken() { return localStorage.getItem("mc_token"); }
function getRole() { return localStorage.getItem("mc_role"); }
function getUsername() { return localStorage.getItem("mc_username"); }
function getFullName() { return localStorage.getItem("mc_fullName"); }

async function handleJsonResponse(res) {
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error(data.message || "Request failed.");
  }
  return data;
}

// ---- Generic authenticated request (used by dashboards) ----
async function apiRequest(path, { method = "GET", body, auth = true } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth) {
    const token = getToken();
    if (token) headers["Authorization"] = token;
  }

  const res = await fetch(API_ORIGIN + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401) {
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

// ---- Mobile OTP ----
async function apiSendOtp(mobile) {
  const res = await fetch(`${API_BASE}/send-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ mobile }),
  });
  return handleJsonResponse(res);
}

async function apiVerifyOtp(mobile, otp) {
  const res = await fetch(`${API_BASE}/verify-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ mobile, otp }),
  });
  return handleJsonResponse(res);
}

// ---- Email OTP ----
async function apiSendEmailOtp(email) {
  const res = await fetch(`${API_BASE}/send-email-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email }),
  });
  return handleJsonResponse(res);
}

async function apiVerifyEmailOtp(email, otp) {
  const res = await fetch(`${API_BASE}/verify-email-otp`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, otp }),
  });
  return handleJsonResponse(res);
}

// ---- Register (multipart, includes profile picture) ----
async function apiRegisterWithPhoto(formData) {
  const res = await fetch(`${API_BASE}/register`, {
    method: "POST",
    body: formData,
  });
  return handleJsonResponse(res);
}

// ---- Login ----
async function apiLogin(username, password, role) {
  const res = await fetch(`${API_BASE}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, role }),
  });
  return handleJsonResponse(res);
}

// ---- Logout ----
async function apiLogout() {
  try { await apiRequest("/api/auth/logout", { method: "POST" }); } catch (e) {}
  localStorage.removeItem("mc_token");
  localStorage.removeItem("mc_role");
  localStorage.removeItem("mc_username");
  localStorage.removeItem("mc_fullName");
  window.location.href = "login.html";
}