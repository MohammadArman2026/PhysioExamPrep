# 🟢 Future-Proof Package Structure for the Exam Preparation App

> A scalable and clean architecture for a Kotlin + Jetpack Compose exam preparation app.

---

# 🌿 Project Structure

```text
com.yourapp
│
├── core
│   ├── common
│   │   ├── Result.kt
│   │   ├── Constants.kt
│   │   └── Extensions.kt
│   │
│   ├── ui
│   │   ├── theme
│   │   ├── components
│   │   └── navigation
│   │
│   ├── database
│   │   ├── AppDatabase.kt
│   │   ├── dao
│   │   ├── entity
│   │   └── mapper
│   │
│   └── util
│       ├── DateUtils.kt
│       ├── PreferenceManager.kt
│       └── NetworkUtils.kt
│
├── feature
│   ├── home
│   │   ├── presentation
│   │   ├── domain
│   │   └── data
│   │
│   ├── subject
│   │   ├── presentation
│   │   ├── domain
│   │   └── data
│   │
│   ├── practice
│   │   ├── presentation
│   │   ├── domain
│   │   └── data
│   │
│   ├── pyq
│   │   ├── presentation
│   │   ├── domain
│   │   └── data
│   │
│   ├── result
│   │   ├── presentation
│   │   ├── domain
│   │   └── data
│   │
│   ├── bookmarks
│   ├── premium
│   ├── ads
│   ├── analytics
│   ├── profile
│   ├── settings
│   ├── streak
│   ├── progress
│   └── notifications
│
├── di
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
│
├── navigation
│   ├── AppNavGraph.kt
│   └── Routes.kt
│
└── MainActivity.kt
```

---

# 🟩 Why This Structure Is Better

Instead of using:

```text
ui/
viewmodel/
repository/
```

Use a feature-based structure:

```text
feature/home
feature/practice
feature/pyq
feature/result
```

### ✅ Benefits

* Easier to understand
* Easier to maintain
* Easier to add new features later
* Less confusion in large projects
* Better separation of code

---

# 💚 Recommended Layers Inside Every Feature

```text
presentation
 domain
 data
```

## 🟢 presentation

Contains everything related to UI.

```text
- Compose Screens
- ViewModels
- UiState
- Reusable UI components
```

Example:

```text
feature/practice/presentation
├── PracticeScreen.kt
├── PracticeViewModel.kt
├── PracticeUiState.kt
└── components
```

---

## 🟢 domain

Contains business logic and use cases.

```text
- GetQuestionsUseCase.kt
- SubmitAnswersUseCase.kt
- CalculateScoreUseCase.kt
```

---

## 🟢 data

Contains everything related to data handling.

```text
- Repository
- Database access
- Mapping logic
```

Example:

```text
feature/practice/data
└── PracticeRepository.kt
```

---

# 🌱 Example: Practice Feature Structure

```text
feature/practice
│
├── presentation
│   ├── PracticeScreen.kt
│   ├── PracticeViewModel.kt
│   ├── PracticeUiState.kt
│   └── components
│       ├── QuestionCard.kt
│       ├── OptionItem.kt
│       └── ProgressBar.kt
│
├── domain
│   ├── GetQuestionsUseCase.kt
│   └── SubmitAnswersUseCase.kt
│
└── data
    └── PracticeRepository.kt
```

---

# 🟢 Database Package Structure

```text
core/database
│
├── entity
│   ├── QuestionEntity.kt
│   ├── SubjectEntity.kt
│   └── CollegeEntity.kt
│
├── dao
│   ├── QuestionDao.kt
│   ├── SubjectDao.kt
│   └── CollegeDao.kt
│
└── AppDatabase.kt
```

---

# 🌿 Future Features You Can Add Later

```text
feature/bookmarks
feature/recentTests
feature/downloadedNotes
feature/premium
feature/payment
feature/leaderboard
feature/rankPrediction
feature/search
feature/filter
feature/notifications
```

### ✨ Why This Helps

Whenever you add a new feature:

1. Create a new folder inside `feature/`
2. Add `presentation`, `domain`, and `data`
3. Keep everything related to that feature inside that folder

Example:

```text
feature/bookmarks
├── presentation
├── domain
└── data
```

---

# 🟩 Best Rule To Follow

> Keep related files together.
>
> Do not mix files from different features.
>
> Organize by feature, not by file type.

That will keep your project clean, beautiful, and scalable even when the app becomes large. 🚀
