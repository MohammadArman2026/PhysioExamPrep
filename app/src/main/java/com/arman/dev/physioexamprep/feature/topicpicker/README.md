# Feature: Topic picker — Figma "03 · Topic picker" (node `9:76`)

Route `Topics/{subjectId}` (Long). Opened from Learn tiles, Home focus areas, Mock result "Practice weak topics".
Entry: `presentation/TopicPickerScreen.kt → TopicPickerRoute`. (Replaces the old `feature/topiclist`.)

## Screen
Soft subject‑colour header behind the status bar: back (chevL) + bookmark circles, SUBJECT overline in accent,
36sp name, "12 topics · 1,240 questions", three white stat boxes (Complete / Accuracy / Time spent).
Canvas sheet overlaps header by 28dp with 32dp top corners: "Choose topics" + "3 selected · 290 questions" +
"Select all" (accent) → topic rows (selected = 2dp ink border + ink checkbox with lime tick; pill Strong/Fair/Weak/New).
Fixed white bottom sheet: "Questions per session" stepper (−/+ by 5, 5–100), "~16 min · untimed",
Practice / Timed / Exam mode segmented toggle, ink "Start practice · 20 Qs".

## MVI (`TopicPickerContract.kt`)
- State: `subject`, `selectedTopicIds`, `questionsPerSession`, `mode` (+ derived counts, `canStart`, `estimatedMinutes`)
- Intents: `BackClicked`, `BookmarkClicked`, `TopicToggled`, `SelectAllClicked` (toggles to "Clear all"), `Decrease/IncreaseQuestions`, `ModeSelected`, `StartClicked`
- Effects: `NavigateBack`, `OpenQuiz(QuizRequest)` — count is capped at the selected topics' question total.
- `subjectId` comes from `SavedStateHandle` (`Routes.Topics.ARG_SUBJECT_ID`).

## Data
`domain/TopicPickerModels.kt` (`SubjectDetail` incl. `suggestedTopicIds`, `TopicProgress`, `Mastery.from(accuracy)`),
`FakeTopicPickerRepository` (Anatomy = Figma exactly + 5 extra topics so it has 12; other subjects are placeholders).
Real: `ChapterDao.getChaptersBySubject`, `ProgressRepository.observeChapterProgressForSubject`, `BookmarkRepository`.

## Guesses to confirm
- Mastery pills: ≥75 % Strong, 50–74 Fair, <50 Weak, never practised = New.
- Preselected topics (Upper Limb, Thorax, Pelvis) come from `suggestedTopicIds` — e.g. "last selection".
- Stepper step 5, range 5–100; 0.8 min/question; label "untimed / timed / exam conditions" and button verb per mode.
- Bookmark filled-ink state when bookmarked.
