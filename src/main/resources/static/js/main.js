async function sendRequest(method, url, data, headers, options) {
    try {


        let finalUrl = new URL(url ? url : "", document.location.origin);
        let getHeaders;
        if (headers !== undefined || true) {
            getHeaders = {
                'Content-Type': 'application/json',
                // "X-CSRF-TOKEN": token,
                ...headers,
            }
        }
        let getInit = {}
        if (method === 'POST') {
            finalUrl = url;
            getInit = {
                method: method,
                body: data,
                headers: getHeaders,
                ...options,
            };
        } else if (method === 'GET') {
            data = JSON.parse(data);
            Object.keys(data).forEach(key => {
                finalUrl.searchParams.set(key, data[key]);
            });
            getInit = {
                method: method,
                headers: getHeaders,
                ...options,
            };
        }
        const response = await fetch(finalUrl, getInit);

        if (response.ok) {
            const data = await response.json();
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                return data;
            }
        }

    } catch (error) {
        console.error("Error sending " + method + " request: ", error);
        throw error;
    }
}

function collectDataForm(form) {
    const data = new FormData(form);
    const objectData = Object.fromEntries(changeSortValue(data).entries());

    return JSON.stringify(objectData);
}

function changeSortValue(formData) {
    const sortKey = "sort"
    if (!formData.has(sortKey)) {
        return formData;
    }
    const sort = formData.get('sort');

    if (sort.includes("asc")) {
        formData.set(sortKey, 'asc');
    } else if (sort.includes("desc")) {
        formData.set(sortKey, 'desc');
    }
    return formData;
}

function capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function formatTime(seconds) {
    if (seconds === null || isNaN(seconds)) return '--:--:--';

    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = Math.floor(seconds % 60);
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

function isISODateString(string) {
    return /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?(Z|[+-]\d{2}:\d{2})?$/.test(string);
}

function hasHtml(string) {
    return /<[a-z][\s\S]*>/i.test(string);
}

function formatDate(date) {
    if (!date) return 'None';
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');

    return `${day}.${month}.${year} ${hours}:${minutes}:${seconds}`;
}

function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}