# How to Deploy Loewe Remote to iPhone from a Windows PC

This guide explains how to install the generated **`loewe-remote-kmp.ipa`** file onto a physical iPhone using a Windows computer (zero Mac required).

---

## Step 1: Download the `.ipa` from GitHub Actions

1. Go to your repository on GitHub: **[github.com/phmandl/loewe_tv_remote](https://github.com/phmandl/loewe_tv_remote)**.
2. Click the **Actions** tab at the top.
3. Click on the latest **Build KMP iOS App** workflow run (with the green checkmark ✅).
4. Scroll down to the **Artifacts** section at the bottom of the summary page.
5. Click **`loewe-remote-kmp-ipa`** to download the ZIP file.
6. Extract the ZIP to get your **`loewe-remote-kmp.ipa`** file.

---

## Step 2: Install Sideloadly on Windows

[Sideloadly](https://sideloadly.io/) is the easiest and most reliable free tool to sideload IPAs onto iOS devices from Windows.

1. Download and install **[Sideloadly for Windows (64-bit)](https://sideloadly.io/)**.
2. **Prerequisite:** Make sure you have Apple drivers on Windows. If prompted by Sideloadly, install the standalone **iTunes** and **iCloud** (non-Microsoft Store versions linked directly inside Sideloadly or from Apple).

---

## Step 3: Sideload the App onto Your iPhone

1. **Connect your iPhone** to your Windows PC with a USB cable.
2. If your iPhone asks *"Trust this computer?"*, tap **Trust** and enter your passcode.
3. Open **Sideloadly**:
   * Under **Device**, select your connected iPhone.
   * Under **Apple ID**, enter your normal Apple ID email (used to sign the app for personal use for 7 days).
   * **Drag & drop** the `loewe-remote-kmp.ipa` file into the large IPA box on the left.
4. Click **Start**.
5. Enter your Apple ID password when prompted (handled securely directly with Apple servers).
6. Wait ~30 seconds until Sideloadly says **`Done.`**

---

## Step 4: First-Time iOS Trust & Developer Mode

Before opening the app on your iPhone for the first time:

### 1. Trust the Developer Certificate
1. On your iPhone, open **Settings** &rarr; **General** &rarr; **VPN & Device Management**.
2. Under *Developer App*, tap your **Apple ID email**.
3. Tap **Trust "[Your Apple ID]"** &rarr; Confirm **Trust**.

### 2. Enable Developer Mode (iOS 16, 17, 18+)
1. Go to **Settings** &rarr; **Privacy & Security**.
2. Scroll to the very bottom and tap **Developer Mode**.
3. Toggle it **ON** and tap **Restart**.
4. After rebooting, unlock your iPhone and tap **Turn On** &rarr; Enter your passcode.

---

## 🎉 Done!
Open **Loewe Remote** from your home screen:
1. Tap **⚙ Settings** in the top right.
2. Enter your TV's **IP address** and **MAC address**.
3. Tap **Save & Connect** to start controlling your TV!
