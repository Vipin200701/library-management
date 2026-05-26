document.getElementById('dateLabel').textContent =
    new Date().toLocaleDateString('en-IN', { weekday:'long', year:'numeric', month:'long', day:'numeric' });

async function loadDashboard() {
    const [books, members, borrowed, overdue] = await Promise.all([
        request('/books'),
        request('/members'),
        request('/borrow/active'),
        request('/borrow/overdue')
    ]);

    document.getElementById('totalBooks').textContent    = books.data?.length    ?? 0;
    document.getElementById('totalMembers').textContent  = members.data?.length  ?? 0;
    document.getElementById('totalBorrowed').textContent = borrowed.data?.length ?? 0;
    document.getElementById('totalOverdue').textContent  = overdue.data?.length  ?? 0;

    // Overdue table
    const overdueTable = document.getElementById('overdueTable');
    if (!overdue.data?.length) {
        overdueTable.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No overdue books 🎉</td></tr>`;
    } else {
        overdueTable.innerHTML = overdue.data.map(r => `
            <tr>
                <td>${r.memberName}</td>
                <td>${r.bookTitle}</td>
                <td><span class="badge-overdue">${formatDate(r.dueDate)}</span></td>
            </tr>`).join('');
    }

    // Recent borrows table
    const recentTable = document.getElementById('recentTable');
    if (!borrowed.data?.length) {
        recentTable.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No active borrows</td></tr>`;
    } else {
        recentTable.innerHTML = borrowed.data.slice(0, 6).map(r => `
            <tr>
                <td>${r.memberName}</td>
                <td>${r.bookTitle}</td>
                <td>${formatDate(r.dueDate)}</td>
            </tr>`).join('');
    }
}

loadDashboard();