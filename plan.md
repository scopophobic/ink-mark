# Midnight Journal — Development Plan

This document breaks the project into **clear, buildable phases** so you can execute steadily on an 8GB RAM machine using Cursor + Android Studio.

---

## 1. Project Goals (Non-Negotiables)

* Fully **offline**, privacy-first journaling app
* **Drawing-first experience** (no clutter on home grid)
* Minimalist, monotone UI using **InkPrimary (#32127A)** only
* Immutable entries — once saved, **never editable**
* Optimized for low-resource development and runtime

---

## 2. Tech Stack & Architecture

### Core Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM
* **Database:** Room (local only)
* **Image Loading:** Coil
* **Storage:** Internal app storage (PNG files)

### Architecture Layers

```
UI (Compose Screens)
│
├── ViewModels (State + Logic)
│
├── Repository (Single source of truth)
│
├── Room Database
│
└── Internal Storage (PNG drawings)
```

---

## 3. Folder & Package Structure

```
com.midnightjournal
│
├── data
│   ├── local
│   │   ├── MemoryEntity.kt
│   │   ├── MemoryDao.kt
│   │   └── MemoryDatabase.kt
│   └── repository
│       └── MemoryRepository.kt
│
├── ui
│   ├── theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   │
│   ├── screens
│   │   ├── home
│   │   ├── draw
│   │   └── entry
│   │
│   └── components
│       ├── DrawingCanvas.kt
│       └── SearchBar.kt
│
├── viewmodel
│   ├── HomeViewModel.kt
│   └── EntryViewModel.kt
│
└── utils
│   ├── FileManager.kt
│   └── BitmapUtils.kt
```

---

## 4. Database Design (Room)

### Entity: `memory_table`

| Column     | Type   | Notes                      |
| ---------- | ------ | -------------------------- |
| id         | Int    | Auto-generated primary key |
| title      | String | User-defined               |
| note       | String | Memory text                |
| tags       | String | Comma-separated            |
| image_path | String | Internal storage path      |
| created_at | Long   | Unix timestamp             |

### DAO Responsibilities

* Insert memory
* Search by title / tags / date
* Fetch all memories (latest first)

---

## 5. Internal File Management

### Storage Rules

* Folder: `context.filesDir/drawings/`
* Format: PNG
* Resolution: **512 × 512**
* Naming: `drawing_<timestamp>.png`

### Why Internal Storage?

* Invisible to gallery apps
* Auto-deleted on uninstall
* Maximum privacy

---

## 6. UI Theme Rules (Strict)

### Colors

```kotlin
val InkPrimary = Color(0xFF32127A)
val InkBlack = Color(0xFF000000)
val InkSurface = Color(0xFFFBFBFF)
```

### UI Constraints

* No gradients
* No shadows
* No multiple colors
* All strokes = `InkPrimary`

---

## 7. Screen-by-Screen Plan

### 7.1 Home Screen

**Purpose:** Visual archive

**Components:**

* Search Bar (top)
* `LazyVerticalGrid`

**Rules:**

* Show **only drawing previews**
* No text overlays
* Infinite scroll
* Coil for image loading

**Search Logic:**

* Filters by title, tags, or date
* Uses `derivedStateOf` to avoid recomposition

---

### 7.2 Drawing Screen

**Canvas Rules:**

* Square canvas
* Fixed pen thickness
* One color only
* Vector-based drawing using `Path`

**State Model:**

```kotlin
List<Path>
```

**Features:**

* Draw
* Undo last stroke
* Clear (optional)
* Save

**Save Flow:**

1. Render paths to bitmap
2. Resize to 512×512
3. Save PNG
4. Return image path

---

### 7.3 Entry Creation Screen

**Inputs:**

* Title (required)
* Text memory
* Tags (comma-separated)

**Flow:**

* Drawing → Entry Details → Save

**Rule:**

* Once saved → **no edit option exists**

---

## 8. ViewModel Responsibilities

### HomeViewModel

* Load all entries
* Handle search queries
* Expose filtered list

### EntryViewModel

* Manage drawing state
* Handle bitmap generation
* Save entry to DB

---

## 9. Performance & Memory Strategy

### Compose Optimization

* Use `remember` aggressively
* Use `derivedStateOf` for filtering
* Avoid recomposing Canvas unnecessarily

### Device Strategy

* No emulator
* USB debugging only
* Physical phone always connected

---

## 10. Development Phases

### Phase 1 — Foundation

* Project setup
* Theme
* Navigation

### Phase 2 — Database

* Room entities
* DAO
* Repository

### Phase 3 — Drawing Engine

* Canvas
* Path logic
* Undo
* PNG export

### Phase 4 — Entry Flow

* Entry screen
* Save logic

### Phase 5 — Home Grid + Search

* LazyVerticalGrid
* Coil
* Search filtering

### Phase 6 — Polish

* UI spacing
* Edge cases
* Crash testing

---

## 11. Definition of “Done”

* App runs smoothly on physical phone
* No memory leaks
* Entries are immutable
* Drawings load instantly
* UI stays monotone and calm

---

**Build slow. Build clean. This app should feel quiet.**
