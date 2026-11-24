# Feature: Quiz — Figma "04 · Quiz" (`9:198`) + "05 · Answer feedback" (`9:276`)

Route `Quiz?source=&count=&mode=&subjectId=&topics=&ref=` built from a `core.domain.model.QuizRequest`
(`Routes.Quiz.create(request)` / `Routes.Quiz.requestFrom(savedStateHandle)`). One screen handles both frames.
Entry: `presentation/QuizScreen.kt → QuizRoute`. System back = close.

## Screen
Quiz bar: outlined X (44dp) · one 6dp segment per question (done = ink, current = subject accent, upcoming = line) ·
ink timer chip with lime clock. "Question 3 of 20" + subject·topic pill (subject soft/accent). 24sp question.
Options = `core OptionRow` (Default/Selected/Correct/Wrong). Bottom bar: flag + bookmark 56dp circles + lime "Check answer".
After checking (Practice/Timed): options lock, correct = green, wrong pick = red, and the ink **Explanation sheet**
appears at the end of the scroll (auto‑scrolls): red X/green ✓ badge, "Not quite"/"Nice, that's right", "You picked …",
"64% got it right" pill, Ink2 explanation box with lime bulb, tags (first tag lime), Soft "Explain more" + lime "Next".

## MVI (`QuizContract.kt`)
- State: `session`, `index`, `selections` (qId→optionId), `checked`, `bookmarked`, `flagged`, `elapsedSeconds`,
  `isSubmitting`, `showExitDialog` (+ derived `current`, `optionState()`, `segment()`, `timerLabel`, `instantFeedback`)
- Intents: `OptionSelected`, `CheckAnswer`, `Next`, `ToggleBookmark`, `ToggleFlag`, `ExplainMore`, `CloseClicked`, `ConfirmExit`, `DismissExit`
- Effects: `NavigateBack`, `OpenResult(resultId)`, `ShowMessage`
- Ticker in the VM: counts up when untimed; counts down for Timed/Exam (`questions × 48s`) and auto‑submits at 0.
- Exam mode (`PracticeMode.EXAM`, used by PYQ papers & mocks): no feedback; button is Next → Submit.
- Last question → `submit()` → `QuizRepository.submit` stores a `ScoredSession` → result screen (quiz popped from back stack).

## Data
`domain/QuizModels.kt`, `data/FakeQuestionBank.kt` (15 sample Qs; #1 is the Figma supraspinatus question),
`data/FakeQuizRepository.kt` (topic matches → rest of subject → anything; count capped at bank size, so sessions are short).
Real: `StartAssessmentUseCase` / `SaveAnswerUseCase` / `CompleteAssessmentUseCase` + `QuestionDao.getQuestionWithOptions`.

## Guesses to confirm
- Correct‑answer header copy ("Nice, that's right") and green ✓ badge — only the wrong state is in Figma.
- Timer behaviour (up vs down) and 48s/question limit; "Time's up" auto-submit.
- Exit confirmation dialog when answers exist (not in Figma).
- "Explain more" → coming‑soon toast. Flag/bookmark filled state = ink circle + lime icon.
