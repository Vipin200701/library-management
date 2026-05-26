let memberModal;
window.onload = () => { memberModal = new bootstrap.Modal(document.getElementById('memberModal')); loadMembers(); }

async function loadMembers() {
    const res = await request('/members');
    renderMembers(res.data || []);
}

async function searchMembers(name) {
    if (!name.trim()) return loadMembers();
    const res = await request(`/members/search?name=${name}`);
    renderMembers(res.data || []);
}

function renderMembers(members) {
    const tbody = document.getElementById('membersTable');
    if (!members.length) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No members found</td></tr>`;
        return;
    }
    tbody.innerHTML = members.map(m => `
        <tr>
            <td><strong>${m.name}</strong></td>
            <td>${m.email}</td>
            <td>${m.phone || '—'}</td>
            <td>${formatDate(m.membershipDate)}</td>
            <td><span class="badge-${m.isActive ? 'active' : 'inactive'}">${m.isActive ? 'Active' : 'Inactive'}</span></td>
            <td>
                <button class="btn btn-sm btn-outline-secondary py-0 me-1" onclick="openEditModal(${JSON.stringify(m).replace(/"/g, '&quot;')})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-${m.isActive ? 'warning' : 'success'} py-0 me-1"
                        onclick="toggleStatus(${m.id}, ${m.isActive})">
                    <i class="bi bi-${m.isActive ? 'person-dash' : 'person-check'}"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger py-0" onclick="deleteMember(${m.id}, '${m.name}')">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>`).join('');
}

function openAddModal() {
    document.getElementById('modalTitle').textContent = 'Add Member';
    document.getElementById('memberId').value = '';
    ['name','email','phone'].forEach(f => document.getElementById(f).value = '');
    memberModal.show();
}

function openEditModal(m) {
    document.getElementById('modalTitle').textContent = 'Edit Member';
    document.getElementById('memberId').value = m.id;
    document.getElementById('name').value     = m.name;
    document.getElementById('email').value    = m.email;
    document.getElementById('phone').value    = m.phone || '';
    memberModal.show();
}

async function saveMember() {
    const id   = document.getElementById('memberId').value;
    const body = {
        name:  document.getElementById('name').value.trim(),
        email: document.getElementById('email').value.trim(),
        phone: document.getElementById('phone').value.trim()
    };
    if (!body.name || !body.email) { showToast('Name and email are required', 'error'); return; }

    const res = id
        ? await request(`/members/${id}`, 'PUT', body)
        : await request('/members', 'POST', body);

    if (res.success) { showToast(res.message); memberModal.hide(); loadMembers(); }
    else showToast(res.message, 'error');
}

async function toggleStatus(id, isActive) {
    const url = isActive ? `/members/${id}/deactivate` : `/members/${id}/reactivate`;
    const res = await request(url, 'PATCH');
    if (res.success) { showToast(res.message); loadMembers(); }
    else showToast(res.message, 'error');
}

async function deleteMember(id, name) {
    if (!confirm(`Delete member "${name}"?`)) return;
    const res = await request(`/members/${id}`, 'DELETE');
    if (res.success) { showToast(res.message); loadMembers(); }
    else showToast(res.message, 'error');
}