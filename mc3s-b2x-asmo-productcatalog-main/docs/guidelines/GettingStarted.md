# Getting Started

* Clone the repository.
* Run `./mvnw git-code-format:format-code clean install` in the project root.
* Import as Maven project into the IDE of your choice.

# Unit & Integration Test Execution Using Maven Plugins

- The Failsafe Plugin is designed to run integration tests while the Surefire Plugin is designed to
  run unit tests
- Use following commands for test execution
- `./mvnw test` & `./mvnw verify` - Runs unit test only
- `./mvnw verify -Pintegration-tests` - Runs unit & integration tests

## Mutation tests

Mutation test are disabled by default as they consume too much time if executed with every build
.They can be enabled by environment variable pitest.execution.skip

```shell
./mvwn clean install -Dpitest.execution.skip=false
```

## Build

To be able to download artifacts from B2X Artifactory on S3, you need to have a valid token , passed
from AWS Login. Same as for local startuo in env file.

:warning: the token has a limited validity time.

Copy and paste the credentials into your shell and start build.

### macos

```shell
export AWS_REGION=eu-central-1
export AWS_ACCESS_KEY_ID=<copy>
export AWS_SECRET_ACCESS_KEY=<copy>
export AWS_SESSION_TOKEN=<copy>

./mvwn clean install
```

### windows

use powershell option

```shell
$Env:AWS_REGION="eu-central-1"
$Env:AWS_ACCESS_KEY_ID=<copy>
$Env:AWS_SECRET_ACCESS_KEY=<copy>
$Env:AWS_SESSION_TOKEN=<copy>

./mvwn clean install
```

## Run Configuration

This is how we create IntelliJ IDEA Run Configuration:

- Goto `Run` →  `Edit Configurations` → Enter the details specified below

![runconfig](images/RunInIntelliJ.png)

- Name - Name of Application which is to be configured. Example: **ordermanagement**
- Java version - select the java 11.
- Module - select module which you need to run. Example: **mc3s-app-aggregator-ordermanagement**
- Main class - Specify the fully qualified name of the class to be executed:
  com.mindcurv.b2x.Mc3sRunnerApp
- VM Options - Specify the Vm option to **-Dspring.profiles.active=local**
- Program arguments - No need to set anything.
- Working directory - Path of the directory that contains your Project.
- Environment variables - Modify/Add the below environment variable.
  To generate the AWS keys, please refer
  to [Setup MACH B2B aggregators in Local](https://mindcurv.atlassian.net/wiki/spaces/MC3S/pages/3008102575/Setup+MACH+B2B+aggregators+in+Local#Step-3.2%3A-AWS-Token)
    - SecretsKey=mc3s-ct-dev,mc3s-default-dev;
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY
    - AWS_SESSION_TOKEN
- Save and Run the project.

## PR Process

This is how we work with Pull Requests (PRs) on Bitbucket:

- Make sure
    - to run and validate the unit tests locally – they must succeed.
    - to validate the Bitbucket job for your branch – it must be stable (green).
    - that your unit test coverage is 100% / stays on current level
    - that unit test mutation test score is at same level or higher. 100% for new code
- Raise PR for each sub-task.
    - If a ticket is complex, then it is recommended to split the ticket into sub-tasks and raise PR
      for each sub-tasks. Once the PRs for all sub-tasks are merged, the scope of the ticket should
      be completed and closed.
- Assign at least 2 approvers on your PR (you may assign random approvers, or pick the most
  appropriate ones).
- Add all Developers on the team as reviewers on your PR (so not necessarily as Approvers, just as
  Reviewers).
- Make sure the technical lead is one of the approvers (as of June 2021, that is Dirk).
- Add your own name as an Approver in the case PR is not ready to be merged, but needs to be
  reviewed.
- As the Author of the PR, after a review, review the feedback and address all tasks.
- As soon as a task is reflected on the PR, mark it as done.
- Once all specified approvers have approved, and all tasks (on the Bitbucket PR) have been
  resolved (and marked as such) the PR may be merged (by anyone).
- The feature/fix branch should be closed when the PR is merged.
- Go through
  the [Mindcurv Developer Guide](https://mindcurv.atlassian.net/wiki/spaces/WOW/pages/838303888/Mindcurv+Developer+Guide#MindcurvDeveloperGuide-_executive_summaryExecutiveSummary).
  This guide explains what is generally expected from Mindcurv Developers.

## JIRA

- [Jira](https://mindcurv.atlassian.net/secure/RapidBoard.jspa?rapidView=357&selectedIssue=MC3SB2BX-3492&assignee=557058%3A39266b32-be28-47ff-aba9-abc1be0d0e93)

## Development - Practice

- [Code QA](https://mindcurv.atlassian.net/wiki/spaces/WOW/pages/2788339414/Code+QA)
- [Development Quality Standard](https://mindcurv.atlassian.net/wiki/spaces/WOW/pages/1321436007/Development+Quality+Standard)
- [Mindcurv Java Coding Guide](https://mindcurv.atlassian.net/wiki/spaces/WOW/pages/2873786654/Mindcurv+Java+Coding+Guide)

# Postman Collection

The [postman collection](mc3s-b2x-ordermanagement.postman_collection.json) contains the related APIS
and examples. 