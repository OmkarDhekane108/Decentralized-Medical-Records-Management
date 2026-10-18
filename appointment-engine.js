/* MedChain — shared appointment store (browser localStorage, same-browser demo).
   Replace the internals with fetch() calls to your Spring Boot API for real multi-device use. */
const APPT_STORE = "medchain_appointments_v1";
const ApptStore = {
  all() { try { return JSON.parse(localStorage.getItem(APPT_STORE) || "[]"); } catch (e) { return []; } },
  save(list) { localStorage.setItem(APPT_STORE, JSON.stringify(list)); },
  add(a) { const l = this.all(); l.push(a); this.save(l); },
  update(id, patch) { this.save(this.all().map(a => a.id === id ? { ...a, ...patch } : a)); },
  forPatient(u) { return this.all().filter(a => a.patientUsername === u); },
  migrate(u) { // move appointments saved by the older patient page
    const k = "mc_appts_" + u, old = localStorage.getItem(k);
    if (!old) return;
    try { JSON.parse(old).forEach(a => this.add({ ...a, patientUsername: u, patientName: u, status: a.status === "Booked" ? "Booked" : a.status })); } catch (e) {}
    localStorage.removeItem(k);
  }
};
function confirmationMessage(a, doctorName) {
  return `MedChain: Hello ${a.patientName}, your appointment at ${a.hospital} on ${a.date} at ${a.time} is CONFIRMED` +
    (doctorName ? ` by Dr. ${doctorName}` : "") + `. Please arrive 10 minutes early and carry your ID. Reason: ${a.reason}.`;
}
