# core — MVI base, shared UI, shared contracts

## core/mvi/Mvi.kt
- `UiState`, `UiIntent`, `UiEffect` marker interfaces.
- `MviViewModel<S, I, E>(initialState)`:
  - `state: StateFlow<S>` · `effects: Flow<E>` (Channel, each effect delivered once)
  - `onIntent(intent)` → your `handleIntent(intent)`
  - `setState { copy(...) }` (reducer) · `sendEffect(effect)` · `currentState`
- `@Composable CollectEffects(viewModel.effects) { effect -> … }` — use in every `*Route`.
- State is collected with `collectAsState()` (the project has no `lifecycle-runtime-compose` dependency;
  switch to `collectAsStateWithLifecycle()` if you add it).

## core/ui/components (Figma "Components" row, y=1500)
| Composable | Figma component | Notes |
|---|---|---|
| `PulseButton(style = Lime/Ink/Outline/Soft)` | Button `6:197` | 56dp, 18dp radius, trailing `ic_arrow_right` |
| `PulseChip` | Chip `6:224` | Default = white + line border, Active = ink |
| `OptionRow(state = Default/Selected/Correct/Wrong)` | OptionRow `6:219` | letter badge 40dp, 20dp radius |
| `PhysioExamPrepBottomBar` | BottomNav `6:176` | floating ink pill 68dp; active tab = lime capsule |
| `Pill`, `ProgressTrack`, `ProgressRing`, `SegmentedControl`, `IconBadge`, `HeaderIconButton`, `SectionHeader`, `ScreenHeader`, `Modifier.pulseCard()` | repeated patterns in the frames | |

StatusBar (`6:50`) is **not** drawn — the real system bar is used; screens draw behind it (edge-to-edge) and
apply `statusBarsPadding()` themselves so coloured headers (Topic picker, Mock result) reach the top.

## core/ui/subject/SubjectVisuals.kt
`subjectVisual(subjectId)` → accent, soft tint, icon. Anatomy=coral/user, Physiology=sky/heart,
Exercise Therapy=limeDeep/activity, Electrotherapy=amber/zap, Orthopedics=lilac/bone, Neurology=ink on surface2/share.

## core/domain/model (cross-feature contracts)
- `SubjectIds` — 1 Anatomy, 2 Exercise Therapy, 3 Electrotherapy, 4 Orthopedics (match the DB), 5 Physiology, 6 Neurology.
- `QuizRequest` / `QuizSource` / `PracticeMode` — how any feature starts a quiz. Serialised by `navigation/Routes.Quiz`.
- `ScoredSession` + `SessionResultStore` — quiz writes, mock result reads (`core/data/fake/InMemorySessionResultStore`, bound in `di/FakeDataModule`).

## Navigation (navigation/)
`Routes.kt` holds every route + its typed args and `create(...)` helpers. `PhysioExamPrepNavHost` overlays the
bottom bar on tab screens and passes them `contentPadding` so their last item clears the bar.
