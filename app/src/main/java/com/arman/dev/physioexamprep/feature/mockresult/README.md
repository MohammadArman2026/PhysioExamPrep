# Feature: Mock result — Figma "08 · Mock result" (node `11:214`)

Route `MockResult/{resultId}`. Opened from Mocks (completed mock) and at the end of **every** quiz.
Entry: `presentation/MockResultScreen.kt → MockResultRoute`. Back/X pops.

> Built from Figma metadata + overview render (MCP limit) — verify styling against `11:214`.

## Screen
Ink score hero (behind status bar, 32dp bottom radius): X + "Mock #2 · Result" + share; YOUR SCORE; 72sp "164" + "/200";
lime pill with award icon "Top 12% · Rank 281 of 2,340"; three Ink2 stat tiles (Accuracy / Time / Attempted).
Body: **Subject breakdown** card (name, ±delta vs avg green/red, %, 8dp bar in subject colour) → two insight tiles
(Strongest = successSoft/trend, Needs work = dangerSoft/alert) → ink "Review answers" → outline "Practice weak topics".

## MVI (`MockResultContract.kt`)
- State: `result: MockResult?`
- Intents: `CloseClicked`, `ShareClicked`, `ReviewAnswersClicked`, `PracticeWeakTopicsClicked`
- Effects: `NavigateBack`, `Share(text)` (Android share sheet), `OpenTopicPicker(weakest subject)`, `ShowMessage`

## Data
`domain/MockResultModels.kt` (`MockResult`, `Ranking?`, `SubjectBreakdown`), `FakeMockResultRepository`:
ids `mock-1`/`mock-2` → Figma sample; any other id → `SessionResultStore` (what the quiz just saved; no ranking →
rank pill and "vs. avg" deltas hidden). Real: `AssessmentRepository.observeResult(sessionId)` + a leaderboard API.

## Guesses to confirm
- Figma shows 164/200 for "Mock #2" while the Mocks list shows Mock 02 "64/100" — the sample keeps both as designed.
- Practice sessions reuse this screen (no separate practice summary in Figma).
- "Review answers" → coming soon. Hero bottom radius 32dp, stat tiles Ink2 — from the render.
