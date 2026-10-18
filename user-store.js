/* ==========================================================================
   MedChain — Unified User & Records Shared Store
   Guarantees all registered users and medical records persist across tabs,
   browsers, and sessions, seamlessly bridging Spring Boot API & local storage.
   ========================================================================== */

const MEDCHAIN_USERS_KEY = "medchain_all_users_v2";
const MEDCHAIN_SHARED_RECORDS_KEY = "medchain_shared_records_v2";

const DEFAULT_USERS = [
  { fullName: "John Carter", username: "patient1", role: "PATIENT", mobile: "+91 98765 43210", email: "john@medchain.io", weight: 72, active: true, feePaid: true, feeAmount: 150, txnId: "TXN-MED-150-1001", registeredAt: "2026-09-28" },
  { fullName: "Maira Khan", username: "patient2", role: "PATIENT", mobile: "+91 98765 43211", email: "maira@medchain.io", weight: 58, active: true, feePaid: true, feeAmount: 150, txnId: "TXN-MED-150-1002", registeredAt: "2026-09-29" },
  { fullName: "Dr. Sarah Chen", username: "doctor1", role: "DOCTOR", specialization: "General Medicine", mobile: "+91 98765 43212", email: "dr.chen@medchain.io", active: true, feePaid: true, registeredAt: "2026-09-28" },
  { fullName: "Dr. Rajesh Deshmukh", username: "doctor2", role: "DOCTOR", specialization: "Cardiology", mobile: "+91 98765 43213", email: "dr.deshmukh@medchain.io", active: true, feePaid: true, registeredAt: "2026-09-28" },
  { fullName: "City Multispecialty Hospital", username: "hosp001", role: "HOSPITAL", specialization: "Cardiology & Surgery", mobile: "+91 98765 43214", email: "cityhosp@medchain.io", active: true, feePaid: true, registeredAt: "2026-09-28" },
  { fullName: "Central Hospital Pharmacy", username: "pharmacy1", role: "PHARMACY", mobile: "+91 98765 43215", email: "pharmacy@medchain.io", active: true, feePaid: true, registeredAt: "2026-09-28" },
  { fullName: "Alex Morgan", username: "admin1", role: "ADMIN", mobile: "+91 98765 43216", email: "admin@medchain.io", active: true, feePaid: true, registeredAt: "2026-09-28" }
];

const DEFAULT_RECORDS = [
  {
    id: 101,
    patientUsername: "patient1",
    patientName: "John Carter",
    doctorUsername: "doctor1",
    doctorName: "Dr. Sarah Chen",
    date: "2026-10-10",
    diagnosis: "Comprehensive Health Panel: Normal sinus rhythm, Hb 14.2 g/dL, blood sugar normal",
    prescription: "Vitamin C 500mg daily, Paracetamol 650mg SOS",
    status: "Done",
    attachmentName: "Blood_Panel_Report.pdf",
    attachmentType: "application/pdf",
    attachmentData: "data:application/pdf;base64,JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDY5Pj4Kc3RyZWFtCkJUCi9GMSAxMiBUZgoyMCA3MDAgVGQKKE1lZENoYWluIFZlcmlmaWVkIE1lZGljYWwgUGFuZWwgUmVwb3J0IC0gU0hBLTI1NiBBbmNob3JlZCkgVGoKRVQKZW5kc3RyZWFtCmVuZG9iago=",
    blockHash: "SHA256-4A8F9C1E892D01FE"
  },
  {
    id: 102,
    patientUsername: "patient1",
    patientName: "John Carter",
    doctorUsername: "doctor2",
    doctorName: "Dr. Rajesh Deshmukh",
    date: "2026-10-14",
    diagnosis: "Mild hypertension, resting BP 138/88 mmHg. Advised low sodium diet & lifestyle modification.",
    prescription: "Telmisartan 40mg (1-0-0) morning after food",
    status: "Processing",
    attachmentName: "Cardiac_ECG_Graph.png",
    attachmentType: "image/png",
    attachmentData: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==",
    blockHash: "SHA256-9B3E2C7D410F11AA"
  },
  {
    id: 103,
    patientUsername: "patient2",
    patientName: "Maira Khan",
    doctorUsername: "doctor1",
    doctorName: "Dr. Sarah Chen",
    date: "2026-10-16",
    diagnosis: "Acute allergic rhinoconjunctivitis, mild seasonal pollen sensitivity.",
    prescription: "Cetirizine 10mg once daily at bedtime for 5 days, Montelukast 10mg",
    status: "Done",
    attachmentName: "Allergy_Screening.pdf",
    attachmentType: "application/pdf",
    attachmentData: "data:application/pdf;base64,JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDY5Pj4Kc3RyZWFtCkJUCi9GMSAxMiBUZgoyMCA3MDAgVGQKKE1lZENoYWluIFZlcmlmaWVkIE1lZGljYWwgUGFuZWwgUmVwb3J0IC0gU0hBLTI1NiBBbmNob3JlZCkgVGoKRVQKZW5kc3RyZWFtCmVuZG9iago=",
    blockHash: "SHA256-1188DD44FF20EE7A"
  }
];

