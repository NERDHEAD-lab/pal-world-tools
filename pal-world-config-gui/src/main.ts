import {app, BrowserWindow} from "electron";
import path from "node:path"; // ES import

let window: BrowserWindow | null = null;

app.on("ready", () => {
    window = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        },    });
    // window.loadFile("build/index.html")
    window.loadFile(path.join(__dirname, "index.html"))
        .then(() => {
            window?.webContents.openDevTools();
        });
});