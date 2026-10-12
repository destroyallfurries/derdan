# DerDan

Watches StatusAI ads for you so you don't have to sit and tap.

## Build
Push to GitHub -> Actions tab -> download `derdan-debug-apk` artifact.

## Install
1. Unzip artifact, tap `app-debug.apk`.
2. Allow "Install unknown apps" for your file manager / browser when prompted.
3. Open DerDan, tap **Enable Accessibility**, toggle DerDan on.
4. Open StatusAI, go to the ad screen, then reopen DerDan and hit **Refresh Log**.

## Spy mode
By default `SPY_MODE = true` in `DerDanService.kt`. It logs every accessibility
node it sees so you can figure out what to click. Once you know the right text
or view ID for StatusAI's "Watch Ad" button, set `SPY_MODE = false` and adjust
`TARGET_TEXT`, then push again.

## Reality check
StatusAI's ToS probably forbids automation. Ad networks detect suspicious
timing. Use at your own risk. Also, on a financed phone you cannot enable
Developer Options, so this app relies entirely on Accessibility (which is a
normal user-facing setting, not a developer one).
