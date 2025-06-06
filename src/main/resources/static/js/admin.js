if (document.location.pathname === '/admin/users') {
    $(document).ready(function () {
        let token = $('#_csrf').attr('content');
        loadUsers();
        $('#searchButton').click(function () {
            loadUsers($('#searchInput').val());
        });
        $('#searchInput').keypress(function (e) {
            if (e.which == 13) {
                loadUsers($(this).val());
            }
        });
        $('#saveUserBtn').click(function () {
            addUser();
        });
        $('#updateUserBtn').click(function () {
            updateUser();
        });
    });

    function loadUsers(search = '', page = 1) {
        $('#usersTableBody').html(`
                <tr>
                    <td colspan="7" class="text-center py-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </td>
                </tr>
            `);
        sendRequest('GET', '/admin/users/manage', JSON.stringify({search: search, page: page}))
            .then(response => {
                let html = '';
                response.forEach(function (user) {
                    let stringRoles = "";
                    user.roles.sort((a, b) => {
                        a = a.role.replace("ROLE_", "")
                        b = b.role.replace("ROLE_", "")
                        return a.localeCompare(b);
                    });
                    user.roles.forEach(function (item) {
                        stringRoles += `<div class="badge role-badge-active">${item.role}</div>`;
                    })
                    html += `
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.username}</td>
                                <td>
                                    <div class="flex-container-copy">
                                        <div id="copiEmail_${user.id}">${user.email}</div>
                                        <span id="copy-message_${user.id}" style="display: none;">Copied!</span>
                                        <button class="copy-button" data-user-id="${user.id}">
                                            <i class="fas fa-copy"></i>
                                        </button>
                                    </div>
                                </td>
                                <td>${stringRoles}</td>
                                <td><span class="badge ${user.enabled ? 'badge-active' : 'badge-inactive'}">
                                    ${user.enabled ? 'Enable' : 'Disable'}
                                </span></td>
                                <td>${new Date(user.createdDate).toLocaleDateString()}</td>
                                <td>
                                    <button class="action-btn btn-edit" data-bs-toggle="modal" data-bs-target="#editUserModal" onclick="loadUser(${user.id})">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="action-btn btn-delete" onclick="confirmDelete(${user.id})">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </td>
                            </tr>
                        `;
                });
                $('#usersTableBody').html(html);
                document.querySelectorAll('.copy-button').forEach((element) => {
                    element.addEventListener('click', async function () {
                        try {
                            const userid = element.getAttribute('data-user-id')
                            const textToCopy = document.getElementById(`copiEmail_${userid}`).textContent;
                            await navigator.clipboard.writeText(textToCopy);

                            // const message = document.getElementById(`copy-message_${userid}`);
                            // message.style.display = 'inline';
                            // setTimeout(() => {
                            //     message.style.display = 'none';
                            // }, 2000);
                        } catch (err) {
                            console.error('Failed to copy text: ', err);
                        }
                    })
                });
                updatePagination(response.totalPages, page, search);
            })
            .catch(error => {
                console.error(error);
                $('#usersTableBody').html(`
                        <tr>
                            <td colspan="7" class="text-center py-5 text-danger">
                                Not found
                            </td>
                        </tr>
                    `);
            });
    }

    function updatePagination(totalPages, currentPage, search) {
        let html = '';
        if (currentPage > 1) {
            html += `
                    <li class="page-item">
                        <a class="page-link" href="#" onclick="loadUsers('${search}', ${currentPage - 1})">Back</a>
                    </li>
                `;
        } else {
            html += `
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1">Back</a>
                    </li>
                `;
        }
        for (let i = 1; i <= totalPages; i++) {
            if (i == currentPage) {
                html += `
                        <li class="page-item active"><a class="page-link" href="#">${i}</a></li>
                    `;
            } else {
                html += `
                        <li class="page-item"><a class="page-link" href="#" onclick="loadUsers('${search}', ${i})">${i}</a></li>
                    `;
            }
        }
        if (currentPage < totalPages) {
            html += `
                    <li class="page-item">
                        <a class="page-link" href="#" onclick="loadUsers('${search}', ${currentPage + 1})">Next</a>
                    </li>
                `;
        } else {
            html += `
                    <li class="page-item disabled">
                        <a class="page-link" href="#">Next</a>
                    </li>
                `;
        }
        $('#pagination').html(html);
    }

    function loadUser(userId) {
        $('#userEditContent').html(`
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `);

        $.ajax({
            url: '/admin/users/manage/' + userId + '/edit',
            type: 'GET',
            success: function (response) {
                const listRoles = ["ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER", "OAUTH2_USER", "OIDC_USER"];
                let optionsReal = ''
                let optionsBox = ''
                let selectedItems = '';
                let selectedRoles = {};
                response.roles.forEach(function (role) {
                    selectedRoles[role.role] = role.id;
                });
                listRoles.forEach(function (arrRole, index) {

                    let dbRoleId = selectedRoles[arrRole]?selectedRoles[arrRole]:'';

                    optionsReal += `<option data-id="${dbRoleId}" value="${arrRole}" selected="${dbRoleId !== '' ? 'selected' : ''}">${arrRole}</option>`;
                    optionsBox += `<div data-id="${dbRoleId}" class="option ${dbRoleId !== '' ? 'selected' : ''}" data-value="${arrRole}" >${arrRole}</div>`;

                    if (dbRoleId !== '') {
                        selectedItems += `<div class="selected-item">${arrRole}<span class="remove">×</span></div>`;
                    }
                })

                $('#userEditContent').html(`
                        <form id="editUserForm">
                            <input type="hidden" id="editUserId" value="${response.id}">
                            
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editUsername" class="form-label">Username</label>
                                    <input type="text" class="form-control" id="editUsername" value="${response.username}" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="editEmail" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="editEmail" value="${response.email || ''}" required>
                                </div>
                            </div>
                            
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editFirstName" class="form-label">Name</label>
                                    <input type="text" class="form-control" id="editFirstName" value="${response.name || ''}">
                                </div>
                                <div class="col-md-6">
                                    <label for="realSelect" class="form-label">Role</label>
                                     <div class="custom-multiselect" tabindex="0">
                                            <select id="realSelect" multiple style="display: none;">
                                            </select>
                                            
                                            <div class="select-box " id="selectBox">
                                                <div class="placeholder_multiselect no-select">Select roles...</div>
                                                <img id="placeholderImg" src="/images/icons8-expand-24.png" alt="">
                                            </div>
                                            
                                            <div class="options-container" id="optionsContainer">
                                            </div>
                                        </div>
                                </div>
                                
                            </div>
                            
                            <div class="row mb-3">
 
                                <div class="col-md-6">
                                    <label for="editStatus" class="form-label">Status</label>
                                    <select class="form-select" id="editStatus" required>
                                        <option value="active" ${response.enabled ? 'selected' : ''}>Enable</option>
                                        <option value="inactive" ${!response.enabled ? 'selected' : ''}>Disable</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="editPassword" class="form-label">New password (leave blank to not change)</label>
                                    <input type="password" class="form-control" id="editPassword">
                                </div>
                            </div>

                        </form>
                    `);
                $('#realSelect').html(optionsReal);
                $('#optionsContainer').append(optionsBox);
                $('#selectBox').append(selectedItems);
                const preSelectedItems = document.querySelectorAll(".selected-item");
                const realSelect = document.getElementById('realSelect');
                const selectBox = document.getElementById('selectBox');
                const optionsContainer = document.getElementById('optionsContainer');
                const searchInput = document.getElementById('searchInput');
                const options = document.querySelectorAll('.option');
                const placeholder = selectBox.querySelector('.placeholder_multiselect');
                const placeholderImg = selectBox.querySelector('#placeholderImg');

                if (selectedItems.length > 0) {
                    if (placeholder && placeholderImg) {
                        placeholder.remove();
                        placeholderImg.remove();
                    }
                    preSelectedItems.forEach((selectedItem) => {
                        const value = selectedItem.firstChild.textContent;
                        selectedItem.addEventListener('click', function (e) {
                            e.stopPropagation();
                            selectedItem.selected = false;
                            options.forEach((option) => {
                                if (option.getAttribute("data-value") === value) {
                                    option.classList.remove('selected');
                                }
                            })
                            selectedItem.remove();
                            realSelect.querySelector(`option[value="${value}"]`).setAttribute("selected", "");
                            if (selectBox.querySelectorAll('.selected-item').length === 0) {
                                selectBox.appendChild(placeholder);
                                selectBox.appendChild(placeholderImg);
                            }
                        });
                    })
                }
                selectBox.addEventListener('click', function (e) {
                    selectBox.classList.add('select-box-focus');
                    e.stopPropagation();
                    optionsContainer.classList.toggle('show');
                    if (optionsContainer.classList.contains('show')) {
                        searchInput.focus();
                    }
                });
                options.forEach(option => {
                    option.addEventListener('click', function () {
                        const value = this.getAttribute('data-value');
                        const text = this.textContent;

                        this.classList.toggle('selected');

                        if (this.classList.contains('selected')) {

                            realSelect.querySelector(`option[value="${value}"]`).setAttribute("selected", "selected");

                            if (placeholder && placeholderImg) {
                                placeholder.remove();
                                placeholderImg.remove();
                            }

                            const selectedItem = document.createElement('div');
                            selectedItem.className = 'selected-item';
                            selectedItem.innerHTML = `${text}<span class="remove">×</span>`;
                            selectBox.insertBefore(selectedItem, selectBox.firstChild);

                            selectedItem.addEventListener('click', function (e) {
                                e.stopPropagation();
                                realSelect.querySelector(`option[value="${value}"]`).setAttribute("selected", "");
                                option.classList.remove('selected');
                                selectedItem.remove();

                                if (selectBox.querySelectorAll('.selected-item').length === 0) {
                                    selectBox.appendChild(placeholder);
                                    selectBox.appendChild(placeholderImg);
                                }
                            });
                        } else {
                            realSelect.querySelector(`option[value="${value}"]`).setAttribute("selected", "");

                            const items = selectBox.querySelectorAll('.selected-item');
                            for (let item of items) {
                                if (item.textContent.trim() === text) {
                                    item.remove();
                                    break;
                                }
                            }

                            if (selectBox.querySelectorAll('.selected-item').length === 0) {
                                selectBox.appendChild(placeholder);
                                selectBox.appendChild(placeholderImg);
                            }
                        }
                    });
                });

                searchInput.addEventListener('input', function () {
                    const searchTerm = this.value.toLowerCase();
                    options.forEach(option => {
                        const text = option.textContent.toLowerCase();
                        option.style.display = text.includes(searchTerm) ? 'block' : 'none';
                    });
                });

                document.addEventListener('click', function () {
                    optionsContainer.classList.remove('show');
                    selectBox.classList.remove('select-box-focus');
                });

                optionsContainer.addEventListener('click', function (e) {
                    e.stopPropagation();
                });
                // });
            },
            error: function () {
                $('#userEditContent').html(`
                        <div class="alert alert-danger">
                            Error loading user data
                        </div>
                    `);
            }
        });


    }

    function addUser() {
        if (!$('#addUserForm')[0].checkValidity()) {
            alert('Please fill in all required fields');
            return;
        }


        const username = $('#username').val();
        const email = $('#email').val();
        const password = $('#password').val();
        if (password !== $('#confirmPassword').val()) {
            alert('The passwords do not match');
            return;
        }
        const name = $('#firstName').val();
        const newRoles = $('#optionsContainer div.selected').map(function () {
            return $(this).text();
        }).get();
        const enabled = $('#status').val() === 'active';
        const userData = {
            username: username !== "" ? username : null,
            email: email !== "" ? email : null,
            password: password !== "" ? password : null,
            name: name !== "" ? name : null,
            roles: newRoles !== "" ? newRoles : null,
            enabled: enabled,
        };

        $.ajax({
            url: '/admin/users/manage/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(userData),
            headers: {
                "X-CSRF-TOKEN": $('#_csrf').attr('content')
            },
            success: function (response) {
                $('#addUserForm')[0].reset();
                $('#addUserModal').modal('hide');
                loadUsers();
                alert('User added successfully');
            },
            error: function (xhr) {
                alert(xhr.responseJSON || 'Error adding user');
            }
        });
    }

    function updateUser() {
        const userId = $('#editUserId').val();
        const newRoles = $('#optionsContainer div.selected').map(function () {

            let role = {}
            role['id'] = this.dataset.id;
            role['role'] = $(this).text();
            return role;
        }).get();

        const username = $('#editUsername').val();
        const email = $('#editEmail').val();
        const password = $('#editPassword').val();
        if (password !== $('#confirmPassword').val()) {
            alert('The passwords do not match');
            return;
        }
        const name = $('#editFirstName').val();
        const enabled = $('#editStatus').val() === 'active';
        const userData = {
            username: username !== "" ? username : null,
            email: email !== "" ? email : null,
            password: password !== "" ? password : null,
            name: name !== "" ? name : null,
            roles: newRoles !== "" ? newRoles : null,
            enabled: enabled
        };

        $.ajax({
            url: '/admin/users/manage/' + userId,
            type: 'PUT',
            contentType: 'application/json',
            headers: {
                "X-CSRF-TOKEN": $('#_csrf').attr('content')
            },
            data: JSON.stringify(userData),
            success: function (response) {
                $('#editUserModal').modal('hide');
                loadUsers();
                alert('Changes saved successfully');
            },
            error: function (xhr) {
                alert(xhr.responseJSON || 'Error saving changes');
            }
        });
    }

    function confirmDelete(userId) {
        if (confirm('Are you sure you want to delete this user?')) {
            $.ajax({
                url: '/admin/users/manage/' + userId,
                type: 'DELETE',
                headers: {
                    "X-CSRF-TOKEN": $('#_csrf').attr('content')
                },
                success: function () {
                    loadUsers();
                    alert('User successfully deleted');
                },
                error: function (xhr) {
                    alert(xhr.responseJSON || 'Error deleting user');
                }
            });
        }
    }
} else if (document.location.pathname === '/admin/settings') {
    $(document).ready(function () {
        loadSettings();

        $('#saveGeneralSettings').click(function () {
            saveSettings('general');
        });
        $('#saveEmailSettings').click(function () {
            saveSettings('email');
        });
        $('#saveSecuritySettings').click(function () {
            saveSettings('security');
        });
        $('#saveMaintenanceSettings').click(function () {
            saveSettings('maintenance');
        });

        $('#testEmailBtn').click(function () {
            $.ajax({
                url: '/admin/settings/test-email',
                type: 'POST',
                success: function (response) {
                    alert('Test email sent successfully!');
                },
                error: function (xhr) {
                    alert('Error sending test email: ' + (xhr.responseJSON || 'Unknown error'));
                }
            });
        });

        $('#backupNowBtn').click(function () {
            if (confirm('Are you sure you want to create a backup now?')) {
                $.ajax({
                    url: '/admin/settings/backup-now',
                    type: 'POST',
                    success: function (response) {
                        alert('Backup created successfully!');
                    },
                    error: function (xhr) {
                        alert('Error creating backup: ' + (xhr.responseJSON || 'Unknown error'));
                    }
                });
            }
        });
    });

    function loadSettings() {
        $.ajax({
            url: '/admin/settings',
            type: 'GET',
            success: function (response) {
                $('#siteName').val(response.siteName || '');
                $('#siteUrl').val(response.siteUrl || '');
                $('#siteDescription').val(response.siteDescription || '');
                $('#siteKeywords').val(response.siteKeywords || '');
                $('#themeColor').val(response.themeColor || '#66CC00');
                $('#timezone').val(response.timezone || 'UTC');
                $('#dateFormat').val(response.dateFormat || 'MM/dd/yyyy');
                $('#itemsPerPage').val(response.itemsPerPage || 10);

                $('#smtpHost').val(response.smtpHost || '');
                $('#smtpPort').val(response.smtpPort || '');
                $('#smtpUsername').val(response.smtpUsername || '');
                $('#smtpPassword').val(response.smtpPassword || '');
                $('#smtpProtocol').val(response.smtpProtocol || 'smtp');
                $('#fromEmail').val(response.fromEmail || '');
                $('#fromName').val(response.fromName || '');
                $('#welcomeEmailSubject').val(response.welcomeEmailSubject || '');
                $('#passwordResetSubject').val(response.passwordResetSubject || '');

                $('#passwordMinLength').val(response.passwordMinLength || 8);
                $('#passwordComplexity').val(response.passwordComplexity || 'medium');
                $('#loginAttempts').val(response.loginAttempts || 5);
                $('#lockoutTime').val(response.lockoutTime || 30);
                $('#sessionTimeout').val(response.sessionTimeout || 30);
                $('#rememberMeDuration').val(response.rememberMeDuration || 7);
                $('#forceHttps').prop('checked', response.forceHttps || false);
                $('#enableCsrf').prop('checked', response.enableCsrf !== false);

                $('#maintenanceMode').prop('checked', response.maintenanceMode || false);
                $('#maintenanceMessage').val(response.maintenanceMessage || '');
                $('#allowedIPs').val(response.allowedIPs || '');
                $('#allowedRoles').val(response.allowedRoles || []);
                $('#backupFrequency').val(response.backupFrequency || 'daily');
                $('#backupTime').val(response.backupTime || '02:00');
                $('#backupLocation').val(response.backupLocation || '');
            },
            error: function (xhr) {
                // alert('Error loading settings: ' + (xhr.responseJSON || 'Unknown error'));
            }
        });
    }

    function saveSettings(type) {
        let formData = {};
        let formId = '';

        switch (type) {
            case 'general':
                formId = '#generalSettingsForm';
                break;
            case 'email':
                formId = '#emailSettingsForm';
                break;
            case 'security':
                formId = '#securitySettingsForm';
                break;
            case 'maintenance':
                formId = '#maintenanceSettingsForm';
                break;
        }

        if (!$(formId)[0].checkValidity()) {
            alert('Please fill all required fields');
            return;
        }

        $(formId).serializeArray().forEach(function (item) {
            formData[item.name] = item.value;
        });

        if (type === 'security') {
            formData.forceHttps = $('#forceHttps').is(':checked');
            formData.enableCsrf = $('#enableCsrf').is(':checked');
        } else if (type === 'maintenance') {
            formData.maintenanceMode = $('#maintenanceMode').is(':checked');
            formData.allowedRoles = $('#allowedRoles').val();
        }

        $.ajax({
            url: '/admin/settings/' + type,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                alert('Settings saved successfully!');
            },
            error: function (xhr) {
                alert('Error saving settings: ' + (xhr.responseJSON || 'Unknown error'));
            }
        });
    }
}
else if (document.location.pathname === '/admin') {
    $(document).ready(function () {
        // Load dashboard data
        loadDashboardData();

        // Initialize charts
        initializeCharts();
    });

// Current time range
    let currentTimeRange = '30days';

    function changeTimeRange(range) {
        currentTimeRange = range;
        $('#timeRangeDropdown').text(
            range === '7days' ? 'Last 7 Days' :
                range === '30days' ? 'Last 30 Days' :
                    range === '90days' ? 'Last 90 Days' : 'Last 12 Months'
        );
        loadDashboardData();
    }

    function loadDashboardData() {
        $('#totalUsers').html('<i class="fas fa-spinner fa-spin"></i>');
        $('#totalOrders').html('<i class="fas fa-spinner fa-spin"></i>');
        $('#totalRevenue').html('<i class="fas fa-spinner fa-spin"></i>');
        $('#growthRate').html('<i class="fas fa-spinner fa-spin"></i>');

        $.ajax({
            url: '/api/dashboard/stats',
            type: 'GET',
            data: {range: currentTimeRange},
            success: function (response) {
                $('#totalUsers').text(response.totalUsers);
                $('#totalOrders').text(response.totalOrders);
                $('#totalRevenue').text('$' + response.totalRevenue.toLocaleString());
                $('#growthRate').text(response.growthRate + '%');

                updateSalesChart(response.salesData);
                updateRevenueChart(response.revenueByCategory);

                loadRecentActivity();

                loadTopProducts();
            },
            error: function (xhr) {
                console.error('Error loading dashboard data:', xhr);
                // alert('Error loading dashboard data. Please try again.');
            }
        });
    }

    let salesChart, revenueChart;

    function initializeCharts() {
        const salesCtx = document.getElementById('salesChart').getContext('2d');
        salesChart = new Chart(salesCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Sales',
                    data: [],
                    backgroundColor: 'rgba(52, 152, 219, 0.2)',
                    borderColor: 'rgba(52, 152, 219, 1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        const revenueCtx = document.getElementById('revenueChart').getContext('2d');
        revenueChart = new Chart(revenueCtx, {
            type: 'doughnut',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    backgroundColor: [
                        'rgba(52, 152, 219, 0.7)',
                        'rgba(46, 204, 113, 0.7)',
                        'rgba(231, 76, 60, 0.7)',
                        'rgba(243, 156, 18, 0.7)',
                        'rgba(155, 89, 182, 0.7)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    function updateSalesChart(salesData) {
        salesChart.data.labels = salesData.labels;
        salesChart.data.datasets[0].data = salesData.values;
        salesChart.update();
    }

    function updateRevenueChart(revenueData) {
        revenueChart.data.labels = revenueData.labels;
        revenueChart.data.datasets[0].data = revenueData.values;
        revenueChart.update();
    }

    function loadRecentActivity() {
        $.ajax({
            url: '/api/dashboard/activity',
            type: 'GET',
            data: {limit: 5},
            success: function (response) {
                let html = '';
                response.forEach(function (activity) {
                    html += `
                            <div class="recent-activity-item">
                                <div class="d-flex justify-content-between">
                                    <strong>${activity.action}</strong>
                                    <span class="activity-time">${formatTime(activity.timestamp)}</span>
                                </div>
                                <div>${activity.details}</div>
                                <div class="text-muted small">By ${activity.user}</div>
                            </div>
                        `;
                });

                if (html === '') {
                    html = '<div class="text-center py-3 text-muted">No recent activity</div>';
                }

                $('#recentActivity').html(html);
            },
            error: function (xhr) {
                $('#recentActivity').html('<div class="alert alert-danger">Error loading recent activity</div>');
            }
        });
    }

    function loadTopProducts() {
        $.ajax({
            url: '/api/dashboard/topproducts',
            type: 'GET',
            data: {limit: 5},
            success: function (response) {
                let html = '';
                response.forEach(function (product, index) {
                    html += `
                            <div class="recent-activity-item">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <strong>#${index + 1} ${product.name}</strong>
                                        <div class="text-muted small">${product.category}</div>
                                    </div>
                                    <div class="text-end">
                                        <div class="text-success">$${product.revenue.toLocaleString()}</div>
                                        <div class="text-muted small">${product.sales} sales</div>
                                    </div>
                                </div>
                            </div>
                        `;
                });

                if (html === '') {
                    html = '<div class="text-center py-3 text-muted">No product data available</div>';
                }

                $('#topProducts').html(html);
            },
            error: function (xhr) {
                $('#topProducts').html('<div class="alert alert-danger">Error loading top products</div>');
            }
        });
    }

    function formatTime(timestamp) {
        const date = new Date(timestamp);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
    }

}
else if (document.location.pathname === '/admin/statistics') {
    $(document).ready(function () {
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(endDate.getDate() - 30);

        $('#startDate').val(startDate.toISOString().split('T')[0]);
        $('#endDate').val(endDate.toISOString().split('T')[0]);

        loadStatistics();

        $('#applyFilter').click(function () {
            loadStatistics();
        });
    });

    let userGrowthChart, rolesChart, activityChart, trafficChart;

    function loadStatistics() {
        const startDate = $('#startDate').val();
        const endDate = $('#endDate').val();

        $('.value').html('<i class="fas fa-spinner fa-spin"></i>');

        $.ajax({
            url: '/admin/statistics/data',
            type: 'GET',
            data: {startDate: startDate, endDate: endDate},
            success: function (response) {
                $('#totalUsers').text(response.totalUsers);
                $('#activeUsers').text(response.activeUsers);
                $('#newUsers').text(response.newUsers);
                $('#avgSessions').text(response.avgSessions.toFixed(1));

                initUserGrowthChart(response.userGrowth);
                initRolesChart(response.rolesDistribution);
                initActivityChart(response.dailyActivity);
                initTrafficChart(response.trafficSources);
            },
            error: function (xhr) {
                // alert('Error loading statistics data');
                console.error(xhr);
            }
        });
    }

    function initUserGrowthChart(data) {
        const ctx = document.getElementById('userGrowthChart').getContext('2d');

        if (userGrowthChart) {
            userGrowthChart.destroy();
        }

        userGrowthChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.labels,
                datasets: [{
                    label: 'Total Users',
                    data: data.values,
                    backgroundColor: 'rgba(102, 204, 0, 0.1)',
                    borderColor: 'rgba(102, 204, 0, 1)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function initRolesChart(data) {
        const ctx = document.getElementById('rolesChart').getContext('2d');

        if (rolesChart) {
            rolesChart.destroy();
        }

        rolesChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.values,
                    backgroundColor: [
                        'rgba(102, 204, 0, 0.7)',
                        'rgba(54, 162, 235, 0.7)',
                        'rgba(255, 206, 86, 0.7)',
                        'rgba(75, 192, 192, 0.7)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                    }
                }
            }
        });
    }

    function initActivityChart(data) {
        const ctx = document.getElementById('activityChart').getContext('2d');

        if (activityChart) {
            activityChart.destroy();
        }

        activityChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [
                    {
                        label: 'Logins',
                        data: data.logins,
                        backgroundColor: 'rgba(102, 204, 0, 0.7)',
                        borderColor: 'rgba(102, 204, 0, 1)',
                        borderWidth: 1
                    },
                    {
                        label: 'Actions',
                        data: data.actions,
                        backgroundColor: 'rgba(54, 162, 235, 0.7)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function initTrafficChart(data) {
        const ctx = document.getElementById('trafficChart').getContext('2d');

        if (trafficChart) {
            trafficChart.destroy();
        }

        trafficChart = new Chart(ctx, {
            type: 'polarArea',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.values,
                    backgroundColor: [
                        'rgba(102, 204, 0, 0.7)',
                        'rgba(54, 162, 235, 0.7)',
                        'rgba(255, 99, 132, 0.7)',
                        'rgba(255, 206, 86, 0.7)',
                        'rgba(75, 192, 192, 0.7)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                    }
                }
            }
        });
    }
}
else if (document.location.pathname === '/admin/logs') {
    $(document).ready(function () {
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(endDate.getDate() - 7);

        $('#startDateFilter').val(startDate.toISOString().split('T')[0]);
        $('#endDateFilter').val(endDate.toISOString().split('T')[0]);

        loadLogs();

        loadApplications();

        $('#applyFiltersBtn').click(function () {
            loadLogs();
        });

        $('#refreshLogsBtn').click(function () {
            loadLogs();
        });

        let autoRefreshInterval;

        function setupAutoRefresh() {
            if ($('#autoRefreshToggle').is(':checked')) {
                autoRefreshInterval = setInterval(loadLogs, 30000); // 30 seconds
            } else {
                clearInterval(autoRefreshInterval);
            }
        }

        $('#autoRefreshToggle').change(setupAutoRefresh);
        setupAutoRefresh();
    });

    function loadLogs(page = 1) {
        $('#logsTableBody').html(`
                <tr>
                    <td colspan="6" class="text-center py-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </td>
                </tr>
            `);

        const filters = {
            level: $('#logLevelFilter').val(),
            application: $('#applicationFilter').val(),
            startDate: $('#startDateFilter').val(),
            endDate: $('#endDateFilter').val(),
            search: $('#searchFilter').val(),
            page: page
        };

        $.ajax({
            url: '/admin/logs',
            type: 'GET',
            data: filters,
            success: function (response) {
                renderLogsTable(response.logs);
                updatePagination(response.totalPages, page, filters);
            },
            error: function (xhr) {
                $('#logsTableBody').html(`
                        <tr>
                            <td colspan="6" class="text-center py-5 text-danger">
                                Error loading logs: ${xhr.responseJSON?.message || 'Unknown error'}
                            </td>
                        </tr>
                    `);
            }
        });
    }

    function renderLogsTable(logs) {
        let html = '';

        if (logs.length === 0) {
            html = `
                    <tr>
                        <td colspan="6" class="text-center py-5">
                            No log entries found matching your criteria
                        </td>
                    </tr>
                `;
        } else {
            logs.forEach(function (log) {
                const levelClass = `log-level-${log.level.toLowerCase()}`;
                const timestamp = new Date(log.timestamp).toLocaleString();

                html += `
                        <tr style="cursor: pointer;" onclick="showLogDetails('${log.id}')">
                            <td>${timestamp}</td>
                            <td><span class="${levelClass}">${log.level}</span></td>
                            <td>${log.application || '-'}</td>
                            <td>${log.logger}</td>
                            <td class="text-truncate" style="max-width: 300px;" title="${log.message}">
                                ${log.message}
                            </td>
                            <td>${log.username || 'System'}</td>
                        </tr>
                    `;
            });
        }

        $('#logsTableBody').html(html);
    }

    function loadApplications() {
        $.ajax({
            url: '/admin/logs/applications',
            type: 'GET',
            success: function (applications) {
                let options = '<option value="">All Applications</option>';
                applications.forEach(function (app) {
                    options += `<option value="${app}">${app}</option>`;
                });
                $('#applicationFilter').html(options);
            },
            error: function () {
                console.error('Failed to load applications');
            }
        });
    }

    function updatePagination(totalPages, currentPage, filters) {
        let html = '';

        if (currentPage > 1) {
            html += `
                    <li class="page-item">
                        <a class="page-link" href="#" onclick="loadLogsWithFilters(${currentPage - 1})">Previous</a>
                    </li>
                `;
        } else {
            html += `
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1">Previous</a>
                    </li>
                `;
        }

        const maxVisiblePages = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        if (startPage > 1) {
            html += `<li class="page-item"><a class="page-link" href="#" onclick="loadLogsWithFilters(1)">1</a></li>`;
            if (startPage > 2) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            if (i === currentPage) {
                html += `<li class="page-item active"><a class="page-link" href="#">${i}</a></li>`;
            } else {
                html += `<li class="page-item"><a class="page-link" href="#" onclick="loadLogsWithFilters(${i})">${i}</a></li>`;
            }
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            html += `<li class="page-item"><a class="page-link" href="#" onclick="loadLogsWithFilters(${totalPages})">${totalPages}</a></li>`;
        }

        if (currentPage < totalPages) {
            html += `
                    <li class="page-item">
                        <a class="page-link" href="#" onclick="loadLogsWithFilters(${currentPage + 1})">Next</a>
                    </li>
                `;
        } else {
            html += `
                    <li class="page-item disabled">
                        <a class="page-link" href="#">Next</a>
                    </li>
                `;
        }

        $('#pagination').html(html);
    }

    function loadLogsWithFilters(page) {
        loadLogs(page);
    }

    function showLogDetails(logId) {
        $('#logDetailsContent').html(`
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `);

        $('#logDetailsModal').modal('show');

        $.ajax({
            url: `/admin/logs/${logId}`,
            type: 'GET',
            success: function (log) {
                const timestamp = new Date(log.timestamp).toLocaleString();
                const levelClass = `log-level-${log.level.toLowerCase()}`;

                let detailsHtml = `
                        <div class="mb-3">
                            <h6>Timestamp</h6>
                            <p>${timestamp}</p>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <h6>Level</h6>
                                <p><span class="${levelClass}">${log.level}</span></p>
                            </div>
                            <div class="col-md-4">
                                <h6>Application</h6>
                                <p>${log.application || '-'}</p>
                            </div>
                            <div class="col-md-4">
                                <h6>User</h6>
                                <p>${log.username || 'System'}</p>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <h6>Logger</h6>
                            <p>${log.logger}</p>
                        </div>
                        
                        <div class="mb-3">
                            <h6>Message</h6>
                            <div class="alert alert-light" style="white-space: pre-wrap;">${log.message}</div>
                        </div>
                    `;

                if (log.stackTrace) {
                    detailsHtml += `
                            <div class="mb-3">
                                <h6>Stack Trace</h6>
                                <div class="alert alert-danger" style="white-space: pre-wrap; font-family: monospace;">${log.stackTrace}</div>
                            </div>
                        `;
                }

                if (log.additionalData) {
                    detailsHtml += `
                            <div class="mb-3">
                                <h6>Additional Data</h6>
                                <pre class="bg-light p-3 rounded">${JSON.stringify(log.additionalData, null, 2)}</pre>
                            </div>
                        `;
                }

                $('#logDetailsContent').html(detailsHtml);
            },
            error: function (xhr) {
                $('#logDetailsContent').html(`
                        <div class="alert alert-danger">
                            Error loading log details: ${xhr.responseJSON?.message || 'Unknown error'}
                        </div>
                    `);
            }
        });
    }
}



