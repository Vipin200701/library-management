// ─────────────────────────────────────────────────────────────
//  Init
// ─────────────────────────────────────────────────────────────
window.onload = () => {
    loadTab('active');
    setupReturnPreview();
    setupOutsideClickClose();
};

// ─────────────────────────────────────────────────────────────
//  Return preview — auto-fetch record details as user types ID
// ─────────────────────────────────────────────────────────────
function setupReturnPreview() {
    document.getElementById('returnRecordId').addEventListener('input', async function () {
        const preview = document.getElementById('returnPreview');

        if (!this.value.trim()) {
            preview.innerHTML = 'Record details will appear here after you enter a Record ID';
            preview.style.color = '#718096';
            return;
        }

        preview.innerHTML = '<span class="text-muted">Looking up record...</span>';

        const res = await request(`/borrow/${this.value}`);

        if (res.success && res.data) {
            const d = res.data;
            const statusClass = d.status.toLowerCase();
            preview.innerHTML = `
                <div class="row g-1">
                    <div class="col-5 text-muted">Member</div>
                    <div class="col-7"><strong>${d.memberName}</strong></div>
                    <div class="col-5 text-muted">Book</div>
                    <div class="col-7"><strong>${d.bookTitle}</strong></div>
                    <div class="col-5 text-muted">Borrowed On</div>
                    <div class="col-7">${formatDate(d.borrowDate)}</div>
                    <div class="col-5 text-muted">Due Date</div>
                    <div class="col-7">${formatDate(d.dueDate)}</div>
                    <div class="col-5 text-muted">Status</div>
                    <div class="col-7">
                        <span class="badge-${statusClass}">${d.status}</span>
                    </div>
                </div>`;
        } else {
            preview.innerHTML = `<span class="text-danger">
                <i class="bi bi-x-circle me-1"></i>Record not found
            </span>`;
        }
    });
}

// ─────────────────────────────────────────────────────────────
//  Close dropdowns when clicking outside
// ─────────────────────────────────────────────────────────────
function setupOutsideClickClose() {
    document.addEventListener('click', (e) => {
        if (!e.target.closest('#memberSearch') && !e.target.closest('#memberDropdown')) {
            closeDropdown('memberDropdown');
        }
        if (!e.target.closest('#bookSearch') && !e.target.closest('#bookDropdown')) {
            closeDropdown('bookDropdown');
        }
    });
}

function closeDropdown(id) {
    document.getElementById(id).classList.remove('open');
}

// ─────────────────────────────────────────────────────────────
//  Member search dropdown
// ─────────────────────────────────────────────────────────────
async function searchMemberDropdown(query) {
    const dropdown = document.getElementById('memberDropdown');

    if (!query.trim()) {
        closeDropdown('memberDropdown');
        return;
    }

    dropdown.innerHTML = `<div class="dropdown-item-custom text-muted">Searching...</div>`;
    dropdown.classList.add('open');

    const res = await request(`/members/search?name=${encodeURIComponent(query)}`);
    const members = res.data || [];

    if (!members.length) {
        dropdown.innerHTML = `<div class="dropdown-item-custom text-muted">No members found</div>`;
        return;
    }

    dropdown.innerHTML = members.map(m => `
        <div class="dropdown-item-custom"
             onclick="selectMember(${m.id}, '${escapeSingleQuote(m.name)}', '${m.email}')">
            <div>
                <div style="font-weight:500">${m.name}</div>
                <div class="item-sub">${m.email} &nbsp;|&nbsp; ${m.phone || 'No phone'}</div>
            </div>
            <div class="text-end">
                <span class="item-id">ID: ${m.id}</span><br/>
                <span style="font-size:10px;color:${m.isActive ? '#48bb78' : '#fc8181'}">
                    ${m.isActive ? 'Active' : 'Inactive'}
                </span>
            </div>
        </div>`).join('');
}

function selectMember(id, name, email) {
    document.getElementById('issueMemberId').value = id;
    document.getElementById('memberSearch').value  = '';
    document.getElementById('selectedMemberText').textContent =
        `${name} (${email})  —  ID: ${id}`;
    document.getElementById('selectedMember').classList.remove('d-none');
    closeDropdown('memberDropdown');
}

function clearMember() {
    document.getElementById('issueMemberId').value = '';
    document.getElementById('memberSearch').value  = '';
    document.getElementById('selectedMember').classList.add('d-none');
}

