/* ==========================================================
   MedChain — Hospital directory engine (for referral feature)
   Mirrors the Java Hospital.java model, stored in localStorage
   so Admin/Doctor dashboards can share the same hospital list.
   ========================================================== */

const HOSPITAL_KEY = "medchain_hospitals_v1";

function loadHospitalsRaw() {
  const raw = localStorage.getItem(HOSPITAL_KEY);
  return raw ? JSON.parse(raw) : null;
}

function saveHospitals(list) {
  localStorage.setItem(HOSPITAL_KEY, JSON.stringify(list));
}

function seedHospitalsIfEmpty() {
  let list = loadHospitalsRaw();
  if (list && list.length) return list;

  list = [
    { id: "H001", name: "City Multispecialty Hospital", lat: 18.5204, lng: 73.8567, specialization: "Cardiology", slots: 5 },
    { id: "H002", name: "Sunrise Ortho Center", lat: 18.5300, lng: 73.8400, specialization: "Orthopedics", slots: 3 },
    { id: "H003", name: "Nova Neuro & Spine Institute", lat: 18.5108, lng: 73.8291, specialization: "Neurology", slots: 2 },
  ];
  saveHospitals(list);
  return list;
}

function addHospital({ name, lat, lng, specialization, slots }) {
  const list = loadHospitalsRaw() || [];
  const id = "H" + String(list.length + 1).padStart(3, "0");
  const hospital = { id, name, lat: parseFloat(lat), lng: parseFloat(lng), specialization, slots: parseInt(slots, 10) };
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