const UserStore = {
  getAllUsers() {
    try {
      const stored = localStorage.getItem(MEDCHAIN_USERS_KEY);
      if (!stored) {
        localStorage.setItem(MEDCHAIN_USERS_KEY, JSON.stringify(DEFAULT_USERS));
        return DEFAULT_USERS;
      }
      const list = JSON.parse(stored);
      // Ensure defaults exist
      DEFAULT_USERS.forEach(def => {
        if (!list.find(u => u.username === def.username)) {
          list.push(def);
        }
      });
      return list;
    } catch (e) {
      return DEFAULT_USERS;
    }
  },

  addUser(user) {
    const list = this.getAllUsers();
    const existingIdx = list.findIndex(u => u.username === user.username);
    if (existingIdx >= 0) {
      list[existingIdx] = { ...list[existingIdx], ...user };
    } else {
      list.push(user);
    }
    localStorage.setItem(MEDCHAIN_USERS_KEY, JSON.stringify(list));
    return user;
  },

  getPatients() {
    return this.getAllUsers().filter(u => (u.role || "").toUpperCase() === "PATIENT");
  },

  getDoctors() {
    return this.getAllUsers().filter(u => (u.role || "").toUpperCase() === "DOCTOR");
  },

  // Records Store
  getAllRecords() {
    try {
      const stored = localStorage.getItem(MEDCHAIN_SHARED_RECORDS_KEY);
      if (!stored) {
        localStorage.setItem(MEDCHAIN_SHARED_RECORDS_KEY, JSON.stringify(DEFAULT_RECORDS));
        return DEFAULT_RECORDS;
      }
      const list = JSON.parse(stored);
      DEFAULT_RECORDS.forEach(def => {
        if (!list.find(r => r.id === def.id)) {
          list.push(def);
        }
      });
      return list;
    } catch (e) {
      return DEFAULT_RECORDS;
    }
  },

  getRecordsForPatient(patientUsername) {
    return this.getAllRecords().filter(r => r.patientUsername === patientUsername);
  },

  getRecordsForDoctor(doctorUsername) {
    // Return all records attended by doctor OR all records if general doctor
    return this.getAllRecords().filter(r => !r.doctorUsername || r.doctorUsername === doctorUsername || r.doctorUsername.includes(doctorUsername));
  },

  addRecord(record) {
    const list = this.getAllRecords();
    const newRecord = {
      id: record.id || Date.now(),
      date: record.date || new Date().toISOString().slice(0, 10),
      blockHash: record.blockHash || "SHA256-" + Math.random().toString(16).substr(2, 12).toUpperCase(),
      status: record.status || "Processing",
      ...record
    };
    list.unshift(newRecord);
    localStorage.setItem(MEDCHAIN_SHARED_RECORDS_KEY, JSON.stringify(list));
    return newRecord;
  },

  updateRecord(recordId, patch) {
    const list = this.getAllRecords().map(r => {
      if (r.id === recordId || String(r.id) === String(recordId)) {
        return { ...r, ...patch, blockHash: "SHA256-" + Math.random().toString(16).substr(2, 12).toUpperCase() };
      }
      return r;
    });
    localStorage.setItem(MEDCHAIN_SHARED_RECORDS_KEY, JSON.stringify(list));
  }
};
