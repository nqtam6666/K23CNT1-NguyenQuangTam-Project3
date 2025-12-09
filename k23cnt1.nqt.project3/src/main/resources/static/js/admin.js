document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.querySelector('.sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function (e) {
            e.preventDefault();
            sidebar.classList.toggle('collapsed');

            // For mobile
            if (window.innerWidth <= 768) {
                sidebar.classList.toggle('toggled');
            }
        });
    }

    // Auto-collapse on small screens
    if (window.innerWidth <= 768) {
        sidebar.classList.add('collapsed');
    }

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function (e) {
        if (window.innerWidth <= 768 &&
            sidebar.classList.contains('toggled') &&
            !sidebar.contains(e.target) &&
            sidebarToggle && !sidebarToggle.contains(e.target)) { // Added check for sidebarToggle

            sidebar.classList.remove('toggled');
            sidebar.classList.add('collapsed');
        }
    });

    // Global Delete Confirmation
    document.addEventListener('click', function (e) {
        const deleteBtn = e.target.closest('.btn-delete');
        if (deleteBtn) {
            console.log('Delete button clicked');
            if (!confirm('Bạn có chắc chắn muốn xóa?')) {
                e.preventDefault();
                console.log('Delete cancelled');
            } else {
                console.log('Delete confirmed');
            }
        }
    });

    // Global Delete Confirmation
    document.addEventListener('click', function (e) {
        const deleteBtn = e.target.closest('.btn-delete');
        if (deleteBtn) {
            if (!confirm('Bạn có chắc chắn muốn xóa?')) {
                e.preventDefault();
            }
        }
    });
});
