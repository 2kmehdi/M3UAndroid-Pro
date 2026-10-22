<div align="center">

# ⚡ M3UAndroid Pro: Universal IPTV & DLNA Cast Suite 📺🚀

### *The Ultimate Open-Source IPTV Player, Universal Smart TV DLNA Caster & In-App Web Video Sniffer for Android, Tablets & Android TV.*

<p align="center">
  <a href="https://github.com/2kmehdi/M3UAndroid-Pro/releases/latest">
    <img src="https://img.shields.io/github/v/release/2kmehdi/M3UAndroid-Pro?color=7C4DFF&label=Latest%20Release&logo=github&style=for-the-badge" alt="Latest Release" />
  </a>
<p align="center">
  <a href="https://github.com/2kmehdi/M3UAndroid-Pro/releases/latest">
    <img src="https://img.shields.io/github/v/release/2kmehdi/M3UAndroid-Pro?color=7C4DFF&label=Latest%20Release&logo=github&style=for-the-badge" alt="Latest Release" />
  </a>
  <a href="https://github.com/2kmehdi/M3UAndroid-Pro/releases/tag/v2.1.0">
    <img src="https://img.shields.io/badge/Release-v2.1.0-00E676?style=for-the-badge&logo=android&logoColor=white" alt="Release v2.1.0" />
  </a>
  <a href="https://github.com/2kmehdi/M3UAndroid-Pro/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/2kmehdi/M3UAndroid-Pro/build-release-apk.yml?label=Build%20Status&logo=githubactions&style=for-the-badge" alt="Build Status" />
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-Apache_2.0-blue?style=for-the-badge" alt="License" />
  </a>
</p>

<p align="center">
  <a href="https://developer.android.com">
    <img src="https://img.shields.io/badge/Platform-Android%208.0%2B%20%7C%20TV%20%7C%20Tablet-3DDC84?style=flat-square&logo=android&logoColor=white" />
  </a>
  <a href="https://kotlinlang.org">
    <img src="https://img.shields.io/badge/Kotlin-2.4-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  </a>
  <a href="https://developer.android.com/jetpack/compose">
    <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" />
  </a>
  <a href="https://developer.android.com/media/media3">
    <img src="https://img.shields.io/badge/Engine-AndroidX%20Media3%20ExoPlayer-FF6F00?style=flat-square&logo=google&logoColor=white" />
  </a>
</p>

---

### 📥 Direct APK Downloads (v2.1.0 Pro)

