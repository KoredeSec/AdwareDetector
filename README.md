# 🛡️ Android Adware Detector 

An Android security app designed to detect adware using machine learning — all on-device. It scans installed apps and uses a lightweight **LightGBM model** (converted to **ONNX**) for privacy-preserving detection via **ONNX Runtime Java**.

---

## 📱 Project Overview

This app aims to help users identify potentially malicious adware apps on their Android device. It is still in early development but already includes key foundational features.

### ✅ Current Features

- 📦 Scans all installed apps on the device
- 🧠 Loads and runs an ONNX-based LightGBM model for inference
- ⚙️ Coroutine-based scanning for smooth UI performance
- 📈 Shows total number of scanned apps and basic detection result

### 🚧 Under Construction

- 📊 Displaying individual app risk scores
- 🚩 Filtering apps with scores > 0.5 (high-risk)
- 🖼️ Showing app icons and names in a results list
- 📋 RecyclerView UI for scan results
- 🔔 Local notifications for flagged apps
- 🗃️ Local scan history and logging

---

## 🧠 Machine Learning Model Info

The model used is a **binary classifier** trained to differentiate between clean apps and those showing adware behavior patterns.

### Sample Features Used for Training

- Number of permissions requested
- Use of dangerous permissions
- Presence of native code (JNI)
- Installation source (Play Store vs. others)
- Use of trackers (Google Ad ID, etc.)
- Network traffic allowance (cleartext, background sync)
- SDK and target SDK versions
- Background services
- And more...

The model was trained offline using Python (LightGBM) and converted to ONNX for Android integration.

---

## 🛠️ Setup Instructions

### 🧰 Requirements

- Android Studio (Arctic Fox or newer)
- Android SDK 31+
- Java 8+
- Gradle 7+
- [ONNX Runtime Java](https://github.com/microsoft/onnxruntime)

### 🚀 How to Run

1. Clone this repository.
2. Place your ONNX model file (e.g. `model.onnx`) inside the `assets/` folder.
3. Open the project in Android Studio.
4. Sync Gradle and build the app.
5. Install and run on an emulator or real device.
6. Tap "Scan" to check installed apps (note: per-app results not shown yet).

> 📝 **Note:** Scan currently returns only a summary (e.g., "No adware detected") and does not yet list per-app risk scores.

---

## 📁 Project Structure

```bash
├── app/
│   ├── java/
│   │   ├── MainActivity.kt              # Main UI and scan trigger
│   │   ├── MalwareDetector.java        # ONNX Runtime + prediction logic
│   │   └── adapters/
│   │       └── ScanResultAdapter.kt    # (In progress) Adapter for results UI
│   ├── assets/
│   │   └── model.onnx                  # Machine learning model
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   └── values/
│   └── AndroidManifest.xml
```


### 🧪 Development Status

- Core scanning logic ✅
- ONNX model integrated ✅
- Coroutine-based scanning ✅
- per-app risk score display ❌
- Result filtering & UI rendering ❌
- Notifications and logging ❌

### 🛣️ Roadmap
 - Implement risk score visibility for each app
 - Build RecyclerView UI with icons and names
 - Add high-risk filtering (e.g., score > 0.5)
 - Add notification alerts
 - Save scan results locally (SQLite or file-based)
 - Improve UI with Material Design
 - Add "about" page or model versioning display


## ✍️ Author

**Ibrahim Yusuf**  
Cybersecurity | Cloud Security | DevSecOps Enthusiast  
📍 Osun State University | NACSS President  






