@'
// Point this at your Spring Boot backend.
const API_BASE = "http://localhost:8081/api/auth";

async function handleJsonResponse(res) {
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error(data.message || "Request failed.");
  }
  return data;
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
    body: formData, // browser sets multipart Content-Type automatically
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
'@ | Set-Content -Path api-client.js -Encoding UTF8
Get-Content api-client.js | Select-String "API_BASE"