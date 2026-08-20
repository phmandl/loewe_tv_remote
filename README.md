# Loewe Remote (Compose Multiplatform)

A modern **Kotlin Multiplatform (Compose Multiplatform)** Remote Control application designed for **Loewe bild 5 OLED TVs (Chassis SL420, Loewe OS)**.

100% shared Kotlin code for UI and business logic targeting **iOS (iPhone/iPad)** and **Android**.

---

## Features

- **SOAP-over-HTTP Protocol Engine:**
  - Automated `RequestAccess` handshake and session management (`<ClientId>`).
  - `InjectRCKey` command ingestion with auto-reconnect / re-handshake fallback if TV is power-cycled.
  - Complete Loewe KeyCode mapping (Navigation, Power, Volume, Program, Color Keys, Numpad, Media).
- **Wake-on-LAN (WoL):**
  - Sends 102-byte UDP Magic Packet (`255.255.255.255:9`) to wake the TV from Standby.
  - Native POSIX BSD sockets on iOS via Kotlin/Native (`platform.posix.*`) and DatagramSockets on Android.
- **Dark OLED Aesthetic:**
  - Designed with luxury graphite/matte black finishes, glowing accents, ergonomic D-Pad, and vertical rocker switches.
  - Collapsible numeric keypad for clean viewing on smaller iPhone screens.
- **Local Persistence:**
  - TV IP address, MAC address, port, and client ID persisted using Multiplatform Settings.
- **Automated CI/CD:**
  - GitHub Actions workflow that compiles the unsigned `.ipa` on `macos-latest` for sideloading with Sideloadly / AltStore.

---

## Project Structure

```
├── .github/workflows/
│   └── build-kmp-ios.yml         # CI/CD: Automated unsigned .ipa build
├── composeApp/                   # Shared Compose Multiplatform module
│   ├── src/
│   │   ├── commonMain/           # 100% Shared UI, ViewModel, Ktor SOAP & Models
│   │   ├── androidMain/          # Android Activity & DatagramSocket WoL
│   │   └── iosMain/              # iOS MainViewController & POSIX WoL Sockets
├── iosApp/                       # Native iOS Xcode wrapper project (SwiftUI)
└── gradle/                       # Gradle wrapper & Version Catalog (libs.versions.toml)
```

---

## Loewe TV Communication Parameters

- **Default Port:** `905` (HTTP/TCP)
- **Path:** `/loewe_tablet_0001`
- **SOAP Header:** `Content-Type: application/soap+xml; charset=utf-8`

### Key Mapping Reference (Exact `hass-loewetv-remoteapi` spec)

| Key / Function | KeyCode (Int) | Description |
| --- | --- | --- |
| POWER | 12 | Standby / Power Toggle (`ON_OFF`) |
| POWER ON / OFF | 22 / 25 | Discrete Power On / Off |
| MUTE | 13 | Mute Toggle |
| VOLUME UP / DOWN | 21 / 20 | Volume Up / Down |
| PROGRAM UP / DOWN | 24 / 23 | Program / Channel Up / Down |
| UP / DOWN / LEFT / RIGHT | 32 / 33 / 17 / 16 | Directional D-Pad |
| OK / SELECT | 38 | Confirm / Center OK |
| MENU | 11 | TV Settings Menu |
| HOME / MEDIA | 49 | Home / Media Menu |
| BACK / END | 65 / 63 | Back / Exit |
| INFO | 79 | Info Banner |
| EPG | 15 | Electronic Program Guide |
| TELETEXT | 60 | Teletext (`ttx`) |
| COLOR KEYS (Red / Green / Yellow / Blue) | 27 / 26 / 43 / 40 | Interactive HbbTV Keys |
| NUMERIC 0 – 9 | 0 – 9 | Direct Channel & Digit Input |
| RADIO / PIP / ASPECT | 53 / 10 / 90 | Direct Function Access |

---

## Getting Started

### 1. Android Studio Setup
1. Open Android Studio.
2. Select **Open** and choose the `loewe_tv_remote` root folder.
3. Gradle will synchronize automatically.
4. Select `composeApp` run configuration to deploy to an Android device or emulator.

### 2. Building iOS App (.ipa) via GitHub Actions
1. Push this repository to GitHub (`main` branch) or run via **Actions -> Build KMP iOS App -> Run workflow**.
2. Download the `loewe-remote-kmp-ipa` artifact from the workflow run.
3. Sideload `loewe-remote-kmp.ipa` to your iPhone using **Sideloadly** or **AltStore**.

### 3. TV Configuration & First Run
1. Ensure your iPhone / Android phone is on the **same local Wi-Fi network** as the Loewe bild 5 TV.
2. Ensure **Mobile App Access / Fast Boot / WOL** is enabled in the TV's Network settings:
   - TV Menu -> Settings -> Network -> Network Settings -> Allow Mobile Access / Quick Start Mode.
3. Launch the app, tap **⚙ Settings**, and enter:
   - **TV IP Address** (e.g. `192.168.1.50`)
   - **TV MAC Address** (e.g. `00:11:22:33:44:55`) for Wake-on-LAN.
4. Tap **Save & Connect**.