// ─────────────────────────────────────────────────────────────
//  Book search dropdown
// ─────────────────────────────────────────────────────────────
async function searchBookDropdown(query) {
    const dropdown = document.getElementById('bookDropdown');

    if (!query.trim()) {
        closeDropdown('bookDropdown');
        return;
    }

    dropdown.innerHTML = `<div class="dropdown-item-custom text-muted">Searching...</div>`;
    dropdown.classList.add('open');

    const res = await request(`/books/search?keyword=${encodeURIComponent(query)}`);
    const books = res.data || [];

    if (!books.length) {
        dropdown.innerHTML = `<div class="dropdown-item-custom text-muted">No books found</div>`;
        return;
    }

    dropdown.innerHTML = books.map(b => {
        const available  = b.availableCopies > 0;
        const clickable  = available
            ? `onclick="selectBook(${b.id}, '${escapeSingleQuote(b.title)}', '${escapeSingleQuote(b.author)}', ${b.availableCopies})"`
            : '';
        const copiesColor = available ? '#48bb78' : '#fc8181';
        const copiesLabel = available ? `${b.availableCopies} available` : 'Not available';

        return `
        <div class="dropdown-item-custom ${!available ? 'text-muted' : ''}"
             style="cursor:${available ? 'pointer' : 'not-allowed'}"
             ${clickable}>
            <div>
                <div style="font-weight:${available ? '500' : '400'}">${b.title}</div>
                <div class="item-sub">${b.author} &nbsp;|&nbsp; ${b.genre || 'No genre'}</div>
            </div>
            <div class="text-end">
                <span class="item-id">ID: ${b.id}</span><br/>
                <span style="font-size:10px;color:${copiesColor}">${copiesLabel}</span>
            </div>
        </div>`;
    }).join('');
}

function selectBook(id, title, author, available) {
    document.getElementById('issueBookId').value  = id;
    document.getElementById('bookSearch').value   = '';
    document.getElementById('selectedBookText').textContent =
        `${title} by ${author}  —  ID: ${id}  (${available} ${available === 1 ? 'copy' : 'copies'} left)`;
    document.getElementById('selectedBook').classList.remove('d-none');
    closeDropdown('bookDropdown');
}

function clearBook() {
    document.getElementById('issueBookId').value = '';
    document.getElementById('bookSearch').value  = '';
    document.getElementById('selectedBook').classList.add('d-none');
}

// ─────────────────────────────────────────────────────────────
//  Issue book
// ─────────────────────────────────────────────────────────────
async function issueBook() {
    const memberId = document.getElementById('issueMemberId').value;
    const bookId   = document.getElementById('issueBookId').value;
    const dueDate  = document.getElementById('issueDueDate').value;

    if (!memberId) { showToast('Please select a member first', 'error'); return; }
    if (!bookId)   { showToast('Please select a book first', 'error');   return; }

    const body = {
        memberId: parseInt(memberId),
        bookId:   parseInt(bookId)
    };
    if (dueDate) body.dueDate = dueDate;

    const res = await request('/borrow/issue', 'POST', body);

    if (res.success) {
        showToast(`Book issued successfully!`);
        clearMember();
        clearBook();
        document.getElementById('issueDueDate').value = '';
        loadTab('active');
    } else {
        showToast(res.message, 'error');
    }
}

// ─────────────────────────────────────────────────────────────
//  Return book
// ─────────────────────────────────────────────────────────────
async function returnBook() {
    const id = document.getElementById('returnRecordId').value;
    if (!id) { showToast('Please enter a Borrow Record ID', 'error'); return; }

    await quickReturn(parseInt(id));

    document.getElementById('returnRecordId').value = '';
    document.getElementById('returnPreview').innerHTML =
        'Record details will appear here after you enter a Record ID';
}

async function quickReturn(id) {
    const res = await request(`/borrow/return/${id}`, 'PATCH');
    if (res.success) {
        showToast(res.message);
        loadTab('active');
    } else {
        showToast(res.message, 'error');
    }
}

// ─────────────────────────────────────────────────────────────
//  Tab switching — Active / Overdue
// ─────────────────────────────────────────────────────────────
async function loadTab(type) {
    const res = await request(`/borrow/${type}`);
    renderTable(res.data || [], type);
}

function switchTab(type, el) {
    document.querySelectorAll('#borrowTabs .nav-link')
            .forEach(t => t.classList.remove('active'));
    el.classList.add('active');
    loadTab(type);
}

function renderTable(records, type) {
    const tbody = document.getElementById('borrowTable');

    if (!records.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="bi bi-inbox me-2"></i>No ${type} records found
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = records.map(r => {
        const statusClass = r.status.toLowerCase();
        return `
        <tr>
            <td><strong>#${r.id}</strong></td>
            <td>${r.memberName}</td>
            <td>${r.bookTitle}</td>
            <td>${formatDate(r.borrowDate)}</td>
            <td>${formatDate(r.dueDate)}</td>
            <td><span class="badge-${statusClass}">${r.status}</span></td>
            <td>
                ${r.status !== 'RETURNED'
                    ? `<button class="btn btn-sm btn-outline-success py-0"
                               onclick="quickReturn(${r.id})">
                           <i class="bi bi-arrow-return-left"></i> Return
                       </button>`
                    : '<span class="text-muted">—</span>'}
            </td>
        </tr>`;
    }).join('');
}

// ─────────────────────────────────────────────────────────────
//  Mark overdue manually
// ─────────────────────────────────────────────────────────────
async function markOverdue() {
    const res = await request('/borrow/mark-overdue', 'PATCH');
    if (res.success) {
        showToast(res.message);
        loadTab('active');
    } else {
        showToast(res.message, 'error');
    }
}

// ─────────────────────────────────────────────────────────────
//  Utility
// ─────────────────────────────────────────────────────────────
function escapeSingleQuote(str) {
    return str ? str.replace(/'/g, "\\'") : '';
}