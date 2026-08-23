# KernelSU Component Gap Audit

Source inspected: `/home/heizige/下载/KernelSU-main.zip`.

This audit compares KernelSU's reusable Compose UI pieces with Kedge's current first-stage bridge layer. Domain-specific KernelSU flows are intentionally not copied into Kedge.

## Already Covered In Kedge

| KernelSU area | Kedge equivalent | Notes |
|---|---|---|
| `FloatingBottomBar` | `KedgeLiquidGlassNavigationBar` / `KedgeLiquidGlassBottomBar` | Added with `miuix-blur-android` backdrop blur, glass highlight, moving capsule, press scale, and drag selection. |
| Liquid backdrop source | `rememberKedgeLiquidGlassBackdrop` / `KedgeLiquidGlassBackdrop` | Provides Kedge's wrapper around the capture layer expected by `drawBackdrop`. |
| `BottomBar*`, `NavigationRail*` | `KedgeNavigationBar`, `KedgeBottomBar`, `KedgeNavigationRail`, `KedgeAdaptiveScaffold` | Kedge uses generic route-based wrappers instead of app-specific pager state. |
| `DialogMaterial`, `DialogMiuix`, `ExpressiveDialog` | `KedgeDialog`, `KedgeAlertDialog`, `KedgeDialogHost` | Generic dialogs plus confirm/loading host state. |
| `BottomSheet` / send-log sheet pattern | `KedgeBottomSheet`, `KedgeModalBottomSheet` | Generic wrapper exists; app/domain sheets are intentionally omitted. |
| Toast / transient feedback | `KedgeToastHost`, `KedgeToast`, `rememberKedgeToastController`, `KedgeToastPill` | Kedge uses Khromia Toast; Miuix is handled by custom toast pill colors. |
| `TonalCard` | `KedgeCard`, `KedgeSurface` | Covered at container level. |
| `StatusTag` basic visual | `KedgeStatusTag`, `KedgeTag` | Covered with semantic Info / Success / Notice / Warning / Error severity. |
| Basic edit fields | `KedgeTextField`, `KedgeOutlinedTextField`, `KedgeSegmentedTextField` | Covered with unified and segmented field variants. |
| Search UI | `KedgeSearchBar`, `KedgeSearchTopAppBar` | Kedge bridges native Material3 `SearchBar` and Miuix `SearchBar` / `InputField`; it does not custom-draw a search field. |

## General-Purpose Gaps Added

| Priority | Proposed Kedge component | KernelSU references | Current status |
|---:|---|---|---|
| P0 | `KedgeSearchBar` / `KedgeSearchTopAppBar` | `material/SearchBar.kt`, `miuix/SuperSearchBar.kt`, `SearchStatus.kt` | Added with native Material3/Miuix search components. |
| P0 | `KedgeSegmentedList`, `KedgeSegmentedListItem`, `KedgeSegmentedSwitchItem`, `KedgeSegmentedTextField` | `material/SegmentedList.kt` | Added. |
| P1 | `KedgeTabRow` | `material/ExpressiveTabRow.kt` | Added. |
| P1 | `KedgeToggleButton` / `KedgeSegmentedButton` | `material/ExpressiveToggleButton.kt` | Added. |
| P1 | `KedgeDropdownMenu`, `KedgeDropdownItem`, `KedgeListPopup` | `material/ExpressiveMenu.kt`, `miuix/DropdownItem.kt`, `MenuPositionProvider.kt` | Added. |
| P1 | `KedgeStatusTag` and `KedgeWarningCard` | `statustag/*`, `miuix/WarningCard.kt`, `WarningLevel.kt` | Added. |
| P2 | Toast feedback host state | Khromia Toast + KernelSU transient feedback pattern | Added. Kedge feedback stays Toast-only. |
| P2 | `KedgeLoadingDialog`, `KedgeDialogHost` | `dialog/Dialog.kt`, `DialogHandle`, `LoadingDialogHandle`, `ConfirmDialogHandle` | Added. |
| P2 | `KedgeTopBarBackButton` | `material/TopBarBackButton.kt` | Added. |
| P2 | `KedgeAppIconImage` / `KedgeAvatarImage` | `AppIconImage.kt` | Added as `KedgeAppIconImage`, `KedgeIconImage`, and `KedgeAvatarImage`. |

## Liquid Glass Status

KernelSU's exact glass navigation references these files:

- `FloatingBottomBar.kt`
- `liquid/CombinedBackdrop.kt`
- `liquid/InnerShadow.kt`
- `liquid/Lens.kt`
- `liquid/Vibrancy.kt`
- `miuix/animation/DampedDragAnimation.kt`
- `miuix/animation/InteractiveHighlight.kt`
- `miuix/modifier/DragGestureInspector.kt`

Kedge now uses `top.yukonga.miuix.kmp:miuix-blur-android` directly for backdrop capture and blur drawing:

- `rememberKedgeLiquidGlassBackdrop()` creates a `KedgeLiquidGlassBackdropState`.
- `KedgeLiquidGlassBackdrop(...)` applies `Modifier.layerBackdrop(backdrop)` around a content region.
- `KedgeLiquidGlassNavigationBar(..., backdrop = backdrop)` draws with `Modifier.drawBackdrop(...)` and `blur(...)`.

Remaining deltas from KernelSU's exact implementation:

- Lens refraction and chromatic aberration are not copied yet.
- Inner shadow is approximated by Kedge's existing highlight/depth drawing.
- KernelSU's damped drag and interactive highlight utilities are not copied as standalone APIs.

## Mostly App-Specific, Not Kedge Core

These are useful references but should not be copied into the base Kedge API unchanged:

- `choosekmidialog/*`, `uninstalldialog/*`, `rebootlistpopup/*`, `DownloadDialog.kt`, `SendLogDialog.kt`: domain flows.
- `profile/*`: KernelSU profile configuration UI, too app-specific.
- `markdown/*`: useful only if Kedge wants a rich text/markdown module.
- `filter/*`: data filtering helpers, not core visual components.
- `KsuValidCheck.kt`: KernelSU-specific state gate.
- `KeyEventBlocker.kt`, `ObserveAsEvents.kt`, `ScrollToTop.kt`, `PagerNavigationSpring.kt`: utility helpers; add only if Kedge grows a utilities module.

## Recommended Next Batch

1. Port optional lens / vibrancy / inner-shadow utilities behind a small `kedge-liquid` API.
2. Add a rich Markdown module only if Kedge should cover release-note/help screens.
3. Add optional utility helpers for `ObserveAsEvents`, `ScrollToTop`, and key-event blocking.
