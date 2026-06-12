# java-pipeline

## Product Catalog Pipeline Test

Use these steps to try the product catalog pipeline in GitLab.

1. Open the GitLab project.

2. Switch to this branch:

```text
product-catalog-pipeline-test
```

3. Open this file and confirm the runner tag is correct:

```text
mc3s-b2x-asmo-productcatalog-main/.gitlab-ci.yml
```

The default runner tag is:

```yaml
tags:
  - asmo
```

If the GitLab runner uses another tag, replace `asmo` with the correct runner tag.

4. Go to `Settings > CI/CD > Variables`.

5. Add Maven Nexus variables. From the Nexus repositories page, use the `maven-public` group repository:

```text
NEXUS_MAVEN_URL=https://nexus.apps.asmonpeclr.np.asmo.com/repository/maven-public/
NEXUS_USERNAME=<username>
NEXUS_PASSWORD=<password>
```

6. For the first test, focus on the Maven build. Docker/image variables can be added later.

7. Go to `Build > Pipelines > Run pipeline`.

8. Select this branch and run the pipeline:

```text
product-catalog-pipeline-test
```

9. Check these stages:

```text
git_clone
unit_test
build_maven
build_image
```

The key stage for the first validation is:

```text
build_maven
```

If `build_maven` passes, the Maven pipeline is working.

10. If `build_image` fails because registry variables are missing, add image variables later.

The screenshot currently shows Maven and NuGet repositories only. It does not show a Docker hosted/group repository. For image publishing, either enable GitLab Container Registry or create a Nexus Docker hosted repository first.

If a Nexus Docker repository is created, add:

```text
NEXUS_DOCKER_IMAGE=nexus.apps.asmonpeclr.np.asmo.com/<docker-repo-name>/mc3s-b2x-asmo-productcatalog
NEXUS_USERNAME=<username>
NEXUS_PASSWORD=<password>
```

For the first test, it is OK if `build_image` does not run or fails due to missing registry settings. Confirm `build_maven` first.

Expected fix from this pipeline:

```text
/.m2 Permission denied
```

This error should not appear anymore because Maven now uses `$CI_PROJECT_DIR/.m2`.

If this error appears:

```text
could not lock config file .../.git/config
```

Check GitLab Runner/OpenShift workspace permissions. That issue is runner/OCP setup, not the Maven pipeline code.

If this error appears:

```text
Unable to lock database: Permission denied
Failed to open apk database: Permission denied
```

The runner container is running as a non-root user. Do not run `apk add` or other package installation commands in the job. Use images that already contain the needed tools, or keep the job to simple shell checks.

If this error appears during `Getting source from Git repository`:

```text
could not lock config file /builds/.../.gitconfig: No such file or directory
```

Do not set `HOME: "$CI_PROJECT_DIR"` in global CI variables. GitLab Runner performs the clone before the project directory exists. Set `HOME` only in `before_script`, after checkout, for Maven jobs.



## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

- [ ] [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [ ] [Add files using the command line](https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://scm.waleed.org.sa/devsecops/shared-libraries/java-pipeline.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](https://scm.waleed.org.sa/devsecops/shared-libraries/java-pipeline/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Set auto-merge](https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/index.html)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
