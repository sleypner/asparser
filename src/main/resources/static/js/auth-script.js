$(document).ready(function () {
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const passwordStrength = document.getElementById('passwordStrength');
    const registerButton = document.getElementById('registerButton');

// password complexity check
    password.addEventListener('input', function () {
        const value = this.value;
        let strength = 0;

        if (value.length >= 8) {
            document.getElementById('req-length').style.color = 'var(--primary-color)';
            strength++;
        } else {
            document.getElementById('req-length').style.color = '';
        }

        if (/[A-Z]/.test(value)) {
            document.getElementById('req-upper').style.color = 'var(--primary-color)';
            strength++;
        } else {
            document.getElementById('req-upper').style.color = '';
        }

        if (/[0-9]/.test(value)) {
            document.getElementById('req-number').style.color = 'var(--primary-color)';
            strength++;
        } else {
            document.getElementById('req-number').style.color = '';
        }

        if (/[!@#$%^&*]/.test(value)) {
            document.getElementById('req-special').style.color = 'var(--primary-color)';
            strength++;
        } else {
            document.getElementById('req-special').style.color = '';
        }

        // complexity visual
        const width = (strength / 4) * 100;
        passwordStrength.style.width = width + '%';

        if (strength === 0) {
            passwordStrength.style.backgroundColor = 'transparent';
        } else if (strength <= 2) {
            passwordStrength.style.backgroundColor = '#dc3545';
        } else if (strength === 3) {
            passwordStrength.style.backgroundColor = '#ffc107';
        } else {
            passwordStrength.style.backgroundColor = '#28a745';
        }

        checkPasswords();
    });

// check password matches
    confirmPassword.addEventListener('input', checkPasswords);

    function checkPasswords() {
        if (password.value !== confirmPassword.value) {
            passwordError.style.display = 'block';
            registerButton.disabled = true;
            registerButton.style.opacity = '0.7';
        } else {
            passwordError.style.display = 'none';
            registerButton.disabled = false;
            registerButton.style.opacity = '1';
        }
    }

// validate form
    document.getElementById('registrationForm').addEventListener('submit', function (e) {
        if (!grecaptcha.getResponse()) {
            e.preventDefault();
            document.getElementById('captchaError').style.display = 'block';
        }
    });

// check username (AJAX example)
    document.getElementById('username').addEventListener('blur', function () {
        const username = this.value;
        if (username.length >= 3) {
            // Here can add an AJAX request to check the name
            fetch('/check-username?username=' + username)
                .then(response => response.json())
                .then(data => {
                    if (data.exists) {
                        document.getElementById('usernameError').textContent = 'Username is already taken';
                        document.getElementById('usernameError').style.display = 'block';
                    } else {
                        document.getElementById('usernameError').style.display = 'none';
                    }
                });
        }
    });

    async function formRegister() {
        let url = new URL("/registration-process", document.location.origin)

        const form = document.forms[register];
        const formData = new FormData(form);

        for (let [name, value] of formData) {
            console.log(`${name}: ${value}`);
            url.searchParams.append(name, value);
        }
    }

// resend timer
    const resendBtn = document.getElementById('resendBtn');
    const timerElement = document.getElementById('timer');
    const countdownElement = document.getElementById('countdown');
    const successMessage = document.getElementById('successMessage');
    const resendTextMessage = document.getElementById('resendTextMessage');

    let timeLeft = 60;
    if (resendBtn) {
        resendTextMessage.style.display = 'none';

        const timer = setInterval(function () {
            timeLeft--;
            timerElement.textContent = timeLeft;

            if (timeLeft <= 0) {
                clearInterval(timer);
                countdownElement.style.display = 'none';
                resendTextMessage.style.display = 'inline-block';
            }
        }, 1000);

        resendBtn.addEventListener('click', function () {

            // AJAX resend timer

            var token = $('#_csrf').attr('content');
            var header = $('#_csrf_header').attr('content');

            fetch('/resend-verification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': token
                },
                body: JSON.stringify({
                    email: document.querySelector('input[name="email"]').value
                })
            })
                .then(response => {
                    if (response.ok) {
                        successMessage.style.display = 'block';
                        setTimeout(() => {
                            successMessage.style.display = 'none';
                        }, 3000);

                        timeLeft = 60;
                        countdownElement.style.display = 'block';
                        resendTextMessage.style.display = 'none';

                        const newTimer = setInterval(function () {
                            timeLeft--;
                            timerElement.textContent = timeLeft;

                            if (timeLeft <= 0) {
                                clearInterval(newTimer);
                                countdownElement.style.display = 'none';
                                resendTextMessage.style.display = 'inline-block';
                            }
                        }, 1000);
                    }
                });
        });
    }

    // Autofocus on first field when load
    document.getElementsByName('digit1')[0].focus();
});
// Autonavigate between inputs
function moveToNext(current, nextFieldName) {
    if (current.value.length === 1) {
        document.getElementsByName(nextFieldName)[0].focus();
    }
}

// Autosend form when loading
function submitIfFilled(field) {
    if (field.value.length === 1) {
        document.getElementById('verificationForm').submit();
    }
}
