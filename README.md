# Loewe TV Remote — Kotlin Multiplatform (Compose)

A modern, high-performance mobile remote control application for **Loewe Smart TVs** (specifically **Loewe bild 5 OLED**, Chassis SL420, and all Loewe OS platforms). Built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, sharing 100% of the UI, state management, and network logic across **iOS** and **Android**.

---

## 📺 Overview & Key Features

* **Dark OLED Remote UI:** Custom tactile dark-mode interface designed for high-contrast OLED displays with haptic/visual feedback.
* **Complete Loewe Control Set:**
  * Top Quick Controls: Power, Info, Menu, and Mute.
  * 5-Way Directional Navigation Pad (Up, Down, Left, Right, OK) + Back and Home.
  * Volume & Program Rockers with direct EPG (Electronic Program Guide) trigger.
  * Interactive Red, Green, Yellow, and Blue color function keys.
  * Collapsible 0–9 numeric keypad with direct Radio mode switcher.
* **Dual RC Alphabet Support:** Full support for both standard remote operations (`alphabet="l2700"`) and dedicated DVR/HDR playback controls (`alphabet="l2700-hdr"`: Play, Pause, Stop, Fast-Forward, Rewind, Record).
* **Smart Power & Wake-on-LAN (WoL):**
  * Automatic fallback: If the TV is in deep standby (SOAP server offline), tapping the Power button automatically broadcasts a 102-byte UDP Magic Packet (`Port 9`) to wake the TV, waits for the network stack, and completes the handshake.
  * Dedicated manual `⚡ WoL` broadcast button in the header.
* **Live Debug Console:** Built-in, 3-line scrollable monospaced terminal displaying real-time SOAP handshakes, key dispatches, network responses, and error diagnostics.
* **Zero-Mac iOS Deployment:** Automated GitHub Actions CI workflow compiles an unsigned `.ipa` on `macos-latest` ready for sideloading via **Sideloadly** or **AltStore**.

## 📡 Compatibility

Engineered and verified for **Loewe bild 5 (Chassis SL420)** running Loewe OS.

Also compatible with all Loewe TV chassis supporting the Loewe Remote TV Tablet SOAP API:
* **SL3xx / SL4xx / SL5xx (Loewe OS):** Loewe bild 1, bild 2, bild 3, bild 4, bild 5, bild 7, bild 9, Reference, Individual, Connect, and Art.
* **SL2xx:** Chassis SL220 / SL212 (Software version `PV1.10+`).
* **SL1xx:** Chassis SL150 / SL121 (Basic SOAP controls).

## 🛠️ Tech Stack & Architecture

* **UI Framework:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) 1.7.0 (JetBrains)
* **Language & Runtime:** Kotlin 2.0.21 on Java 21 (JBR 21 LTS)
* **Networking & HTTP:** [Ktor Client](https://ktor.io/) 3.0.1 (OkHttp on Android, Darwin on iOS)
* **Async & State:** Kotlinx Coroutines & Flow 1.9.0 + AndroidX Lifecycle ViewModel
* **Local Persistence:** [Multiplatform-Settings](https://github.com/russhwolf/multiplatform-settings) 1.2.0 (NSUserDefaults on iOS, SharedPreferences on Android)
* **Native Sockets:** BSD POSIX UDP sockets (`platform.posix.*`) on iOS, `java.net.DatagramSocket` on Android for Wake-on-LAN.

## 🚀 Getting Started

### 1. Requirements
* [Android Studio](https://developer.android.com/studio) (Ladybug / Meerkat or later) with **Kotlin Multiplatform** plugin.
* JDK: **JBR 21** or **Adoptium OpenJDK 21** configured as the Gradle JDK.

### 2. Run on Android
1. Open the project in Android Studio.
2. Select the **`composeApp`** run configuration in the top toolbar.
3. Connect your Android phone via USB (with USB Debugging enabled) or Wi-Fi.
4. Click **Run** (<kbd>Shift</kbd> + <kbd>F10</kbd>).

### 3. Build for iOS (GitHub Actions)
1. Push this repository to your GitHub account:
   ```bash
   git remote add origin https://github.com/<your-username>/loewe_tv_remote.git
   git push -u origin main
   ```
2. Navigate to the **Actions** tab in your GitHub repository.
3. Once the **Build Kotlin Multiplatform iOS App (.ipa)** workflow completes, download the **`loewe-remote-kmp-ipa`** artifact.
4. Sideload the `.ipa` onto your iPhone using [Sideloadly](https://sideloadly.io/) or [AltStore](https://altstore.io/).


## ⚙️ TV Configuration

1. In your Loewe TV menu, ensure mobile access and network standby are enabled:
   * **Settings** &rarr; **Network** &rarr; **Network configuration** &rarr; **Mobile access** / **Quick Start Mode** &rarr; **On**.
2. Open the Loewe Remote app on your phone.
3. Tap **⚙ Settings** in the top right:
   * **TV IP Address:** (e.g. `192.168.1.50`)
   * **TV Port:** `905` (Default)
   * **TV MAC Address:** (e.g. `00:09:82:XX:XX:XX` — found under TV Network settings for Wake-on-LAN).
4. Tap **Save & Connect**.


## 🙏 Acknowledgements & References

* **[hass-loewetv-remoteapi](https://github.com/gadgetbazza/hass-loewetv-remoteapi):** Great appreciation to **gadgetbazza** and contributors for the Home Assistant Loewe integration, providing the initial foundation for SOAP endpoints and network behavior.
* **Loewe Technologies GmbH:** Official specification reference from the *"LOEWE TV remote API"* documentation (Revision 1.0.47), defining the complete `l2700` and `l2700-hdr` keycode matrices, `RequestAccess` handshake, and network standby protocols.
* **Google Gemini & DeepMind Antigravity:** Developed and architected with the assistance of **Gemini** and **Google DeepMind Antigravity**, enabling the complete Kotlin Multiplatform implementation, dual-OS native network socket layers, and automated zero-Mac CI/CD deployment pipeline.


## 📄 License
This project is open-source under the [MIT License](LICENSE).
