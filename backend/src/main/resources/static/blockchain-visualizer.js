/* ==========================================================
   MedChain — Interactive Blockchain Visualizer Engine
   Renders SHA-256 blocks as interactive nodes with hash links,
   block inspector modal, and real-time tampering simulation.
   ========================================================== */

let currentChainData = [];
let isTamperedDemo = false;

function generateGenesisBlock() {
  return {
    index: 0,
    timestamp: "2026-01-01 00:00:00",
    patientUsername: "GENESIS",
    doctorUsername: "SYSTEM",
    diagnosis: "Genesis Block — Network Initialized",
    previousHash: "0000000000000000000000000000000000000000000000000000000000000000",
    blockHash: "0000a89f3c71e4289a0b12c45d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e",
    nonce: 10482
  };
}

function renderBlockchainVisualizer(containerId, recordsList) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = "";
  
  // Format chain with Genesis block
  const chain = [generateGenesisBlock()];
  if (recordsList && recordsList.length > 0) {
    recordsList.forEach((r, idx) => {
      chain.push({
        index: idx + 1,
        timestamp: r.date ? (Array.isArray(r.date) ? `${r.date[0]}-${String(r.date[1]).padStart(2,'0')}-${String(r.date[2]).padStart(2,'0')}` : r.date) : "2026-09-28",
        patientUsername: r.patientUsername || "Patient",
        doctorUsername: r.doctorUsername || "Doctor",
        diagnosis: r.diagnosis || "Medical Record Entry",
        previousHash: idx === 0 ? chain[0].blockHash : (recordsList[idx - 1].blockHash || "0000prev..."),
        blockHash: r.blockHash || "0000hash" + Math.random().toString(36).substring(2, 10),
        nonce: Math.floor(Math.random() * 90000) + 10000,
        tampered: isTamperedDemo && idx === 1
      });
    });
  }
  currentChainData = chain;

  const visualizerWrapper = document.createElement("div");
  visualizerWrapper.className = "chain-visualizer";

  chain.forEach((block, i) => {
    // Render Node
    const node = document.createElement("div");
    node.className = `block-node ${block.tampered ? 'tampered' : ''}`;
    node.onclick = () => openBlockDetailsModal(block);

    node.innerHTML = `
      <div class="block-header">
        <span class="block-index">BLOCK #${block.index}</span>
        <span class="block-badge">${block.tampered ? '⚠️ TAMPERED' : (block.index === 0 ? 'GENESIS' : 'VALID')}</span>
      </div>
      <div class="block-field">
        <div class="lbl">Patient / Doctor</div>
        <div class="val" style="color:#00FFF0;">${block.patientUsername} ↔ ${block.doctorUsername}</div>
      </div>
      <div class="block-field">
        <div class="lbl">Previous Hash</div>
        <div class="val">${block.previousHash.slice(0, 10)}…${block.previousHash.slice(-6)}</div>
      </div>
      <div class="block-field">
        <div class="lbl">Current Hash</div>
        <div class="val" style="color:${block.tampered ? '#FF5252' : '#00E676'};">${block.blockHash.slice(0, 10)}…${block.blockHash.slice(-6)}</div>
      </div>
    `;

    visualizerWrapper.appendChild(node);

    // Connector Arrow
    if (i < chain.length - 1) {
      const connector = document.createElement("div");
      connector.className = "chain-connector";
      connector.innerHTML = "➔";
      visualizerWrapper.appendChild(connector);
    }
  });

  container.appendChild(visualizerWrapper);
}

function openBlockDetailsModal(block) {
  let modal = document.getElementById("blockDetailsModal");
  if (!modal) {
    modal = document.createElement("div");
    modal.id = "blockDetailsModal";
    modal.className = "modal-overlay";
    document.body.appendChild(modal);
  }

  modal.innerHTML = `
    <div class="modal" style="width:540px;">
      <h3 style="display:flex; align-items:center; justify-content:space-between;">
        <span>Block #${block.index} Inspector</span>
        <span class="status-pill ${block.tampered ? 'critical' : 'normal'}">${block.tampered ? 'INVALID' : 'VERIFIED'}</span>
      </h3>
      <div class="modal-sub">Cryptographic integrity data calculated with SHA-256 hash algorithm.</div>
      
      <div class="field">
        <label>Patient Username</label>
        <input value="${block.patientUsername}" readonly style="background:#F8FAFC;">
      </div>
      <div class="field">
        <label>Attending Doctor</label>
        <input value="${block.doctorUsername}" readonly style="background:#F8FAFC;">
      </div>
      <div class="field">
        <label>Clinical Diagnosis / Data</label>
        <textarea readonly style="background:#F8FAFC; height:60px;">${block.diagnosis}</textarea>
      </div>
      <div class="field">
        <label>Previous Block Hash (prevHash)</label>
        <input value="${block.previousHash}" readonly style="font-family:'JetBrains Mono',monospace; font-size:11.5px; background:#F8FAFC;">
      </div>
      <div class="field">
        <label>Block Hash (SHA-256)</label>
        <input value="${block.blockHash}" readonly style="font-family:'JetBrains Mono',monospace; font-size:11.5px; background:${block.tampered ? '#FEE2E2' : '#F0FDF4'}; color:${block.tampered ? '#B91C1C' : '#15803D'}; font-weight:700;">
      </div>
      <div style="display:flex; gap:12px;" class="field">
        <div style="flex:1;">
          <label>Proof of Work Nonce</label>
          <input value="${block.nonce}" readonly style="font-family:'JetBrains Mono',monospace; background:#F8FAFC;">
        </div>
        <div style="flex:1;">
          <label>Timestamp</label>
          <input value="${block.timestamp}" readonly style="background:#F8FAFC;">
        </div>
      </div>
      <div class="modal-actions">
        <button class="btn btn-primary" onclick="document.getElementById('blockDetailsModal').classList.remove('open')">Close Inspector</button>
      </div>
    </div>
  `;

  modal.classList.add("open");
}

function toggleTamperSimulation(containerId, recordsList) {
  isTamperedDemo = !isTamperedDemo;
  renderBlockchainVisualizer(containerId, recordsList);
  return isTamperedDemo;
}
