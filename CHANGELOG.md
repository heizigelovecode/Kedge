# Changelog

All notable changes to Kedge will be documented in this file.

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
