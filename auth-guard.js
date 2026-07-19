/* ==========================================================
   MedChain — Auth Guard
   Protects dashboard pages: redirects to login.html if no
   valid session is found in localStorage, and enforces that
   the session's role matches the page it's guarding.
   ========================================================== */

function requireRole(expectedRole) {
  const user = localStorage.getItem("loggedInUser");
  const role = localStorage.getItem("loggedInRole");

  if (!user || !role) {
    window.location.href = "login.html";
    return null;
  }
  if (role !== expectedRole) {
    // Logged in, but as the wrong role for this page
    window.location.href = "login.html";
    return null;
  }
  return { user, role };
}

function logout() {
  localStorage.removeItem("loggedInUser");
  localStorage.removeItem("loggedInRole");
  window.location.href = "login.html";
}
