# Spring Application Template

This project serves as template for new spring applications. It follows a modular monolith architecture enforced with
the spring modulith package. It currently has 3 modules: appusers (for user related stuff), notes (for note related
stuff) and shared (for common stuff like configurations). Modules can interact with each other either by direct method
calls or by events, either way these interactions are all described in `SomeModuleManagement` classes (
ex: `AppuserManagement` and `NoteManagement`). The spring modulith package enforces the modular monolith architecture
with tests - during the context tests a verification of the architecture will be performed. For more information on the
architecture click on this [link](https://spring.io/projects/spring-modulith).

## Authentication

This template has 2 different type of authentication: endpoints starting with `/app` use basic authentication (username
and password) while endpoints starting with `/api` use bearer token authentication (JWT).

For the basic authentication I have implemented custom UserDetails, UserDetailsService and GrantedAuthority classes.
This classes can be updated to accommodate different needs. For password encoding this template uses BCrypt.

For the bearer token authentication I have created a custom token converter in order to convert the principal (which by
default is an object of type `JWT`) to an object of type `AppuserPrincipal`. This authentication type requires a public
and private secrets/keys in order to decode and encode the tokens. I have already generated the keys which are located
in **resources/certificates**. To generate new keys follow these steps:

```
# create rsa key pair
openssl genrsa -out keypair.pem 2048

# extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# create private key in PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

## GitHub's configuration, actions and applications

This GitHub repository is a template repository. It is intended to be used as a start of point for new projects. As such
it has some minimal configurations that can be extended further if needed.

### GitHub configuration

#### Merge rules

To protect both the main branch and the git history a couple of rules are in place:

1. Only squash merges are allowed to the main branch
2. Before merging changes to the main branch a pull request must be opened
3. Before merging changes to the main branch all status checks must pass.

#### Dependabot

To help with keeping the application dependencies always up to date, this repository has dependabot configured to, on a
weekly basis open PRs with version updates for both the maven and actions dependencies used.

### GitHub actions - CI CD Workflow

This workflow is made of 3 different steps and its intent is to build and test your changes and then to build a docker
image with the resulting JAR file and push it to docker hub, finally it will deploy your image.

#### Build and Test Application

This job is the **FIRST** to run on the workflow.

It sets up temurin JDK and maven and then runs the command "mvn clean verify". This command will compile the code, test
it, package it in a JAR file and run linting analysis performed by sonar. It then uploads the resulting JAR file, so it
can be used in other jobs.

#### Build and Push Image

This job is dependent on [Build and Test Application](#build-and-test-application).

It downloads the JAR file artifact and build a docker image with it. After building the image it pushes it to the docker
registry. This step is configured to only actually push the image to the registry when it is running on main branch to
avoid cluttering the registry.

#### Deploy Application

This job is dependent on [Build and Push Image](#build-and-push-image).

In this step we should make the necessary changes to deploy the application to wherever it should be deployed.

### GitHub applications - Semantic PR

It verifies your Pull Request title follows the conventional commit guidelines. For more information on the rules being
enforced, take a look at
the [angular commit message guidelines](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines).

- feat: A new feature
- fix: A bug fix
- docs: Documentation only changes
- style: Changes that do not affect the meaning of the code (white-space, formatting, missing semicolons, etc...)
- refactor: A code change that neither fixes a bug nor adds a feature
- perf: A code change that improves performance
- test: Adding missing tests or correcting existing tests
- build: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
- ci: Changes to our CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs)
- chore: Changes that don't modify the source code directly but are important for maintaining the project
- revert: Revert existing code

### GitHub applications - SonarCloud Code Analysis

This application will look at your linting results from Sonar Cloud and block any merge that introduces new issues, be
it bugs, vulnerabilities, technical debt, decreased coverage or an increased code duplication.
