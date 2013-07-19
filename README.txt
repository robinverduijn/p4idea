p4idea
======

A plugin for Intellij IDEA to allow Perforce (P4) to be used as a version control system.
It is still in a rudimentary state but already provides some basic functionality:

- Registers itself with the IDEA Vcs API like a good citizen.
- Configurable from within the IDEA settings panel.
- Handles local file operations done by IDEA, executing the appropriate p4 edit/add/delete/revert commands.
- Leaves a pending (default) changelist with all the local changes; use your full-fledged Perforce GUI (P4V or P4Win)
  to submit these changes, diff them, move them to other changelists, etc.

Future improvements include making sure refactoring is fully supported (e.g. using P4 integrate/copy/smart move),
allowing the plugin to offer named changelists, fixing the inevitable bugs as I find them,
and ensuring performance is good.
