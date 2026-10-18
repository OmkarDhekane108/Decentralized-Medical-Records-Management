/* ==========================================================
   MedChain — Hospital directory engine (for referral feature)
   ========================================================== */

const HOSPITAL_KEY = "medchain_hospitals_v1";

function loadHospitalsRaw() {
  const raw = localStorage.getItem(HOSPITAL_KEY);
  return raw ? JSON.parse(raw) : null;
}

function saveHospitals(list) {
  localStorage.setItem(HOSPITAL_KEY, JSON.stringify(list));
}

// Builds a Google Maps link — uses lat/lng if present, otherwise searches by name+address
function mapsLinkFor(h) {
  if (h.lat && h.lng) {
    return `https://www.google.com/maps/search/?api=1&query=${h.lat},${h.lng}`;
  }
  const q = encodeURIComponent(`${h.name} ${h.address || ""}`);
  return `https://www.google.com/maps/search/?api=1&query=${q}`;
}

function seedHospitalsIfEmpty() {
  let list = loadHospitalsRaw();
  if (list && list.length) return list;

  list = [
    { id: "H001", name: "City Multispecialty Hospital", lat: 18.5204, lng: 73.8567, specialization: "Cardiology", slots: 5 },
    { id: "H002", name: "Sunrise Ortho Center", lat: 18.5300, lng: 73.8400, specialization: "Orthopedics", slots: 3 },
    { id: "H003", name: "Nova Neuro & Spine Institute", lat: 18.5108, lng: 73.8291, specialization: "Neurology", slots: 2 },
    {
      id: "H004",
      name: "Dr. Sachin Deshmukh ENT & Maternity Hospital",
      lat: 18.1780, lng: 76.0430,
      specialization: "ENT (Ear, Nose & Throat)",
      slots: 4,
      address: "Near Chh. Shivaji High School, Tambari Vibhag, Dharashiv (Osmanabad), Maharashtra 413501",
      phone: "094051 44681",
    },
  ];
  saveHospitals(list);
  return list;
}

function addHospital({ name, lat, lng, specialization, slots, address, phone }) {
  const list = loadHospitalsRaw() || [];
  const id = "H" + String(list.length + 1).padStart(3, "0");
  const hospital = {
    id, name, lat: parseFloat(lat), lng: parseFloat(lng),
    specialization, slots: parseInt(slots, 10),
    address: address || "", phone: phone || "",
  };
  list.push(hospital);
  saveHospitals(list);
  return hospital;
}

function removeHospital(id) {
  let list = loadHospitalsRaw() || [];
  list = list.filter(h => h.id !== id);
  saveHospitals(list);
  return list;
}

function getHospitalById(id) {
  const list = loadHospitalsRaw() || [];
  return list.find(h => h.id === id) || null;
}

// ---- Hospital login credentials (plaintext shown once, only a salted SHA-256 hash is stored) ----
function _rand(n) {
  const cs = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#$";
  const a = new Uint32Array(n); crypto.getRandomValues(a);
  return Array.from(a, x => cs[x % cs.length]).join("");
}
async function _sha(t) {
  const b = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(t));
  return Array.from(new Uint8Array(b), x => x.toString(16).padStart(2, "0")).join("");
}
async function generateHospitalCredentials(id) {
  const list = loadHospitalsRaw() || [];
  const h = list.find(x => x.id === id);
  if (!h) return null;
  const password = _rand(10), salt = _rand(8);
  h.loginId = h.loginId || ("hosp" + h.id.slice(1));
  h.pwSalt = salt; h.pwHash = await _sha(salt + password); h.credsAt = new Date().toISOString();
  saveHospitals(list);
  return { name: h.name, loginId: h.loginId, password };
}
async function verifyHospitalLogin(loginId, password) {
  const h = (loadHospitalsRaw() || []).find(x => x.loginId === loginId);
  return (h && h.pwHash && (await _sha(h.pwSalt + password)) === h.pwHash) ? h : null;
}
