document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById('registerForm');

    form.addEventListener('submit', async function (e) {
        e.preventDefault();

        const payload = {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value,
            phone: document.getElementById('phone').value.trim(),
            address: document.getElementById('address').value.trim(),
            gender: parseInt(document.getElementById('gender').value || '0', 10),
            dateOfBirth: document.getElementById('dateOfBirth').value || null
        };

        if (document.getElementById('password').value !== document.getElementById('confirmPassword').value) {
            Swal.fire({
                icon: 'warning',
                title: 'Cảnh báo',
                text: 'Mật khẩu không khớp'
            });
            return;
        }

        try {
            const csrfToken = document.getElementById('_csrf').value;
            const res = await fetch('/registerAdmin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) throw new Error(await res.text());

            Swal.fire({
                icon: 'success',
                title: 'Thành công',
                text: 'Đăng ký thành công',
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                window.location.href = '/login';
            });

        } catch (err) {
            Swal.fire({
                icon: 'error',
                title: 'Thất bại',
                text: 'Đăng ký thất bại: ' + err.message
            });
        }
    });
});
