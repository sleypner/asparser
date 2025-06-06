$(document).ready(function () {

    const container = document.querySelector('.auth-container');
    const pageType = container.id

    switch (pageType) {
        case 'registerPage':
            initRegister();
            break;
        case 'loginPage':
            initLogin();
            break;
        case 'verificationPage':
            initEmailVerify();
            break;
    }

    function initRegister() {
        // password complexity check
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordError = document.getElementById('passwordError');
        const passwordStrength = document.getElementById('passwordStrength');
        const registerButton = document.getElementById('registerButton');
        const captchaError = document.getElementById('captchaError')

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
        });

        confirmPassword.addEventListener('input', function (e) {
            if (password.value !== confirmPassword.value) {
                passwordError.style.display = 'block';
                registerButton.disabled = true;
                registerButton.style.opacity = '0.7';
            } else {
                passwordError.style.display = 'none';
                registerButton.disabled = false;
                registerButton.style.opacity = '1';
            }
        });

        function collectDataFormRegister(form) {
            const data = new FormData(form);
            let captchaResponse = '';
            let userData = {};
            data.forEach((value, key) => {
                if (key === 'g-recaptcha-response') {
                    captchaResponse = value;
                } else {
                    userData[key] = value;
                }
            });
            const requestData = {
                "captcha": captchaResponse,
                "user": userData
            };
            return JSON.stringify(requestData);
        }

        async function submitForm(data) {
            sendRequest('POST', "/auth/signup-process", data, undefined, {redirect: 'manual'})
                .then(response => {
                    if (response.ok) {
                        Object.keys(response).forEach(key => {
                            const errorElement = document.getElementById(key + "Error");
                            errorElement.style.display = 'block'
                            errorElement.textContent = response[key];
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                })
        }

        document.getElementById('registrationForm').addEventListener('submit', async (e) => {
            if (grecaptcha.getResponse()) {
                e.preventDefault();
                const data = collectDataFormRegister(document.getElementById("registrationForm"));
                await submitForm(data);

                captchaError.style.display = 'none';

            } else {
                captchaError.style.display = 'block';
                captchaError.textContent = 'Please confirm that you are not a robot!'
            }
        });
        // check username
        document.getElementById('username').addEventListener('blur', function () {
            const username = this.value;
            if (username.length >= 3) {
                const data = JSON.stringify({username: username});
                sendRequest('POST', "/auth/check-username", data)
                    .then(data => {
                        if (data === true) {
                            document.getElementById('usernameError').textContent = 'Username is already taken';
                            document.getElementById('usernameError').style.display = 'block';
                        } else {
                            document.getElementById('usernameError').style.display = 'none';
                        }
                    });
            } else {
                document.getElementById('usernameError').textContent = 'Username most by bigger when 4 characters';
                document.getElementById('usernameError').style.display = 'block';
            }
        });
        document.getElementById('email').addEventListener('blur', function () {
            const email = this.value;

            const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (isValidEmail(email)) {
                const data = JSON.stringify({email: email});
                sendRequest('POST', "/auth/check-email", data)
                    .then(data => {
                        if (data === true) {
                            document.getElementById('emailError').textContent = 'This email is already taken';
                            document.getElementById('emailError').style.display = 'block';
                        } else {
                            document.getElementById('emailError').style.display = 'none';
                        }
                    });
            } else {
                document.getElementById('emailError').textContent = 'Please enter correct email address';
                document.getElementById('emailError').style.display = 'block';
            }
        });
    }

    function initLogin() {

    }

    function initEmailVerify() {
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
                const url = '/auth/resend-verification';
                const data = JSON.stringify({email: document.querySelector('input[name="email"]').value});
                sendRequest('GET', url, data)
                    .then(response => {
                        successMessage.style.display = 'block';
                        setTimeout(() => {
                            successMessage.style.display = 'none';
                        }, 10000);

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
                    })
                    .catch(console.error);
            });
        }
        const firstDigit = document.getElementsByName('digit1')[0];
        if (firstDigit) {
            firstDigit.focus();
        }
    }
});

// Autofocus on first field when load
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
