# Feudal Tactics - Medieval Strategy Game

A medieval strategy game with countless unique and challenging levels. You play on an randomly generated island made of hexagons. Your goal is to conquer all of it. To do so, you attack the enemy with your units while trying to protect your own kingdoms. Made with libGDX. The game mechanics are heavily inspired by Sean O'Connor's Slay.

[![F-Droid](https://img.shields.io/f-droid/v/de.sesu8642.feudaltactics?&color=306bc0&logo=f-droid)](https://f-droid.org/en/packages/de.sesu8642.feudaltactics/)
[![Google Play](https://img.shields.io/static/v1?label=Google&message=Play&color=306bc0&logo=google-play)](https://play.google.com/store/apps/details?id=de.sesu8642.feudaltactics)
[![itch.io](https://img.shields.io/static/v1?label=itch.io&message=Feudal%20Tactics&color=306bc0&logo=itch.io)](https://sesu8642.itch.io/feudal-tactics)
[![Flathub](https://img.shields.io/flathub/v/de.sesu8642.feudaltactics?&color=306bc0&logo=flathub)](https://flathub.org/apps/details/de.sesu8642.feudaltactics)
[![Matrix Community](https://img.shields.io/badge/Matrix-Community-blue?logo=matrix)](https://matrix.to/#/#feudal-tactics-community:matrix.org)

![Ingame Screenshot](metadata/en-US/images/sevenInchScreenshots/1.png)

## Roadmap
I would like to implement the following features. It might happen soon, in a few years or never.
- ~~playable tutorial~~
- sound
- scenario maps
- map editor
- local multiplayer
- online multiplayer

## Licensing

Copyright (C) 2022  Sesu8642

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.

## Licensing of Contributions
Contributions are multi-licensed under __GPL-3.0-or-later or MIT__. See [CLA](CLA.md).

This allows me to potentially release versions that contain propriatary libraries, e.g. for integrating third party multiplayer services or receiving payments. The F-Droid version will never contain any proprietary components, of course.

Why not just license contributions under MIT? All files somebody contributes to would be licensed under GPL-3.0-or-later __and__ MIT. While the MIT license is compatible with the GPL-3.0, it does add the additional requirement to include the MIT license text in the software. This would make it complicated if anyone wanted to use some file in a different project. With the multi-licensing, every file can simply be used under GPL-3.0-or-later.

## Contributing
Before starting any work, please propose your changes in a GitHub issue.

Before a pull request can be accepted, you must sign the [CLA](CLA.md) by adding your name to the table.

## Privacy

No data is collected at all.
See [privacy policy](https://raw.githubusercontent.com/Sesu8642/FeudalTactics/master/privacy_policy.txt). It is based
on [the one from the Catima Website](https://github.com/CatimaLoyalty/Website/blob/98018f63d0f69331de70054db61c699b9c316c2c/_pages/privacy-policy.md).

## Language Support
The game is currently available in English and German.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## Wiki
Additional technical documentation is available in the [wiki](../../wiki).
