# Build process

## Pipelines

bitbucket is currently the only supported flavour

## Versioning

The version is concatenated from build.properties + pipeline execution number and used as version
tag.

## Code Compliance

the pipeline executes different tests as part of maven build and scripts

### Line count for module

### License check

The allowed licenses are part of global
settings [whitelisted-licenses.xml](../../scripts/whitelisted-licenses.xml). The file is available
in every module.

### OWASP

### Maven dependency tree

### PMD checks

PMD checks are part of standard maven build process. the rules are globally configured in
file [pmd-ruleset.xml](../../templates/pmd-ruleset.xml). The file is available in every module.

### Gitleaks

#### How to fix gitleaks secrets

Suppose if gitleaks runs successfully in the PR pipeline and fails in the master pipeline, check for
the following

1. the commit is not related to the file that contains secrets.
2. the same set of secrets are repeatedly caught by gitleaks with different commit ids.

If the above two conditions are true, then the issue can be resolved by performing a repo cleanup.

#### Repo Cleanup Procedure

Prefer a dedicated tool like BFG Repo Cleaner for the
process https://rtyley.github.io/bfg-repo-cleaner/

Before performing the cleanup make sure:

1. No one is currently working on that repository
2. Remove any remote branches that exist
3. Make sure you have the latest version of that repository

Now delete the commits associated with the files that are getting detected in the master pipeline by
using the command provided by bfg

java -jar bfg.jar --delete-files <file-names> <repo-path>

Then force push the changes to master by running the command

git push origin --force

Make sure to re-clone the repository again after this is done.

# Contract generation

- the download is part of build.sh
- It requires a token for the swagger user
- com.mindcurv.b2x.aws.core.cognito.app.GetToken in cloud module is used to fetch it. Run
  configuration is present. you need to pass the username for the swagger user as argument. This
  will create/ update tokens.properties file in idea folder (next to env file)
- This file is read during build.sh execution and used as bearer token


