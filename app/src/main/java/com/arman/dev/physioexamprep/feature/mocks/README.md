# Feature: Mock tests — Figma "07 · Mock tests" (node `10:318`)

Route `Mocks` (tab 4). Entry: `presentation/MocksScreen.kt → MocksRoute`.

> Built from Figma **metadata (sizes/structure) + the low‑res page render** — the detailed design‑context call hit the
> Figma Starter-plan MCP limit. Re-check colours/spacing against Figma when the limit resets (re-run get_design_context on `10:318`).

## Screen
Header "Mock tests" + "Simulate the real exam, end to end" + outlined calendar button → **Live mock hero** (ink, 28dp):
lime "LIVE THIS SATURDAY" badge with red dot, 26sp title, calendar/users meta, 4 Ink2 countdown tiles (Days/Hours/Mins/Secs,
ticking every second), lime "Reserve my seat" → **Full‑length mocks** ("4 tests") white list: 48dp number tile, title,
Easy/Medium/Hard pill + "100 Qs · 120 min", trailing "Scored 72/100" | ink play button | grey lock → dashed
**Build a custom mock** card (limeSoft sliders tile, ink arrow button).

## MVI (`MocksContract.kt`)
- State: `overview: MocksOverview?`, `countdown: Countdown` (recomputed every second in the VM from `startsAtMillis`)
- Intents: `ReserveSeatClicked`, `MockClicked(id)`, `CustomMockClicked`, `CalendarClicked`
- Effects: `OpenQuiz` (available mock → Exam mode), `OpenResult(resultId)` (completed mock), `ShowMessage` (locked / coming soon)

## Data
`domain/MockModels.kt` (`LiveMock`, `FullLengthMock`, `Difficulty`, `MockStatus` Completed/Available/Locked),
`FakeMocksRepository` (live mock starts 4d 12h 36m 8s after launch so the countdown matches Figma).
Real: `MockDefinitionRepository` + `StartAssessmentUseCase.fromMockDefinition` + `AssessmentRepository.observeCompletedAssessments`.

## Guesses to confirm
- Exact colours of the LIVE badge dot (red), countdown tile colour (Ink2), meta text colour (text3), list dividers, dashed border colour.
- Subtitle text ("Simulate the real exam, end to end") — truncated in metadata.
- Lock rule: unlocks after the previous mock. Reserved state = Soft "Seat reserved" button.
