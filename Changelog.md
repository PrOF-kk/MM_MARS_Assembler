## Unofficial Release 5.0

### Major Changes
* MARS now uses the **FlatLaf** Look and Feel, for a much modern look
* The UI icons are now from the **Colibre** icon pack, the same ones used in LibreOffice
* Added <kbd>Ctrl</kbd> + <kbd>7</kbd> shortcut to **toggle comments** for the selected lines (default editor only)
* Added <kbd>Ctrl</kbd> + <kbd>Tab</kbd> and <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>Tab</kbd> shortcuts to **cycle between editor tabs**
* Added <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>S</kbd> shortcut for "Save As"
* Added support for <kbd>Shift</kbd> + <kbd>Scrollwheel</kbd> for **horizontal scrolling**
* Added an **ASCII Table** to the Help panel
* Fixed the file opener dialog not filtering for Assembly files by default
* MARS now always tries to use the system's file chooser, because it is easier to use than the default Metal one
* Added a work-in-progress alternative Editor based on **[RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)**. It currently does not support user-defined syntax highlighting colors
* Screen Magnifier: Fix magnifier sometimes capturing itself instead of MARS window
* Rebuilt the documentation using the current doclet
* Complete refactor of the whole codebase for easier future modifications
* MARS now requires at least Java 8
### Minor Changes
* New higher-resolution taskbar icon with proper trasparency
* Darkened the default green color for comments and string literals
* Changed Run Speed slider label ("Run speed at # inst/sec" -> "Run at # inst/sec")
* Removed the small Collapse/Expand buttons on the Split Pane borders
* Disabled the ability to detach the toolbar as it didn't work properly
* The Find/Replace dialog output text is now green if the operation was successful
* The Find/Replace dialog will now perform a search on <kbd>Enter</kbd>
* Widened default width for the Basic column in the Text Segment Window
* Increased the scrolling speed for the Screen Magnifier tool
* Replaced outdated HTML tags in the help files with more modern ones and CSS
* Various changes and fixes in the Help -> Help (F1) docs
* Various microoptimizations

I hope you find these changes helpful, if you encounter any bugs open an Issue here on Github
