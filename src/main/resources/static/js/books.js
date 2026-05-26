let bookModal;
window.onload = () => { bookModal = new bootstrap.Modal(document.getElementById('bookModal')); loadBooks(); }

async function loadBooks() {
    const res = await request('/books');
    renderBooks(res.data || []);
}

async function searchBooks(keyword) {
    if (!keyword.trim()) return loadBooks();
    const res = await request(`/books/search?keyword=${keyword}`);
    renderBooks(res.data || []);
}

function renderBooks(books) {
    const tbody = document.getElementById('booksTable');
    if (!books.length) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted">No books found</td></tr>`;
        return;
    }
    tbody.innerHTML = books.map(b => `
        <tr>
            <td><strong>${b.title}</strong></td>
            <td>${b.author}</td>
            <td style="font-size:12px;color:#718096">${b.isbn}</td>
            <td>${b.genre || '—'}</td>
            <td>${b.totalCopies}</td>
            <td>${b.availableCopies}</td>
            <td><span class="badge-${b.status === 'Available' ? 'available' : 'borrowed'}">${b.status}</span></td>
            <td>
                <button class="btn btn-sm btn-outline-secondary py-0 me-1" onclick="openEditModal(${JSON.stringify(b).replace(/"/g, '&quot;')})">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger py-0" onclick="deleteBook(${b.id}, '${b.title}')">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>`).join('');
}

function openAddModal() {
    document.getElementById('modalTitle').textContent = 'Add Book';
    document.getElementById('bookId').value = '';
    ['title','author','isbn','genre','publishedYear','totalCopies'].forEach(f => document.getElementById(f).value = '');
    bookModal.show();
}

function openEditModal(b) {
    document.getElementById('modalTitle').textContent = 'Edit Book';
    document.getElementById('bookId').value        = b.id;
    document.getElementById('title').value         = b.title;
    document.getElementById('author').value        = b.author;
    document.getElementById('isbn').value          = b.isbn;
    document.getElementById('genre').value         = b.genre || '';
    document.getElementById('publishedYear').value = b.publishedYear || '';
    document.getElementById('totalCopies').value   = b.totalCopies;
    bookModal.show();
}

async function saveBook() {
    const id = document.getElementById('bookId').value;
    const body = {
        title:         document.getElementById('title').value.trim(),
        author:        document.getElementById('author').value.trim(),
        isbn:          document.getElementById('isbn').value.trim(),
        genre:         document.getElementById('genre').value.trim(),
        publishedYear: parseInt(document.getElementById('publishedYear').value) || null,
        totalCopies:   parseInt(document.getElementById('totalCopies').value)
    };

    if (!body.title || !body.author || !body.isbn || !body.totalCopies) {
        showToast('Please fill all required fields', 'error'); return;
    }

    const res = id
        ? await request(`/books/${id}`, 'PUT', body)
        : await request('/books', 'POST', body);

    if (res.success) {
        showToast(res.message);
        bookModal.hide();
        loadBooks();
    } else {
        showToast(res.message, 'error');
    }
}

async function deleteBook(id, title) {
    if (!confirm(`Delete "${title}"?`)) return;
    const res = await request(`/books/${id}`, 'DELETE');
    if (res.success) { showToast(res.message); loadBooks(); }
    else showToast(res.message, 'error');
}