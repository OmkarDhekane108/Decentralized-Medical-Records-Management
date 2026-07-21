/* ==========================================================
   MedChain — lightweight client-side hash-chain engine
   Mirrors the Java Blockchain.java logic (SHA-256 block linking)
   so the dashboards can demonstrate real chain verification.
   ========================================================== */

const CHAIN_KEY = "medchain_records_v1";

async function sha256Hex(message) {
  const enc = new TextEncoder().encode(message);
  const buf = await crypto.subtle.digest("SHA-256", enc);
  return Array.from(new Uint8Array(buf)).map(b => b.toString(16).padStart(2, "0")).join("");
}

function shortHash(h) {
  if (!h) return "";
  return h.slice(0, 8) + "…" + h.slice(-6);
}

async function computeBlockHash(prevHash, timestamp, dataString) {
  return sha256Hex(prevHash + timestamp + dataString);
}

function loadChainRaw() {
  const raw = localStorage.getItem(CHAIN_KEY);
  return raw ? JSON.parse(raw) : null;
}

function saveChain(chain) {
  localStorage.setItem(CHAIN_KEY, JSON.stringify(chain));
}

// Seed demo data matching the Java Blockchain.java sample run
async function seedChainIfEmpty() {
  let chain = loadChainRaw();
  if (chain && chain.length) return chain;

  chain = [];
  const genesisData = "Genesis Block";
  const genesisHash = await computeBlockHash("0", 0, genesisData);
  chain.push({
    id: "blk-0", patientId: "system", patientName: "—", doctorName: "—",
    diagnosis: "Genesis Block", date: "—", timestamp: 0,
    prevHash: "0", hash: genesisHash, type: "genesis",
  });

  const samples = [
    { patientId: "ravi", patientName: "Ravi Kumar", doctorName: "Dr. Sharma", diagnosis: "Fever", date: "2026-07-10" },
    { patientId: "anita", patientName: "Anita Joshi", doctorName: "Dr. Patil", diagnosis: "Fracture", date: "2026-07-12" },
    { patientId: "ravi", patientName: "Ravi Kumar", doctorName: "Dr. Sharma", diagnosis: "Routine Checkup — Normal", date: "2026-07-13" },
  ];

  for (const s of samples) {
    const prev = chain[chain.length - 1];
    const ts = Date.now() - (samples.length - samples.indexOf(s)) * 100000;
    const dataStr = `${s.patientName}|${s.doctorName}|${s.diagnosis}|${s.date}`;
    const hash = await computeBlockHash(prev.hash, ts, dataStr);
    chain.push({
      id: "blk-" + chain.length, ...s, timestamp: ts,
      prevHash: prev.hash, hash, type: "record",
    });
  }

  saveChain(chain);
  return chain;
}

function blockDataString(b) {
  if (b.type === "genesis") return b.diagnosis;
  if (b.type === "referral") return `${b.patientName}|${b.doctorName}|REFERRAL|${b.hospitalName}|${b.reason}|${b.date}`;
  return `${b.patientName}|${b.doctorName}|${b.diagnosis}|${b.date}`;
}

async function addRecordToChain({ patientId, patientName, doctorName, diagnosis, date }) {
  const chain = loadChainRaw() || [];
  const prev = chain[chain.length - 1];
  const ts = Date.now();
  const block = {
    id: "blk-" + chain.length, patientId, patientName, doctorName, diagnosis, date,
    timestamp: ts, prevHash: prev.hash, type: "record",
  };
  block.hash = await computeBlockHash(prev.hash, ts, blockDataString(block));
  chain.push(block);
  saveChain(chain);
  return block;
}

async function addReferralToChain({ patientId, patientName, doctorName, hospitalId, hospitalName, reason, date }) {
  const chain = loadChainRaw() || [];
  const prev = chain[chain.length - 1];
  const ts = Date.now();
  const block = {
    id: "blk-" + chain.length, patientId, patientName, doctorName, hospitalId, hospitalName, reason, date,
    status: "Pending", timestamp: ts, prevHash: prev.hash, type: "referral",
  };
  block.hash = await computeBlockHash(prev.hash, ts, blockDataString(block));
  chain.push(block);
  saveChain(chain);
  return block;
}

// Recompute every hash & linkage — the same check as Blockchain.isChainValid()
async function verifyChain() {
  const chain = loadChainRaw() || [];
  for (let i = 0; i < chain.length; i++) {
    const b = chain[i];
    const recomputed = await computeBlockHash(b.prevHash, b.timestamp, blockDataString(b));
    if (recomputed !== b.hash) return { valid: false, brokenAt: i, reason: "hash-mismatch" };
    if (i > 0 && b.prevHash !== chain[i - 1].hash) return { valid: false, brokenAt: i, reason: "broken-link" };
  }
  return { valid: true, brokenAt: null };
}

// DEV-ONLY demo helper: tampers with a block's content to show detection working
function tamperRecord(index) {
  const chain = loadChainRaw() || [];
  const b = chain[index];
  if (!b) return;
  if (b.type === "referral") {
    b.reason = (b.reason || "") + " (edited)";
  } else {
    b.diagnosis = (b.diagnosis || "") + " (edited)";
  }
  saveChain(chain);
}

function resetChain() {
  localStorage.removeItem(CHAIN_KEY);
}
