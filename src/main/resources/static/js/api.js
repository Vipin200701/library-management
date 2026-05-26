const API = 'http://localhost:8080/api';

async function request(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body) options.body = JSON.stringify(body);

    const res = await fetch(API + url, options);
    return await res.json();
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const id = 'toast_' + Date.now();
    const bg = type === 'success' ? '#48bb78' : '#fc8181';
    container.innerHTML += `
        <div id="${id}" class="toast align-items-center show"
             style="background:${bg};color:#fff;min-width:280px;border-radius:8px;padding:12px 16px;margin-bottom:8px;box-shadow:0 4px 12px rgba(0,0,0,0.15);">
            <div class="d-flex align-items-center gap-2">
                <i class="bi ${type === 'success' ? 'bi-check-circle' : 'bi-x-circle'}"></i>
                <span>${message}</span>
            </div>
        </div>`;
    setTimeout(() => document.getElementById(id)?.remove(), 3000);
}

function formatDate(dateStr) {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
}