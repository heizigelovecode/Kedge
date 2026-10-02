# Changelog

All notable changes to Kedge will be documented in this file.

## 0.1.2 - 2026-10-02

### Added

- Added `KedgeEditDialog`: dual-style dispatch for the multi-field edit dialog. MD3Exp delegates to Khromia `EditDialog`; Miuix uses a private implementation built on Miuix `WindowDialog`, `KedgeTextField`, `KedgeButton`/`KedgeTextButton` and Miuix's own corner-radius tokens (no MD3 28dp/16dp leakage). `EditFieldConfig` and the validation rules are reused from Khromia rather than redefined, so the two styles share one set of rules. Requires Khromia 1.6.5.
- Added `KedgeSegmentedListSlots`: a slot-based grouped list whose container only owns the group shape — the item gap comes from the caller and each item's `content` receives its own `(index, count)` among visible items so the caller can compute corner radii itself. Intended for hosts whose corner-radius policy is user-configurable, and for rows that need text fields, error colors or custom type scales that `KedgeSegmentedListItem`'s `String` parameters cannot express. The container deliberately does not add a surface, so rows that already carry their own background and shape are not double-wrapped. `KedgeSegmentedList` is unchanged.

### Changed

- `KedgeOptionItem`'s `onClick` is now nullable. Passing `null` marks a pure-information row: it is not clickable and has no press feedback in either style, instead of silently bouncing on press. Requires Khromia 1.6.5.

### Notes

- `material3` is pinned to `1.5.0-alpha29` and must stay in sync with KhatKit and Khromia; a mismatch compiles fine but throws `NoSuchMethodError` at runtime.

## 0.1.1 - 2026-09-12

### Added

- Added `KedgeSearchAppBar`: two-row app bar with an integrated search field. MD3Exp builds on the current search APIs (`rememberContainedSearchBarState`, new collapsed `SearchBar`, `ExpandedFullScreenContainedSearchBar`); Miuix uses the stock Miuix top app bar plus `SearchBar`. The title row and the search field sit in one column with no vertical gap between them.
- Added `KedgeTwoRowsTopAppBar` with configurable collapsed/expanded heights and an optional `windowInsets` override. MD3Exp maps to Material3 `TwoRowsTopAppBar`; Miuix falls back to the large Miuix top app bar.

### Fixed

- Fixed MD3 `KedgeSearchBar` gaining a phantom status-bar-height gap above the input field: the deprecated query-based `SearchBar` shim applies `SearchBarDefaults.windowInsets` (system bars) by default. The MD3 branch now uses `rememberSearchBarState` + `SearchBarDefaults.InputField`, which apply no vertical window insets while collapsed.
- Fixed `KedgeSearchBar` consuming the caller `modifier` twice (outer box and inner search bar both applied it).
- `KedgeSearchBar` now reserves 16dp of top spacing on MD3Exp only; the Miuix branch keeps its stock spacing.

## 0.1.0 - 2026-08-23

### Added

- Added `KedgeStyle`, `LocalKedgeStyle`, and `KedgeTheme`.
- Added MD3Exp / Miuix bridge for buttons, icon buttons, text fields, selection controls, sliders, badges, tags, cards, surfaces, and layout scopes.
- Added `KedgeAdaptiveScaffold` with Material3 `NavigationSuiteScaffold` and Miuix responsive navigation branches.
- Added top app bar, large top app bar, bottom bar, navigation bar, navigation rail, breadcrumb bar, and top-bar back button.
- Added `KedgeLiquidGlassNavigationBar` and `KedgeLiquidGlassBottomBar` with `miuix-blur-android` backdrop blur support.
- Added `rememberKedgeLiquidGlassBackdrop` and `KedgeLiquidGlassBackdrop` helpers for page-level blur capture.
- Added native search bridge: Material3 `SearchBar` for MD3Exp and Miuix `SearchBar` / `InputField` for Miuix.
- Added dialog, alert dialog, bottom sheet, modal bottom sheet, dropdown menu, list popup, loading dialog, and dialog host state.
- Added Toast-only feedback: `KedgeToastHost`, `KedgeToast`, `rememberKedgeToastController`, `KedgeToastPill`, and `KedgeDismissToast`.
- Added preference components: category, text preference, switch preference, list preference, arrow preference, and option model.
- Added segmented list, segmented switch/checkbox/radio/text-field rows, tab row, toggle button, segmented button, status tag, warning card, and image containers.
- Added first complete showcase in `KedgeUsageExample.kt`.
- Added documentation, roadmap, contributing guide, KernelSU gap audit, and MIT license.

### Changed

- MD3 text fields default to 20dp rounded corners.
- MD3 buttons and icon buttons support Material3 Expressive shape morph via `shapes` parameters.
- Miuix theme defaults to Miuix color modes rather than Material You dynamic color.
- Kedge settings rows prefer Khromia MD3Exp assets on the MD3 branch and Miuix-style custom rows on the Miuix branch.

### Removed

- Removed Snackbar API from the public feedback surface. Kedge uses Toast for transient feedback.
