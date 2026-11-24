# Feature: Home — Figma "01 · Home" (node `8:2`)

Route `Home` (start destination, tab 1). Entry: `presentation/HomeScreen.kt → HomeRoute`.

## What's on screen (top → bottom)
Greeting ("Good morning/afternoon/evening" from device hour) + name · coral streak chip (flame) · lilac initials avatar
→ **Daily goal hero** (ink, 28dp radius: TODAY'S GOAL, `32 / 50 Qs`, "18 more to keep your 12‑day streak alive",
96dp lime progress ring, lime "Continue practice") → **Pick up where you left off** resume card (subject colour icon,
progress bar, chevron) → **Bento**: lime "Quick 10" + sky "PYQ of the day" → **Focus areas** card (accuracy < 50 % = red bar/text,
else amber) → **Coming up** lilac live‑mock card with Register pill.

## MVI (`HomeContract.kt`)
- State: `isLoading`, `greeting`, `dashboard: HomeDashboard?` (+ derived `initials`)
- Intents: `ContinuePracticeClicked`, `ResumeClicked`, `QuickMixClicked`, `PyqOfTheDayClicked`, `SeeAllFocusAreasClicked`,
  `FocusAreaClicked(subjectId)`, `RegisterMockClicked`, `AvatarClicked`
- Effects: `OpenQuiz(QuizRequest)`, `OpenTopicPicker(subjectId)`, `OpenLearn`, `OpenProfile`, `ShowMessage`

## Where actions go
| Tap | Result |
|---|---|
| Continue practice | Quiz, `QuizSource.PRACTICE`, count = remaining daily goal |
| Resume card | Quiz, `RESUME`, subject + topic of the resume item |
| Quick 10 | Quiz, `QUICK_MIX`, 10 Qs |
| PYQ of the day | Quiz, `PYQ`, 1 Q, ref = paper id |
| Focus area row | Topic picker for that subject |
| See all | Learn tab |
| Register | marks registered in repo (+1 count), toast; pill turns white "Registered" |
| Avatar | Profile tab |

## Data
`domain/HomeModels.kt` (`HomeDashboard`, `DailyGoal`, `ResumeItem`, `QuickMix`, `PyqOfTheDay`, `FocusArea`, `UpcomingMock`),
`HomeRepository` → `data/FakeHomeRepository` (Figma values). Real version should combine `UserProfileRepository`
(name, daily goal), `ProgressRepository.observeActivityHistory` (streak, today's count), `AssessmentRepository.observeActiveSessions` (resume).

## Guesses to confirm
- Greeting changes with time of day (Figma only shows "Good morning").
- Focus-area colour threshold 50 %. "Biomechanics" row is mapped to Exercise Therapy.
- Register state ("Registered" white pill) isn't in Figma.
- Continue practice = remaining goal count, mixed subjects.
