### Conventional Commits

Our guidelines take inspiration from the [Angular project](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#type).

#### Commit Message Header

```
<type>(<scope>): <short summary>
  │       │             │
  │       │             └─⫸ Summary in present tense. Not capitalized. No period at the end.
  │       │
  │       └─⫸ Commit Scope: deps
  │
  └─⫸ Commit Type: feat|fix|perf|refactor|docs|test|build|ci|chore
```

The `<type>` and `<summary>` fields are mandatory, the `(<scope>)` field is optional.

##### Type

It must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **perf**: A code change that improves performance
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **docs**: Documentation only changes
* **test**: Adding missing tests or correcting existing tests
* **build**: Changes that affect the build system or external dependencies
* **ci**: Changes to our CI configuration files and scripts (e.g. GitHub Actions, Renovate, ...)
* **chore**: Changes that don't in any other category (e.g. dependencies' update, update
  of `.gitignore` `.gitattributes` & `.editorconfig` files, ...)

##### Summary

The summary must provide a succinct description of the change:

* Use the imperative, present tense: "change" not "changed" nor "changes"
* Don't capitalize on the first letter
* No dot (.) at the end

#### Commit Message Body

Just as in the summary, use the imperative, present tense: "fix" not "fixed" nor "fixes".

Explain the motivation for the change in the commit message body. This commit message should explain
_why_ you are making the change.
You can include a comparison of the previous behavior with the new behavior in order to illustrate
the impact of the change.

#### Commit Message Footer

The footer can contain information about breaking changes and is also the place to reference GitHub
issues, Jira tickets, and other PRs that this commit closes or is related to.

For example:

```
BREAKING CHANGE: <breaking change summary>
<BLANK LINE>
<breaking change description + migration instructions>
<BLANK LINE>
<BLANK LINE>
Fixes #<issue number>
```

Breaking Change section should start with the phrase `BREAKING CHANGE: ` followed by a summary of
the breaking change, a blank line, and a detailed description of the breaking change that also
includes migration instructions.
