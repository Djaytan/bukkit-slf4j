# Contributing

When contributing to this repository, please first discuss the change you wish to make via issues,
discussions, or any other method with the owners of this repository before making a change.

Please note we have a [code of conduct](CODE_OF_CONDUCT.md), please follow it in all your
interactions with the project.

## How To Contribute

A contribution can be as simple as opening a discussion or reporting us an issue, an idea of
enhancement or a code improvement. More details can be found about the
[types of contributions](docs/types-of-contributions.md) for this repo.

No matter of your capabilities or how important is your wish to contribute on this project, your
help will be the welcome and very appreciated!

## First Contribution

If this is your first contribution, we recommend you to get familiar with the process through
this [GitHub repository](https://github.com/firstcontributions/first-contributions).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for
development and testing purposes.

### Prerequisites

Working on this project requires the following dependencies installed on your local environment:

* JDK 11 ([Download Link](https://adoptium.net/en-GB/temurin/releases/?version=11))
* Maven
  3.6+ ([Download Link](https://maven.apache.org/download.cgi) | [Install Guide](https://maven.apache.org/install.html))

### Installing

The project use Maven as build system, so you only need to execute the following code:

    $ mvn clean install

### Documentation & Help

If you want to work on Bukkit related aspects, you will find the API JavaDoc
[here](https://hub.spigotmc.org/javadocs/spigot/).

A [wiki](https://bukkit.fandom.com/wiki/Main_Page) exists as well to get familiar with Bukkit
core concepts.

Anyway, if you have any question, don't hesitate to
[ask us](https://github.com/Djaytan/mc-jobs-reborn-patch-place-break/discussions)! We are here
to help you for getting started.

## Pull Requests

1. Try to open several smaller PRs instead of only a big one, it will make the job of the reviewers
   easier.
2. Give a summary of the changes provided by your PR. Link any related issue, discussion or
   documentation that could help the reviewer understand your work, the impacts and the plus-value.
3. You will need at least one approval of a reviewer before being able to merge the PR.
4. All automatic jobs must pass (CircleCI, SonarQube analysis, formatting verification, ...) before
   merging.

## Code Formatting

The only thing we ask when contributing to the code is to apply
[google-java-format](https://google.github.io/styleguide/javaguide.html) style guidelines.

To help respecting this, an automatic-formatter as been configured through Maven to be dispatched
automatically when building the project and running tests. If you forgot to run Maven before
committing and pushing your changes, your PR will be prevented from being merged by our CI until
you solve the issue.

### IntelliJ IDEA plugin

If you are using IntelliJ IDEA, you can install the
[google-java-format plugin](https://plugins.jetbrains.com/plugin/8527-google-java-format) which will
replace the default IDE code formatting behavior.

Take care to well
follow [these instructions](https://github.com/google/google-java-format/blob/master/README.md#intellij-jre-config).
