const nodeVersion = process.versions.node;
const electronVersion = process.versions.electron;
const chromiumVersion = process.versions.chrome;

const nodeVersionElement = document.getElementById('node-version');
if (nodeVersionElement) {
    nodeVersionElement.textContent = nodeVersion;
}

const chromeVersionElement = document.getElementById('chrome-version');
if (chromeVersionElement) {
    chromeVersionElement.textContent = chromiumVersion;
}

const electronVersionElement = document.getElementById('electron-version');
if (electronVersionElement) {
    electronVersionElement.textContent = electronVersion;
}