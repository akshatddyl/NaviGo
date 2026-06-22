# NaviGo

NaviGo is an Android app for **accessible indoor navigation**, built to help users move through complex indoor spaces using voice-assisted, step-aware guidance.

It combines:
- **Local indoor maps** (recorded on-device)
- **Cloud map sharing** via Firebase
- **Voice + NLP destination understanding** (English/Hindi/Hinglish)
- **Graph-based routing** with accessibility-aware path selection

---

## ✨ Features

- **Accessible indoor navigation**
  - Turn-by-turn guidance
  - Live progress and rerouting support
  - Step-counter + compass-based movement tracking
  - Haptic and speech feedback support

- **Voice-first localization flow**
  - 3-step flow: floor → current location → destination
  - Speech recognition with partial transcripts
  - Bilingual support (English + Hindi)

- **Map setup mode (Helper flow)**
  - Record venue nodes/paths using sensors
  - Save venue maps locally with Room

- **MapShare**
  - Browse public venues
  - Download maps to local device
  - Upload locally created venues (publisher flow)

- **GraphRAG-assisted query resolution**
  - Uses Gemini + Neo4j vector search to interpret natural language queries
  - Falls back to local matching when needed

---

## 🧱 Tech Stack

- **Kotlin + Jetpack Compose**
- **Android Navigation Compose**
- **Hilt (DI)**
- **Room (local storage)**
- **Firebase Auth + Firestore**
- **Retrofit + OkHttp**
- **Neo4j AuraDB**
- **Gemini API**
- **KSP**

---

## 📁 Project Structure

- `app/src/main/java/com/sensecode/navigo/`
  - `navigation_ui/` — localization + active navigation screens/viewmodels
  - `setup/` — venue setup/recording flow
  - `mapshare/` — public/local venue browsing and download
  - `data/` — repositories, local DB, remote clients (Firebase/Neo4j/Gemini)
  - `engine/` — navigation engine logic
  - `sensors/` — step counter + compass integration
  - `audio/` — speech input and text-to-speech managers

---

## ⚙️ Prerequisites

- Android Studio (latest stable recommended)
- JDK 17
- Android SDK 34
- A Firebase project (Firestore/Auth)
- A Neo4j AuraDB instance
- A Gemini API key

---

## 🔐 Configuration

Create or update `local.properties` in the project root:

```properties
NEO4J_URI=your-auradb-uri.databases.neo4j.io
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=your-auradb-password
GEMINI_API_KEY=your-gemini-api-key
