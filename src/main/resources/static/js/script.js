$(document).ready(function () {
    function addData(chart, oldChart) {
        let newData = oldChart.data

        chart.data.labels = newData.labels

        newData.datasets.forEach((datasets) => {
            chart.data.datasets.push(datasets);
        })
        chart.update();
    }

    function removeData(chart) {
        chart.data.labels.pop();
        let l = chart.data.datasets.length
        for (let i = 0; i < l; i++) {
            chart.data.datasets.pop();
        }
        chart.update();
    }

    async function drawChart(chart, url) {
        var now = moment().format("DD-MM-YYYYTHH%3Amm%3Ass");
        var start = moment().subtract(5, 'years').format("DD-MM-YYYYTHH%3Amm%3Ass");
        const baseUrl = document.location.origin + '/api/online?server=x5&interval=1800&period-start=' + start + '&period-end=' + now + '';
        if (url == '') {
            url = baseUrl;
        }
        try {
            let response = await fetch(url);

            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }

            const json = await response.json();
            setChartTitle()

            removeData(chart)
            addData(chart, json)

        } catch (error) {
            console.error(error.message);
        }
    }

    $(".nav-link").on('click', function () {
        this.classList.toggle('active-status');
    });

    function formOnline(chart) {
        let url = new URL("/api/online", document.location.origin)

        let elements = document.forms["onlineChart"].children;
        for (let i = 0; i < elements.length; i++) {
            if (elements.item(i).name == "submit") continue;

            url.searchParams.append(elements.item(i).name, elements.item(i).value);
        }
        drawChart(chart, url);
    }

    function setChartTitle() {
        let elements = document.forms["onlineChart"].children;
        for (let i = 0; i < elements.length; i++) {
            if (elements.item(i).name == "submit") continue;

            if (isISODateString(elements.item(i).value)) {

                const date = new Date(elements.item(i).value);

                document.getElementById(elements.item(i).name).textContent = date.toLocaleString('en-GB');
            } else if (elements.item(i).name == "interval") {
                document.getElementById(elements.item(i).name).textContent = $("#" + elements.item(i).id + " :selected").text();
            } else {
                document.getElementById(elements.item(i).name).textContent = elements.item(i).value;
            }
        }
    }

    // Roulette
    function playRoulette(straightVal, numberVal, coffer) {
        spinWheel();

        const formatNumber = (num) => {
            return new Intl.NumberFormat('ru-RU').format(num);
        };

        const numberLimit = 36;
        const coefficient = 35;
        const random = Math.floor(Math.random() * (numberLimit + 1));

        let rolledCount = parseInt(document.getElementById("rolledCount").textContent) || 0;
        let rolledCountWon = parseInt(document.getElementById("rolledCountWon").textContent) || 0;
        let resultValue, nowCoffer;

        rolledCount += 1;

        const logEntry = document.createElement("div");
        logEntry.className = "log-entry";

        if (numberVal == random) {
            rolledCountWon += 1;
            resultValue = straightVal * coefficient;
            nowCoffer = Number(coffer) + Number(resultValue);

            logEntry.innerHTML = `Play #${rolledCount}: <span class="win">Victory! You have won! ${formatNumber(resultValue)} Adena</span> (roll: ${random})`;
        } else {
            resultValue = straightVal;
            nowCoffer = Number(coffer) - Number(resultValue);

            logEntry.innerHTML = `Play #${rolledCount}: <span class="lose">Loss! You lost! ${formatNumber(resultValue)} Adena</span> (roll: ${random})`;
        }

        document.getElementById("rouletteLog").prepend(logEntry);

        if (rolledCountWon > 0) {
            const winRate = Math.floor((rolledCountWon / rolledCount) * 1000) / 10;
            document.getElementById("rolledCoefficientWon").textContent = winRate + ' %';
        }

        document.getElementById("rolledCount").textContent = rolledCount;
        document.getElementById("rolledCountWon").textContent = rolledCountWon;
        document.getElementById("playerCofferNum").textContent = formatNumber(nowCoffer);
        document.getElementById("rolledNumber").textContent = random;

        document.getElementById("rouletteLog").scrollTop = 0;

        return nowCoffer;
    }

    $(".roulette-form").on("submit", function (e) {
        rouletteValidForm()
    })

    function rouletteValidForm() {
        const straightInput = document.querySelector('input[name="straight"]');
        const numberInput = document.querySelector('input[name="number"]');
        const cofferElement = document.getElementById("playerCofferNum");

        const straightVal = parseInt(straightInput.value);
        const numberVal = parseInt(numberInput.value);
        const coffer = parseInt(cofferElement.textContent.replace(/\s/g, ''));

        if (isNaN(straightVal) || isNaN(numberVal)) {
            alert("Please enter correct values!");
            return false;
        }

        if (straightVal < 5000 || straightVal > 500000000) {
            alert("The bet must be between 5,000 and 500,000,000!");
            return false;
        }

        if (numberVal < 0 || numberVal > 36) {
            alert("The number must be between 0 and 36!");
            return false;
        }

        if (straightVal > coffer) {
            alert("You do not have enough funds for this bet!");
            return false;
        }

        document.getElementById("straightVal").textContent = new Intl.NumberFormat('ru-RU').format(straightVal);
        document.getElementById("numberVal").textContent = numberVal;

        const newCoffer = playRoulette(straightVal, numberVal, coffer);

        cofferElement.textContent = new Intl.NumberFormat('ru-RU').format(newCoffer);

        return false;
    }

    function spinWheel() {
        const wheel = document.getElementById('rouletteWheel');
        wheel.style.transition = 'none';
        wheel.style.transform = 'rotate(0deg)';

        setTimeout(() => {
            const spins = 3;
            const degrees = 360 * spins + Math.floor(Math.random() * 360);
            wheel.style.transition = 'transform 5s cubic-bezier(0.1, 0.7, 0.1, 1)';
            wheel.style.transform = `rotate(${degrees}deg)`;

            const numbers = [];
            for (let i = 0; i <= 36; i++) {
                numbers.push(i < 10 ? `0${i}` : i.toString());
            }
            let counter = 0;
            const interval = setInterval(() => {
                wheel.textContent = document.getElementById('rolledNumber').textContent;
                counter++;
                if (counter > 30) clearInterval(interval);
            }, 100);
        }, 10);
    }

    // Language switcher
    $('#languageSwitcher').click(function () {
        toggleLanguage();
    });

    let currentLanguage = 'gb';
    const translations = {ru: "RU", gb: "EN"}

    function toggleLanguage() {
        currentLanguage = currentLanguage === 'ru' ? 'gb' : 'ru';
        updateLanguage();
    }

    function updateLanguage() {
        const lang = translations[currentLanguage];

        $('#languageSwitcher').html(`
                <i class="flag-icon flag-icon-${currentLanguage === 'gb' ? 'gb' : 'ru'}"></i>
            `);
    }

    // Custom select
    const originalSelect = $('#originalSelect');
    const customMultiselect = $('#customMultiselect');
    const selectedItemsContainer = $('#selectedItemsContainer');
    const dropdownList = $('#dropdownList');
    const optionsContainer = $('#optionsContainer');
    const searchInput = $('#searchOptions');
    const submitBtn = $('#submitBtn');

    originalSelect.find('option').each(function () {
        const option = $(this);
        optionsContainer.append(`
                    <div class="option-item" data-value="${option.val()}">
                        ${option.text()}
                    </div>
                `);
    });

    customMultiselect.on('click', function (e) {
        e.stopPropagation();
        dropdownList.toggleClass('show');

        if (dropdownList.hasClass('show')) {
            searchInput.focus();
        }
    });

    optionsContainer.on('click', '.option-item', function () {
        const option = $(this);
        const value = option.data('value');
        const text = option.text();

        option.toggleClass('selected');

        if (option.hasClass('selected')) {
            originalSelect.find(`option[value="${value}"]`).prop('selected', true);

            selectedItemsContainer.find('.placeholder').remove();
            selectedItemsContainer.append(`
                        <div class="selected-item" data-value="${value}">
                            ${text}
                            <span class="remove-item">&times;</span>
                        </div>
                    `);
        } else {
            originalSelect.find(`option[value="${value}"]`).prop('selected', false);

            selectedItemsContainer.find(`.selected-item[data-value="${value}"]`).remove();

            if (selectedItemsContainer.children().length === 0) {
                selectedItemsContainer.append('<div class="placeholder">Select elements...</div>');
            }
        }
    });

    selectedItemsContainer.on('click', '.remove-item', function (e) {
        e.stopPropagation();
        const item = $(this).parent();
        const value = item.data('value');

        originalSelect.find(`option[value="${value}"]`).prop('selected', false);

        item.remove();

        optionsContainer.find(`.option-item[data-value="${value}"]`).removeClass('selected');

        if (selectedItemsContainer.children().length === 0) {
            selectedItemsContainer.append('<div class="placeholder">Select elements...</div>');
        }
    });

    searchInput.on('input', function () {
        const searchTerm = $(this).val().toLowerCase();

        optionsContainer.find('.option-item').each(function () {
            const option = $(this);
            const text = option.text().toLowerCase();

            if (text.includes(searchTerm)) {
                option.show();
            } else {
                option.hide();
            }
        });
    });

    $(document).on('click', function () {
        dropdownList.removeClass('show');
    });

    dropdownList.on('click', function (e) {
        e.stopPropagation();
    });

    submitBtn.on('click', function () {
        const selectedValues = originalSelect.val() || [];
        alert('Elements selected: ' + selectedValues.join(', '));
    });

    originalSelect.find('option:selected').each(function () {
        const option = $(this);
        const value = option.val();
        const text = option.text();

        selectedItemsContainer.find('.placeholder').remove();
        selectedItemsContainer.append(`
                    <div class="selected-item" data-value="${value}">
                        ${text}
                        <span class="remove-item">&times;</span>
                    </div>
                `);

        optionsContainer.find(`.option-item[data-value="${value}"]`).addClass('selected');
    });
    // Events form
    let debounceTimer;
    const content = document.querySelector(".main-content");
    let formConstant = {};
    if (content !== null) {
        formConstant = getFormConst(content);
        if (formConstant !== null) {
            addFormListeners();
            initContentEvents();
        }
    }


    function getFormConst(content) {
        if (Object.keys(content.dataset).length > 0) {
            let result = {};
            result["name"] = content.dataset.name;
            result["formType"] = content.dataset.formType;
            result["form"] = document.getElementById(result.name + 'Form');
            result["url"] = new URL(`/api/${result.name}`, document.location.origin);
            return result;
        } else {
            return null;
        }
    }

    function handleFieldChange() {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(async () => {
            await initContentEvents();
        }, 0);
    }

    function showSkeleton() {
        if (formConstant.name === 'events' || formConstant.name === 'fortress-history' || formConstant.name === 'fortress' || formConstant.name === 'articles') {
            const skeleton = document.querySelector(".skeleton-body");
            let colCount = document.querySelectorAll("#" + formConstant.name + "Table thead th").length;
            let skeletonFragment = document.createDocumentFragment();
            for (let i = 0; i < 50; i++) {
                let row = document.createElement('tr');
                row.classList.add("skeleton-row")
                for (let j = 0; j < colCount; j++) {
                    let col = document.createElement('td');
                    let div = document.createElement('div');
                    div.classList.add('skeleton');
                    col.appendChild(div)
                    row.appendChild(col);
                }
                skeletonFragment.appendChild(row);
            }
            skeleton.replaceChildren(skeletonFragment)
        }

        if (formConstant.name === 'bosses') {
            const skeleton = document.querySelector(".skeleton-body");
            const count = skeleton.children.length === 0 ? 50 : skeleton.children.length;
            let skeletonFragment = document.createDocumentFragment();
            for (let i = 0; i < count; i++) {
                let element = document.createElement('div');
                element.classList.add("skeleton-row")
                element.classList.add("boss-card");
                let skeletonBox = document.createElement('div');
                skeletonBox.classList.add('skeleton');
                element.appendChild(skeletonBox);
                skeletonFragment.appendChild(element);
            }
            skeleton.replaceChildren(skeletonFragment)
        }
    }

    function loadContent(result) {
        if (formConstant.name === 'events' || formConstant.name === 'articles') {
            const tbody = document.querySelector("#" + formConstant.name + "Table tbody");
            const resultFrag = document.createDocumentFragment();
            let i = 0;
            for (const obj of result) {
                i++;
                let row = document.createElement("tr");
                for (const key in obj) {
                    let col = document.createElement("td");
                    let value = obj[key];

                    if (formConstant.name === 'articles') {
                        if (key === 'link') {
                            continue;
                        }
                        if (key === 'title') {
                            let link = document.createElement("a");
                            link.href = obj['link'];
                            link.textContent = value;
                            col.appendChild(link);
                            row.appendChild(col);
                            continue;
                        }
                    }

                    if (key === "id") {
                        value = i;
                    }
                    if (isISODateString(value)) {
                        value = formatDate(obj[key]);

                    }
                    if (key === "updatedDate" || key === "createdDate") {
                        continue;
                    }
                    if (hasHtml(value)) {
                        col.innerHTML = value;
                        row.appendChild(col);
                        continue;
                    }
                    col.textContent = value;
                    row.appendChild(col);
                }
                resultFrag.appendChild(row);
            }
            tbody.replaceChildren(resultFrag);
        } else if (formConstant.name === 'bosses') {
            const bosses = initializeBossGrid(result);

            updateTimers(bosses);
            setInterval(() => updateTimers(bosses), 60000);
        } else if (formConstant.name === 'fortress-history' || formConstant.name === 'fortress') {
            const tbody = document.querySelector("#" + formConstant.name + "Table tbody");
            const resultFrag = document.createDocumentFragment();
            let i = 0;
            for (const object of result) {
                i++;
                let row = document.createElement("tr");
                for (const secondObject in object) {
                    let col = document.createElement("td");
                    let value = object[secondObject];
                    if (secondObject === "id") {
                        value = i;
                    } else if (secondObject === "coffer") {
                        value = value + " Aden.";
                    } else if (secondObject === "holdTime") {
                        value = value + " Hours.";
                    } else if (secondObject === "createdDate") {
                        continue;
                    } else if (isISODateString(value)) {
                        value = formatDate(value);
                    } else if (typeof value === "object") {
                        let secondTable = document.createElement("table");
                        let secondTbody = document.createElement("tbody");
                        let secondThead = document.createElement("thead");
                        secondTable.classList.add("table-hover");
                        secondTable.classList.add("table-custom");
                        secondTable.classList.add("table");
                        secondTable.classList.add("table-nested");

                        if (secondObject === "skills") {
                            let secondValueRow = document.createElement("tr");
                            secondValueRow.classList.add("d-inline-block");
                            secondValueRow.classList.add("w-25");
                            for (const thirdObject in value) {

                                let thirdValue = value[thirdObject];
                                let tooltipContainer = document.createElement("td");
                                tooltipContainer.classList.add("tooltip-container");

                                let thirdImage = document.createElement("img");
                                thirdImage.src = '/content/fortress-skills/image/' + thirdValue.id;
                                thirdImage.alt = thirdValue.name;

                                let tooltipText = document.createElement("span");
                                tooltipText.classList.add("tooltip-text");
                                let spanName = document.createElement("span");
                                spanName.textContent = thirdValue.name + ": ";
                                spanName.classList.add("fw-bold");
                                let spanEffect = document.createElement("span");
                                spanEffect.textContent = thirdValue.effect;

                                tooltipText.appendChild(spanName);
                                tooltipText.appendChild(spanEffect);

                                tooltipContainer.appendChild(thirdImage);
                                tooltipContainer.appendChild(tooltipText);

                                secondValueRow.appendChild(tooltipContainer);
                            }

                            col.appendChild(secondValueRow);
                            row.appendChild(col);

                            continue;
                        }

                        // secondTbody.classList.add("skeleton-body");
                        let secondValueRow = document.createElement("tr");
                        let secondHeadRow = document.createElement("tr");
                        for (const thirdObject in value) {
                            if (thirdObject === "id" || thirdObject === "image" || thirdObject === "updatedDate" || thirdObject === "createdDate" || thirdObject === "server") {
                                continue;
                            }
                            let secondHeadCol = document.createElement("th");
                            secondHeadCol.textContent = capitalize(thirdObject);
                            secondHeadRow.appendChild(secondHeadCol);
                        }
                        for (const thirdObject in value) {
                            let secondValue = value[thirdObject];
                            if (thirdObject === "id" || thirdObject === "image" || thirdObject === "updatedDate" || thirdObject === "createdDate" || thirdObject === "server") {
                                continue;
                            }
                            if (thirdObject === "name") {
                                let clanName = document.createElement("td");
                                let spanName = document.createElement("span");
                                let img = document.createElement("img");
                                img.src = '/content/clans/image/' + value.id;
                                img.alt = "";
                                spanName.textContent = secondValue;
                                clanName.appendChild(img);
                                clanName.appendChild(spanName);
                                secondValueRow.appendChild(clanName);
                                continue;
                            }
                            if (isISODateString(secondValue)) {
                                secondValue = formatDate(secondValue);
                            }
                            let secondCol = document.createElement("td");
                            secondCol.textContent = secondValue;
                            secondValueRow.appendChild(secondCol);
                        }
                        secondThead.appendChild(secondHeadRow);
                        secondTable.appendChild(secondHeadRow);

                        secondTbody.appendChild(secondValueRow);
                        secondTable.appendChild(secondTbody);

                        col.appendChild(secondTable);
                        row.appendChild(col);
                        continue;
                    }
                    col.textContent = value;
                    row.appendChild(col);
                }
                resultFrag.appendChild(row);
            }
            tbody.replaceChildren(resultFrag);
        }
    }

    function initializeBossGrid(bosses) {
        const bossGrid = document.getElementById('bossGrid');
        bossGrid.innerHTML = '';

        bosses.forEach(boss => {
            const card = document.createElement('div');
            card.className = 'boss-card';

            card.innerHTML = `
                    <div class="boss-header">
                        <span class="boss-type">${boss.type || 'None'}</span>
                        <span class="boss-name">${boss.name}</span>
                        <span class="boss-server">${boss.server || 'Global'}</span>
                    </div>
                    <div class="timer-container">
                        <svg class="timer-circle" viewBox="0 0 42 42">
                            <circle class="timer-background" cx="21" cy="21" r="15.9"></circle>
                            <circle class="timer-progress" cx="21" cy="21" r="15.9" stroke-dasharray="100 100" stroke-dashoffset="100"></circle>
                        </svg>
                        <div class="timer-text">--:--:--<br><span class="timer-status">None</span></div>
                    </div>
                    <div class="boss-info">
                        <div>Respawn start: <span id="start-${boss.id}">${formatDate(boss.respawnStart)}</span></div>
                        <div>Respawn end: <span id="end-${boss.id}">${formatDate(boss.respawnEnd)}</span></div>
                        <div>Killed: ${boss.countKilling || 0}</div>
                    </div>
                    <div class="boss-killer" id="killer-${boss.id}">
                        ${boss.lastKiller ? `The last one killed: ${boss.lastKiller}` : ''}
                        ${boss.lastKillersClan ? ` (${boss.lastKillersClan})` : ''}
                    </div>
                `;

            bossGrid.appendChild(card);
            boss.element = card;
            boss.timerElements = {
                progress: card.querySelector('.timer-progress'),
                text: card.querySelector('.timer-text'),
                status: card.querySelector('.timer-status'),
                start: card.querySelector(`#start-${boss.id}`),
                end: card.querySelector(`#end-${boss.id}`),
                killer: card.querySelector(`#killer-${boss.id}`)
            };
        });

        return bosses;
    }

    function updateTimers(bosses) {
        const now = new Date();
        bosses.forEach(boss => {
            if (!boss.respawnStart || !boss.respawnEnd) {
                updateBossElement(boss, null, null, 'unknown');
                return;
            }

            const respawnStart = new Date(boss.respawnStart);
            const respawnEnd = new Date(boss.respawnEnd);
            const nowTime = now.getTime();
            if (nowTime < respawnStart.getTime() && (respawnStart.getTime() - nowTime) / 1000 < 7200) {
                const total = (respawnStart.getTime() - new Date(boss.date).getTime()) / 1000;
                const remaining = (respawnStart.getTime() - nowTime) / 1000;
                updateBossElement(boss, remaining, total, 'warning');
            } else if (nowTime < respawnStart.getTime()) {
                const total = (respawnStart.getTime() - new Date(boss.date).getTime()) / 1000;
                const remaining = (respawnStart.getTime() - nowTime) / 1000;
                updateBossElement(boss, remaining, total, 'waiting');
            } else if (nowTime < respawnEnd.getTime()) {
                const total = (respawnEnd.getTime() - respawnStart.getTime()) / 1000;
                const remaining = (respawnEnd.getTime() - nowTime) / 1000;
                updateBossElement(boss, remaining, total, 'active');
            } else {
                updateBossElement(boss, 0, 1, 'expired');
            }
        });
    }

    function updateBossElement(boss, remainingSeconds, totalSeconds, status) {
        const elements = boss.timerElements;

        if (remainingSeconds !== null && totalSeconds !== null && totalSeconds > 0) {
            const progressPercent = (remainingSeconds / totalSeconds) * 100;
            elements.progress.style.strokeDashoffset = progressPercent;
            elements.text.innerHTML = `${formatTime(remainingSeconds)}<br><span class="timer-status">${getStatusText(status)}</span>`;
        } else {
            elements.progress.style.strokeDashoffset = '100';
            elements.text.innerHTML = '--:--:--<br><span class="timer-status">None</span>';
        }

        switch (status) {
            case 'active':
                elements.progress.style.stroke = '#4CAF50';
                elements.status.className = 'active-status';
                break;
            case 'warning':
                elements.progress.style.stroke = '#FF5722'
                elements.status.className = 'warning-status';
                break;
            case 'expired':
                elements.progress.style.stroke = '#CCFF00';
                elements.status.className = '';
                break;
            default:
                elements.progress.style.stroke = '#FFC107';
                elements.status.className = '';
        }

        elements.start.textContent = formatDate(boss.respawnStart);
        elements.end.textContent = formatDate(boss.respawnEnd);

        elements.killer.innerHTML = `
                ${boss.lastKiller ? `The last one killed: ${boss.lastKiller}` : ''}
                ${boss.lastKillersClan ? ` (${boss.lastKillersClan})` : ''}
            `;
    }

    function getStatusText(status) {
        switch (status) {
            case 'active':
                return 'Respawn!';
            case 'waiting':
                return 'Waiting';
            case 'expired':
                return 'Alive!';
            case 'warning':
                return 'Soon!';
            default:
                return 'None';
        }
    }

    async function initContentEvents() {
        showSkeleton();
        const data = collectDataForm(formConstant.form);
        const result = await sendRequest('POST', formConstant.url, data);
        loadContent(result)
    }

    function addFormListeners() {
        formConstant.form.querySelectorAll('input, select').forEach(field => {
            field.addEventListener('input', handleFieldChange);
            field.addEventListener('change', handleFieldChange);
        });

        formConstant.form.addEventListener('submit', function (e) {
            e.preventDefault();
            initContentEvents();
        });
    }

});