| Target Platform | Package Name | Direct Download |
| :--- | :--- | :--- |
| 📱 **Smartphone & Tablet** | `smartphone-debug.apk` | [⬇️ **Download Smartphone APK v2.1.0**](https://github.com/2kmehdi/M3UAndroid-Pro/releases/download/v2.1.0/smartphone-debug.apk) |
| 📺 **Android TV / Fire TV** | `tv-debug.apk` | [⬇️ **Download Android TV APK v2.1.0**](https://github.com/2kmehdi/M3UAndroid-Pro/releases/download/v2.1.0/tv-debug.apk) |

👉 **[View All Releases & Mirrors on GitHub](https://github.com/2kmehdi/M3UAndroid-Pro/releases)**

---

</div>

## 🌟 What's New in V2.1?

- 🍏 **Apple iOS Glass Style UI**: Redesigned home interface featuring frosted glassmorphism, translucent iOS Control Center tiles, glowing avatar orbs, and smooth segmented capsules.
- ▶️ **Native YouTube Stream Resolver**: Dual-engine Innertube + Piped extractor resolves YouTube URLs and live feeds into direct MP4/HLS streams for ExoPlayer and DLNA Smart TVs.
- 🛡️ **Xtream Codes & M3U Super Resilience**: Fixed URL double-slash bug, sanitized credentials, and empowered the provider playback client with SSL TrustAll and extended timeouts.
- 🎛️ **Live Profile & Category Filtering**: Real-time filtering by profiles (`👑 All Access`, `⚽ Live Sports`, `🍿 Cinema VOD`, `📺 TV Series`, `🧸 Family & Kids`) and categories (`⚡ All`, `📡 Live TV`, `🍿 Movies`, `🎞️ TV Shows`).
- 📲 **Universal System Share Receiver**: Send links from Chrome, YouTube, TikTok, Telegram, WhatsApp, or VLC to M3UAndroid Pro for instant 1-tap DLNA casting.
- 🛡️ **Xtream Codes & M3U Super Resilience**: Integrated 120s extended timeouts, smart URL normalization, and IPTV Smarters / ExoPlayer User-Agent spoofing to bypass 403 Forbidden errors.
- ⚡ **Segmented Content Categories**: Instant top-level filtering for **Live TV**, **Movies**, and **TV Shows / Series**.
- 🌐 **Web Sniffer Pro**: Enhanced detection of live streams (`.m3u8`, `.mpd`, `.mp4`, `.ts`, `.flv`) with direct casting to Smart TVs.

## 🌟 Why M3UAndroid Pro?

Standard IPTV players lock you into basic local playback and fail when you attempt to cast live HLS or DASH streams to modern Smart TVs. **M3UAndroid Pro** completely re-engineers playback and casting into a unified, high-octane streaming powerhouse:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            M3UAndroid Pro Suite                             │
├──────────────────────┬───────────────────────────────┬──────────────────────┤
│  ⚡ Quick Link Cast   │  🌐 In-App Stream Sniffer     │  📡 Universal DLNA   │
│  Paste any video url │  Detects .m3u8, .mpd, mp4     │  Samsung, LG, Sony,  │
│  Instant Smart TV cast│  Floating radar sniffer badge │  Roku, Fire TV & more│
└──────────────────────┴───────────────────────────────┴──────────────────────┘
```

---

## 🔥 Pro Key Features

### 📡 1. Universal Smart TV DLNA & UPnP Casting Engine
Cast any live channel, movie, or internet video to almost any connected screen with zero configuration:
- **Hardware MulticastLock Integration**: Automatically acquires Android `WifiManager.MulticastLock` during discovery so aggressive battery savers never drop SSDP UDP discovery packets.
- **Multi-Target SSDP Discovery**: Concurrently scans for `MediaRenderer:1`, `MediaRenderer:2`, `AVTransport:1`, and `upnp:rootdevice` — instantly discovering Samsung Tizen, LG webOS, Roku, and Sony Bravia TVs.
- **Smart ProtocolInfo & MIME Negotiation**: Dynamically builds RFC-compliant DIDL-Lite metadata for HLS (`.m3u8`), DASH (`.mpd`), MP4, MKV, TS, and FLV with automated minimal fallback to avoid TV **Error 714**.
- **Interactive Remote Control Card**: Control playback directly from your phone — Play, Pause, Resume, Stop, Scrub/Seek time (`HH:mm:ss`), and adjust volume.

### ⚡ 2. Direct Link Quick Caster
- No need to save a playlist or create a channel just to test a stream.
- Paste any direct link from your clipboard with automatic stream-type detection.
- Cast instantly to your living room TV or play locally in the app with hardware decoding.

### 🌐 3. Built-In Web Browser with Real-Time Video Sniffer
- Browse any streaming website, sports portal, or video host directly inside the app.
- **Automated Network Interception**: Sniffs background HTTP/HTTPS requests for media streams (`.m3u8`, `.mpd`, `.mp4`, `.mkv`, `.ts`, `.flv`, `.webm`).
- **Interactive Radar Badge**: Displays live stream counter with a slide-up drawer to:
  - 📡 **Cast to Smart TV** via DLNA with custom user-agent preservation.
  - ▶️ **Play in App** with ExoPlayer / Media3 hardware acceleration.
  - 📋 **Copy Stream Link** with single-tap clipboard sync.

### 🎨 4. Ultra-Modern Material 3 "Codevibe" UI
- Fluid Glassmorphism with real-time backdrop blur (`haze` & `backdrop`).
- Adaptive navigation tailored for smartphones, foldables, tablets, and TV screens.
- Animated radar scan pulse indicators and custom hardware TV brand badges.

---

## 📺 Supported TV Brands & DLNA Renderers

| Brand / Ecosystem | Protocol | Compatibility | Features Supported |
| :--- | :--- | :---: | :--- |
| **Samsung Smart TV** | Tizen UPnP / DLNA | ✅ **100%** | Full remote, HLS live cast, volume, seek |
| **LG Smart TV** | webOS / NetCast | ✅ **100%** | Multi-target SSDP, DIDL fallback, scrub |
| **Sony BRAVIA** | Android TV / UPnP | ✅ **100%** | Hardware decoding, track position sync |
| **Roku TV & Stick** | Roku DIAL / UPnP | ✅ **100%** | Direct link casting, MP4/HLS streaming |
| **Amazon Fire TV** | FireOS MediaRenderer | ✅ **100%** | Live stream casting, volume control |
| **Xiaomi / Mi Box** | Android TV DLNA | ✅ **100%** | Fast discovery, high bitrate playback |
| **Google Cast / TV** | UPnP Renderers | ✅ **100%** | Adaptive bitrate casting |
| **Generic DLNA / PC**| Kodi / VLC / Windows | ✅ **100%** | Full DIDL-Lite metadata negotiation |

---

## 🛠️ Architecture & Tech Stack

Built strictly following modern Android development best practices and Clean Architecture:

- **Language**: 100% Kotlin with Coroutines & Flow
- **UI Framework**: Jetpack Compose with Material 3 Adaptive Navigation Suite
- **Media Engine**: AndroidX Media3 / ExoPlayer with native codec acceleration
- **Networking & Discovery**: UPnP / SSDP engine via MMUPnP with hardware MulticastLock
- **Dependency Injection**: Dagger Hilt
- **Database**: Room Database with Paging 3
- **Modular Architecture**:
  - `:app:smartphone` – Phone & tablet Compose UI, Web Sniffer, Cast Hub
  - `:app:tv` – Leanback D-Pad optimized Android TV interface
  - `:business:channel` – DLNA Controller, playback engine, brand detection
  - `:data` – Playlist repositories, Room DB, Ktor local proxy
  - `:i18n` – Comprehensive multilingual internationalization

---

## 📱 How to Install

### On Android Phone or Tablet:
1. Download [**smartphone-debug.apk**](https://github.com/2kmehdi/M3UAndroid-Pro/releases/download/v1.0.7/smartphone-debug.apk).
2. Tap the downloaded file in your notification bar or Downloads folder.
3. If prompted, allow *"Install from unknown sources"*.
4. Launch **M3UAndroid Pro** and enjoy!

### On Android TV / Google TV / Fire TV:
1. Install the **Downloader** app from the Google Play Store or Amazon Appstore.
2. In Downloader, enter the direct download link:
   ```
   https://github.com/2kmehdi/M3UAndroid-Pro/releases/download/v1.0.7/tv-debug.apk
   ```
3. Click **Install** once the download completes.

---

## 💻 Building from Source

```bash
# Clone the repository
git clone https://github.com/2kmehdi/M3UAndroid-Pro.git
cd M3UAndroid-Pro

# Build smartphone APK
./gradlew :app:smartphone:assembleDebug

# Build TV APK
./gradlew :app:tv:assembleDebug
```

---

## 🔍 SEO & Discovery Keywords

`iptv`, `m3u`, `m3u8`, `m3u-player`, `iptv-player`, `android-iptv`, `smart-tv-cast`, `dlna-player`, `dlna-caster`, `cast-to-tv`, `samsung-tv-cast`, `lg-webos-cast`, `roku-cast`, `fire-tv-cast`, `sony-bravia`, `stream-sniffer`, `video-sniffer`, `hls-player`, `dash-player`, `exoplayer`, `media3`, `jetpack-compose`, `material3`, `open-source-iptv`, `free-iptv`, `live-stream-caster`, `xtream-codes`, `epg-player`, `android-tv`, `google-tv`, `firestick`, `tizen`, `upnp-av-transport`, `multicast-lock`

---

## 📜 License & Credits

- Based on the upstream project [M3UAndroid](https://github.com/oxyroid/M3UAndroid).
- Distributed under the **Apache License 2.0**. See [LICENSE](LICENSE) for details.
- Fork maintained and enhanced by [**@2kmehdi**](https://github.com/2kmehdi).
