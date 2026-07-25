// Drop this in place of the hardcoded `const USERS = {...}` block in login.html.
// Set API_BASE to your deployed backend URL (e.g. https://medvault-api.onrender.com).
const API_BASE = "http://localhost:8080";

document.getElementById("loginForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value;
  const role = document.getElementById("role").value; // matches the "Login As" dropdown

  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password, role }),
    });

    const data = await res.json();

    if (!res.ok) {
      document.getElementById("errorMsg").textContent = data.message || "Login failed.";
      return;
    }

    // Store the session token + role so dashboard.html can call protected endpoints
    // and show the right view. sessionStorage clears when the tab closes.
    sessionStorage.setItem("medvault_token", data.token);
    sessionStorage.setItem("medvault_role", data.role);
    sessionStorage.setItem("medvault_name", data.fullName);

    window.location.href = "dashboard.html";
  } catch (err) {
    document.getElementById("errorMsg").textContent = "Could not reach the server. Is the backend running?";
  }
});

// --- Example: dashboard.html fetching the logged-in patient's records ---
//
// const token = sessionStorage.getItem("medvault_token");
// const res = await fetch(`${API_BASE}/api/patient/records`, {
//   headers: { Authorization: `Bearer ${token}` }
// });
// const records = await res.json();
