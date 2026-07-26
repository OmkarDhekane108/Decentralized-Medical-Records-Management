/* ==========================================================
   MedChain — Auth Guard (real backend session)
   Protects dashboard pages: redirects to login.html if no
   valid token is found, or if the session role doesn't match
   the page it's guarding.
   ========================================================== */

function requireRole(expectedRole) {
  const token = localStorage.getItem("mc_token");
  const role = localStorage.getItem("mc_role");

  if (!token || !role) {
    window.location.href = "login.html";
    return null;
  }
  if (role.toUpperCase() !== expectedRole.toUpperCase()) {
    window.location.href = "login.html";
    return null;
  }
  return {
    token,
    role,
    username: localStorage.getItem("mc_username"),
    fullName: localStorage.getItem("mc_fullName"),
  };
}

function logout() {
  apiLogout();
}
