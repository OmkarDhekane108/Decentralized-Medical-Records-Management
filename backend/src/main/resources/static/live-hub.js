/* ==========================================================================
   MedChain — Central Live Data Hub & Real-Time Event State Machine
   Interconnects all 5 Dashboards: Patient, Doctor, Hospital Admin, Pharmacy, System Admin.
   Uses BroadcastChannel API + LocalStorage State Machine for instant cross-tab sync.
   ========================================================================== */

const MEDCHAIN_CHANNEL_NAME = "medchain_live_hub_v1";
const HUB_STORAGE_KEY = "medchain_state_events_v1";
const PHARMACY_PRESCRIPTIONS_KEY = "medchain_pharmacy_rx_v1";
const HOSPITAL_BEDS_KEY = "medchain_hospital_beds_v1";
const PATIENT_CONSENT_KEY = "medchain_patient_consent_v1";

class MedChainLiveHub {
  constructor() {
    this.listeners = new Map();
    this.broadcastChannel = null;

    if (typeof window !== "undefined" && "BroadcastChannel" in window) {
      try {
        this.broadcastChannel = new BroadcastChannel(MEDCHAIN_CHANNEL_NAME);
        this.broadcastChannel.onmessage = (event) => {
          this._handleIncomingEvent(event.data);
        };
      } catch (e) {
        console.warn("BroadcastChannel not available, falling back to storage listener", e);
      }
    }

    if (typeof window !== "undefined") {
      window.addEventListener("storage", (e) => {
        if (e.key === HUB_STORAGE_KEY && e.newValue) {
          try {
            const parsed = JSON.parse(e.newValue);
            this._handleIncomingEvent(parsed);
          } catch (err) {}
        }
      });
    }
  }

  // Publish event to all connected dashboards
  publish(eventType, payload) {
    const eventObj = {
      event: eventType,
      timestamp: new Date().toISOString(),
      id: "EVT-" + Date.now() + "-" + Math.random().toString(36).substr(2, 5),
      ...payload,
    };

    // Broadcast across tabs
    if (this.broadcastChannel) {
      this.broadcastChannel.postMessage(eventObj);
    }

    // Persist to trigger storage event
    try {
      localStorage.setItem(HUB_STORAGE_KEY, JSON.stringify(eventObj));
    } catch (e) {}

    // Also trigger locally in current tab
    this._handleIncomingEvent(eventObj);
    return eventObj;
  }

  // Subscribe to specific or wildcard events
  subscribe(eventType, callback) {
    if (!this.listeners.has(eventType)) {
      this.listeners.set(eventType, []);
    }
    this.listeners.get(eventType).push(callback);
    return () => this.unsubscribe(eventType, callback);
  }

  unsubscribe(eventType, callback) {
    if (!this.listeners.has(eventType)) return;
    const list = this.listeners.get(eventType).filter((cb) => cb !== callback);
    this.listeners.set(eventType, list);
  }

  _handleIncomingEvent(eventObj) {
    if (!eventObj || !eventObj.event) return;
    
    // Exact match listeners
    if (this.listeners.has(eventObj.event)) {
      this.listeners.get(eventObj.event).forEach((cb) => {
        try { cb(eventObj); } catch (e) { console.error("Event callback error", e); }
      });
    }

    // Wildcard listeners
    if (this.listeners.has("*")) {
      this.listeners.get("*").forEach((cb) => {
        try { cb(eventObj); } catch (e) { console.error("Wildcard callback error", e); }
      });
    }
  }

  // Pharmacy Queue helper
  static getPharmacyPrescriptions() {
    try {
      return JSON.parse(localStorage.getItem(PHARMACY_PRESCRIPTIONS_KEY) || "[]");
    } catch (e) { return []; }
  }

  static savePharmacyPrescriptions(list) {
    localStorage.setItem(PHARMACY_PRESCRIPTIONS_KEY, JSON.stringify(list));
  }

  static addPharmacyPrescription(rx) {
    const list = MedChainLiveHub.getPharmacyPrescriptions();
    list.unshift(rx);
    MedChainLiveHub.savePharmacyPrescriptions(list);
  }

  static updatePharmacyPrescription(id, patch) {
    const list = MedChainLiveHub.getPharmacyPrescriptions().map((item) => {
      if (item.id === id) {
        return { ...item, ...patch };
      }
      return item;
    });
    MedChainLiveHub.savePharmacyPrescriptions(list);
  }

  // Hospital Bed Management Helper
  static getHospitalBeds(hospitalId) {
    try {
      const all = JSON.parse(localStorage.getItem(HOSPITAL_BEDS_KEY) || "{}");
      if (!all[hospitalId]) {
        // Seed initial 12 beds
        all[hospitalId] = [
          { id: "B101", ward: "Emergency ICU", occupied: true, patientName: "Rahul Sharma", token: "TK-101", admitTime: "10:30 AM" },
          { id: "B102", ward: "Emergency ICU", occupied: false, patientName: null, token: null, admitTime: null },
          { id: "B103", ward: "Emergency ICU", occupied: true, patientName: "Ayesha Patel", token: "TK-102", admitTime: "11:15 AM" },
          { id: "B104", ward: "General Ward", occupied: false, patientName: null, token: null, admitTime: null },
          { id: "B105", ward: "General Ward", occupied: false, patientName: null, token: null, admitTime: null },
          { id: "B106", ward: "General Ward", occupied: true, patientName: "Maira Khan", token: "TK-103", admitTime: "01:00 PM" },
          { id: "B107", ward: "Special Cardiac", occupied: false, patientName: null, token: null, admitTime: null },
          { id: "B108", ward: "Special Cardiac", occupied: false, patientName: null, token: null, admitTime: null },
        ];
        localStorage.setItem(HOSPITAL_BEDS_KEY, JSON.stringify(all));
      }
      return all[hospitalId];
    } catch (e) { return []; }
  }

  static setHospitalBeds(hospitalId, beds) {
    const all = JSON.parse(localStorage.getItem(HOSPITAL_BEDS_KEY) || "{}");
    all[hospitalId] = beds;
    localStorage.setItem(HOSPITAL_BEDS_KEY, JSON.stringify(all));
  }

  // Patient Consent helper
  static getConsent(username) {
    const all = JSON.parse(localStorage.getItem(PATIENT_CONSENT_KEY) || "{}");
    return all[username] !== false; // Default true
  }

  static setConsent(username, allowed) {
    const all = JSON.parse(localStorage.getItem(PATIENT_CONSENT_KEY) || "{}");
    all[username] = allowed;
    localStorage.setItem(PATIENT_CONSENT_KEY, JSON.stringify(all));
  }
}

// Global singleton instance
const LiveHub = new MedChainLiveHub();
