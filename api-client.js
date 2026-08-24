// Point this at your Spring Boot backend.
const API_BASE = "http://localhost:8080/api/auth";

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
// Backend returns { message, username, password, role } - the plaintext
// password is only ever shown here once (also emailed/texted), so show it
// to the user or route them straight to login.html with it prefilled.
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
