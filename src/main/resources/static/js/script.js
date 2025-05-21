function addData(chart, oldChart) {
    let newData = oldChart.data

    chart.data.labels = newData.labels

    newData.datasets.forEach((datasets) =>{
        chart.data.datasets.push(datasets);
    })
    chart.update();
}

function removeData(chart) {
    chart.data.labels.pop();
    let l = chart.data.datasets.length
    for (let i=0;i<l;i++){
        chart.data.datasets.pop();
    }
    chart.update();
}

 async function drawChart(chart,url) {
     var now = moment().format("DD-MM-YYYYTHH%3Amm%3Ass");
     var start = moment().subtract(5, 'years').format("DD-MM-YYYYTHH%3Amm%3Ass");
     const baseUrl = document.location.origin + '/api/online?server=x5&interval=1800&period-start='+start+'&period-end='+now+'';
     if (url == ''){
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
         addData(chart,json)

     } catch (error) {
         console.error(error.message);
     }
}
$(document).ready(function() {
    $('.nav-link').on('click', function(){
        this.classList.toggle('active');
    })
})
function formOnline(chart) {
    let url = new URL("/api/online", document.location.origin)

    let elements = document.forms["onlineChart"].children;
    for(let i = 0; i < elements.length ; i++){
        if(elements.item(i).name == "submit") continue;

        url.searchParams.append(elements.item(i).name,elements.item(i).value);
    }
    drawChart(chart,url);
}
function setChartTitle(){
    let elements = document.forms["onlineChart"].children;
    for(let i = 0; i < elements.length ; i++){
        if(elements.item(i).name == "submit") continue;

        if(isDateValid(elements.item(i).value)){

            const date = new Date(elements.item(i).value);

            document.getElementById(elements.item(i).name).textContent = date.toLocaleString('en-GB');
        }else if(elements.item(i).name == "interval"){
            document.getElementById(elements.item(i).name).textContent = $("#"+elements.item(i).id+" :selected").text();
        }else{
            document.getElementById(elements.item(i).name).textContent = elements.item(i).value;
        }
    }
}
function isDateValid(dateStr) {
    let res = false;
    if (!isNaN(dateStr)){
        res = false;
    }else{
        const date = new Date(dateStr);
        res = date instanceof Date && !isNaN(date)
    }
    return res;
}
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
        document.getElementById("rolledCoefficientWon").textContent = winRate+' %';
    }

    document.getElementById("rolledCount").textContent = rolledCount;
    document.getElementById("rolledCountWon").textContent = rolledCountWon;
    document.getElementById("playerCofferNum").textContent = formatNumber(nowCoffer);
    document.getElementById("rolledNumber").textContent = random;

    document.getElementById("rouletteLog").scrollTop = 0;

    return nowCoffer;
}

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
function formatNumber(num){
    let res = num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
    return res;
}

async function formEvents(name) {
    let url = new URL("/api/"+name, document.location.origin)

    const form = document.forms[name];
    const formData = new FormData(form);

    for (let [name, value] of formData) {
        console.log(`${name}: ${value}`);
        url.searchParams.append(name, value);
    }
    console.log(url);

    let response = await fetch(url);

    if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
    }

    const json = await response.json();

    let oldData = document.getElementsByClassName("div-table-row");
    while (oldData.length > 1){
        oldData[1].remove()
    }

    for (const obj of json) {
        console.log(obj);
        let row = $('<div class="div-table-row">')
        for (const key in obj){
            if(key == "updatedDate" || key == "createdDate"){
                continue;
            }
            row.append(
                $('<div class="div-table-col">').text(obj[key])
            );
        }
        row.appendTo('#'+name+'Table');
    }
}

$(document).ready(function() {

    $('#languageSwitcher').click(function() {
        toggleLanguage();
    });

    let currentLanguage = 'gb';
    const translations = {ru:"RU",gb:"EN"}

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

    const originalSelect = $('#originalSelect');
    const customMultiselect = $('#customMultiselect');
    const selectedItemsContainer = $('#selectedItemsContainer');
    const dropdownList = $('#dropdownList');
    const optionsContainer = $('#optionsContainer');
    const searchInput = $('#searchOptions');
    const submitBtn = $('#submitBtn');

    originalSelect.find('option').each(function() {
        const option = $(this);
        optionsContainer.append(`
                    <div class="option-item" data-value="${option.val()}">
                        ${option.text()}
                    </div>
                `);
    });

    customMultiselect.on('click', function(e) {
        e.stopPropagation();
        dropdownList.toggleClass('show');

        if (dropdownList.hasClass('show')) {
            searchInput.focus();
        }
    });

    optionsContainer.on('click', '.option-item', function() {
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

    selectedItemsContainer.on('click', '.remove-item', function(e) {
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

    searchInput.on('input', function() {
        const searchTerm = $(this).val().toLowerCase();

        optionsContainer.find('.option-item').each(function() {
            const option = $(this);
            const text = option.text().toLowerCase();

            if (text.includes(searchTerm)) {
                option.show();
            } else {
                option.hide();
            }
        });
    });

    $(document).on('click', function() {
        dropdownList.removeClass('show');
    });

    dropdownList.on('click', function(e) {
        e.stopPropagation();
    });

    submitBtn.on('click', function() {
        const selectedValues = originalSelect.val() || [];
        alert('Elements selected: ' + selectedValues.join(', '));
    });

    originalSelect.find('option:selected').each(function() {
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
});


