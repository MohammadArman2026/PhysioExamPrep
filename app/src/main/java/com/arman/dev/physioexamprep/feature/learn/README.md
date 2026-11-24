# Feature: Learn — Figma "02 · Learn" (node `8:137`)

Route `Learn` (tab 2). Entry: `presentation/LearnScreen.kt → LearnRoute`.

## Screen
Header "Learn" + "6 subjects · 6,240 questions" + outlined search button → filter chips (All / In progress /
Not started / Saved) → **Overall mastery** card (47 %, "+6% this week" green trend, segmented bar) → 2‑column grid of
192dp subject tiles in the subject's soft colour (white icon circle, % in accent, name, "1,240 Qs · 12 topics", progress on white track).
Neurology shows "New" instead of a %.

## MVI (`LearnContract.kt`)
- State: `isLoading`, `overview: LearnOverview?`, `filter: SubjectFilter` (+ derived `visibleSubjects`)
- Intents: `FilterSelected`, `SubjectClicked(subjectId)`, `SearchClicked`
- Effects: `OpenTopicPicker(subjectId)`, `ShowMessage`

## Data
`domain/LearnModels.kt` (`SubjectSummary`, `LearnOverview`, `SubjectFilter` with `matches()`), `LearnRepository` →
`data/FakeLearnRepository`. Real: `SubjectDao.getAllActiveSubjects()` + `QuestionDao.getTotalQuestionsBySubject()` +
`ProgressRepository.observeChapterProgressForSubject()`.

## Guesses to confirm
- Segmented mastery bar: each coloured segment = that subject's mastered questions ÷ all questions; grey = rest.
- Filter meanings: In progress = mastery > 0, Not started = 0, Saved = `isSaved` (Anatomy & Electrotherapy in fake data).
- Search button → "coming soon" toast (no search screen in Figma).
