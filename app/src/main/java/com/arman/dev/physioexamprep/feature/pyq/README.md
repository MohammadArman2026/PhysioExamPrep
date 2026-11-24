# Feature: PYQs / Past papers — Figma "06 · Past papers" (node `10:144`)

Route `PYQs` (tab 3). Entry: `presentation/PyqScreen.kt → PyqRoute`.

## Screen
Header "Past papers" + "Previous year questions · 9 exams" + outlined sliders button → white search field
(52dp, 18dp radius, live filtering) → By exam / By topic / By year segmented control → exam cards:
52dp monogram (AIIMS ink/lime, PGIMER coral, JIPMER sky, BHU amber), name, description, chevron;
year chips (solved = green soft + ✓, else surface2), "+N" outlined pill; "3 of 12 papers solved" + ink progress.

## MVI (`PyqContract.kt`)
- State: `exams`, `topicCounts`, `query`, `grouping` (+ derived `visibleExams`, `visibleTopics`, `years`)
- Intents: `QueryChanged`, `GroupingSelected`, `ExamClicked` (opens newest unsolved paper), `PaperClicked(paperId)`,
  `MoreYearsClicked` (switches to By year), `TopicClicked`, `YearClicked`, `FiltersClicked`
- Effects: `OpenQuiz(QuizRequest(source = PYQ, mode = EXAM, refId = "AIIMS-2023"))`, `ShowMessage`

## Data
`domain/PyqModels.kt` (`ExamPapers`, `PastPaper`, `ExamTone`, `PyqTopicCount`, `PyqGrouping`), `FakePyqRepository`
(first 4 exams = Figma; 5 placeholder exams so the "9 exams" subtitle is true). No PYQ tables exist in Room yet —
`MockType.PREVIOUS_YEAR` + `sourcePaperId` in `MockDefinition` look like the intended home.

## Guesses to confirm
- "By topic" and "By year" tabs aren't designed: they use a simple white row list (`PyqListRow`).
- Tapping a year chip opens that paper in Exam mode; the card chevron opens the newest unsolved paper; "+N" jumps to By year.
- The 5 extra exams (CMC, MAHE, GGSIPU, Jamia Hamdard, KGMU) are placeholders.
- Sliders → "Filters coming soon".
