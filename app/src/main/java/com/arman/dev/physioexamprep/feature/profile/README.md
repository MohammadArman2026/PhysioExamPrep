# Feature: Profile — Figma "09 · Profile" (node `11:329`)

Route `User` (tab 5, label "Me"). Entry: `presentation/ProfileScreen.kt → ProfileRoute`. (Replaces old `feature/user`.)

> Built from Figma metadata + overview render (MCP limit) — verify styling against `11:329`.

## Screen
"Profile" title + outlined sliders button → identity card (72dp lilac initials avatar, name, "Final year · BPT, Delhi",
limeSoft "Target: AIIMS 2027" pill, edit button) → three stat tiles (Solved ✓ green, Percentile award lilac, Streak flame coral)
→ **Activity** heatmap card (12 weeks × 7 days, levels surface2 → limeSoft → lime → limeDeep, Less/More legend)
→ settings list: Study reminders (7:00 PM ›), Dark mode (toggle), Exam target (AIIMS 2027 ›), Help & support ›, Log out (red).

## MVI (`ProfileContract.kt`)
- State: `profile: ProfileOverview?`, `showLogoutDialog` (+ `initials`)
- Intents: `EditClicked`, `SettingsClicked`, `RemindersClicked`, `DarkModeToggled`, `ExamTargetClicked`, `HelpClicked`,
  `LogoutClicked`, `LogoutConfirmed`, `LogoutDismissed`
- Effects: `ShowMessage`, `LoggedOut` (NavHost shows a toast and goes to Home — there's no auth yet)

## Data
`domain/ProfileModels.kt`, `FakeProfileRepository` (Figma values; heatmap is a stable made-up pattern).
Real: `UserProfileRepository` (name, target exam), `ProgressRepository.observeActivityHistory` (heatmap/streak),
a DataStore for reminder time + dark mode.

## Guesses to confirm
- Dark-mode toggle only stores the flag; the app theme has no dark palette yet. Toggle "on" look (ink track, lime knob).
- Heatmap colours / 4 levels, avatar shape (rounded square 24dp), stat tile layout — from the render.
- Logout confirmation dialog; settings rows → coming soon.
