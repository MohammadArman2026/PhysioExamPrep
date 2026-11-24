# Features — index (read this first in a new session)

Figma source: **PhysioPrep — Fresh Concept**
`https://www.figma.com/design/j1gNeHsDhsI9gVq2tL0Ak3/Untitled?node-id=6-2` (page `6:2`)

| Feature folder | Figma frame (node) | Route | Status |
|---|---|---|---|
| `home/` | 01 · Home (`8:2`) | `Home` (tab) | UI + MVI done, fake data |
| `learn/` | 02 · Learn (`8:137`) | `Learn` (tab) | UI + MVI done, fake data |
| `topicpicker/` | 03 · Topic picker (`9:76`) | `Topics/{subjectId}` | UI + MVI done, fake data |
| `quiz/` | 04 · Quiz (`9:198`) + 05 · Answer feedback (`9:276`) | `Quiz?source=…` | UI + MVI done, 15-question sample bank |
| `pyq/` | 06 · Past papers (`10:144`) | `PYQs` (tab) | UI + MVI done, fake data |
| `mocks/` | 07 · Mock tests (`10:318`) | `Mocks` (tab) | Built from metadata + overview render (see its README) |
| `mockresult/` | 08 · Mock result (`11:214`) | `MockResult/{resultId}` | Built from metadata + overview render |
| `profile/` | 09 · Profile (`11:329`) | `User` (tab "Me") | Built from metadata + overview render |

Shared Figma components (`6:50`–`6:224`) live in `core/ui/components/` — see `core/mvi/README.md`.

## Layout of every feature

```
feature/<name>/
├── README.md                 ← context for that feature (keep it updated when you change it)
├── domain/                   ← models + repository interface (no Android imports)
├── data/                     ← Fake<Name>Repository (in-memory, matches Figma sample data)
├── di/<Name>Module.kt        ← Hilt @Binds interface → fake impl  (swap here for a real impl)
└── presentation/
    ├── <Name>Contract.kt     ← State (UiState) · Intent (UiIntent) · Effect (UiEffect)
    ├── <Name>ViewModel.kt    ← extends core.mvi.MviViewModel; reducer + side effects
    ├── <Name>Screen.kt       ← <Name>Route (stateful: VM + effects → nav) and <Name>Screen (stateless, previewable)
    └── components/           ← composables only used by this feature
```

## Rules we follow
- UI → `viewModel.onIntent(Intent)` only. UI reads `state` (StateFlow) and collects `effects` once via `CollectEffects`.
- State changes only through `setState { copy(...) }`. Navigation/toasts/share are **Effects**, never state.
- Features never import each other. Cross-feature contracts live in `core/domain/model`
  (`QuizRequest`, `PracticeMode`, `SubjectIds`, `ScoredSession` + `SessionResultStore`).
- Colours / type only from `core/ui/theme` (`Ink`, `Lime`, `PulseType.*`). Subject colours via `core/ui/subject/subjectVisual(id)`.
- Icons are `res/drawable/ic_*.xml`, exported from the Figma `icon/*` layers; tint them with `Icon(tint = …)`.
- Every screen has `@Preview`s fed by its fake repository.

## Data: fake now, Room later
All repositories are fakes (user's choice). The real Room DB + `core/data/repository/*` (Assessment, Progress,
Bookmark, MockDefinition, UserProfile) already exist but no screen uses them yet. To go real for a feature:
write `<Name>RepositoryImpl` in its `data/` using the core repositories/DAOs and change the `@Binds` in its `di/` module.

Known DB gaps: `assets/physio_exam_prep.db` has only 4 subjects (no Physiology/Neurology), 12 chapters, 6 questions,
and its `questionType` values are `SINGLE_CHOICE` while `QuestionEntity.QuestionType` expects `SINGLE_CORRECT`.

## Not built yet (no Figma frames)
Search, filters, custom mock builder, mock calendar, answer review, edit profile, settings, reminders, auth/logout,
dark theme. Their buttons emit a "coming soon" toast effect so the wiring is ready.
