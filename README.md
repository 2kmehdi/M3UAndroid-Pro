<div align="center">

# M3UAndroid Pro: Universal IPTV & DLNA Cast Suite

**A next-generation IPTV player, universal DLNA casting engine, and web stream sniffer for Android phones, tablets, and TV.**

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/github/license/oxyroid/M3UAndroid)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

</div>

---

## 🚀 Key Pro Enhancements

### 1. 📡 Universal DLNA & UPnP Casting Engine
- **Full TV Brand Compatibility**: Seamless discovery and casting across Samsung (Tizen), LG (webOS & NetCast), Sony BRAVIA, Roku TV, Amazon Fire TV, Xiaomi Mi TV, Philips, and generic UPnP MediaRenderers.
- **Android Wi-Fi MulticastLock**: Fixes missing devices by acquiring a hardware multicast lock, ensuring Android OS power-saving modes do not drop SSDP UDP discovery packets.
- **Multi-Target SSDP Broadcast**: Concurrently searches `ssdp:all`, `MediaRenderer:1`, `MediaRenderer:2`, and `AVTransport:1`.
- **Dynamic ProtocolInfo & MIME Negotiation**: Automatically formats compliant DLNA flags and MIME headers (`application/vnd.apple.mpegurl`, `application/x-mpegURL`, `video/mp4`, `video/x-matroska`, `video/mp2t`, `application/dash+xml`), resolving `SetAVTransportURI` Error 714.
- **Resilient Fallback Recovery**: Automatically retries with minimal/empty metadata for TVs that reject complex DIDL-Lite descriptors.
- **Full In-App Cast Remote Control**: Real-time Play, Pause, Resume, Stop, Scrub/Seek (HH:mm:ss), Volume adjustments, and Track Position polling.

### 2. ⚡ Quick Link Caster
- Directly cast any live or video link without having to save it into a playlist first.
- Paste links from clipboard with instant format auto-detection (HLS, DASH, MP4, MKV, TS, FLV).
- One-tap target screen selection and cast launch.

### 3. 🌐 In-App Web Browser with Video Stream Sniffer
- Integrated Chromium browser with real-time network request interception.
- Automatically sniffs live HLS manifests (`.m3u8`), DASH (`.mpd`), and direct video files (`.mp4`, `.mkv`, `.ts`, `.flv`).
- Floating "Streams Sniffed" radar pill with an expandable drawer:
  - **Cast to TV**: Send the sniffed web stream directly to your Smart TV via DLNA.
  - **Play in App**: Instant playback inside the hardware-accelerated Media3/ExoPlayer engine.
  - **Copy Link**: One-tap copy with extracted authentication headers.

### 4. 🎨 Elevated Pro "Codevibe" UI/UX
- Material 3 Glassmorphism with backdrop blur (`haze` & `backdrop`).
- Sleek floating bottom navigation bar with automatic width resolution.
- Live radar pulse animations during device scanning.
- Brand badges and hardware identification tags for all connected screens.

---

## 🛠️ Project Architecture

```
m3u_android/
├── app/
│   ├── smartphone/       # Smartphone & tablet Compose UI, navigation & Cast Hub
│   │   └── ui/business/cast/ # Web Stream Sniffer & Quick Cast Hub
│   └── tv/               # Android TV leanback DPad interface
├── business/             # Core business features & view models
│   └── channel/          # Enhanced DlnaController with MulticastLock & protocolInfo
├── data/                 # Database (Room), repositories, parsers & Ktor server
├── core/                 # Shared foundation, design tokens & utilities
├── i18n/                 # Multilingual localization resources
└── parser/               # M3U, Xtream & EPG playlist parsers
```

---

## 📦 How to Build the Project

### Prerequisites
- JDK 17 or JDK 21
- Android SDK (API 34 / 35)

### Build Debug / Release APK
```bash
# Debug APK
./gradlew :app:smartphone:assembleDebug

# Release APK
./gradlew :app:smartphone:assembleRelease
```
The compiled APK will be generated at:
`app/smartphone/build/outputs/apk/release/smartphone-release.apk`

---

## 🐙 How to Publish Your Fork to GitHub

1. Create a new repository on your GitHub account (e.g. `M3UAndroid-Pro-DLNA`).
2. Add your new GitHub remote:
   ```bash
   git remote add myfork https://github.com/<your-username>/M3UAndroid-Pro-DLNA.git
   ```
3. Commit and push your changes:
   ```bash
   git add .
   git commit -m "feat: universal DLNA casting, web stream sniffer browser & quick caster"
   git push -u myfork master
   ```

---

## 📄 License
This project is licensed under the [GNU General Public License v3.0](LICENSE).
