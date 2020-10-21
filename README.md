# Maven Reactor Plugin

This is a Maven plugin which can be used to dump the details of the Maven reactor.

## Installation

Add the following to your root `pom.xml`:
```xml
<plugins>
  ...
  <plugin>
    <groupId>com.starlingbank</groupId>
    <artifactId>reactor-maven-plugin</artifactId>
    <version>1.0</version>
  </plugin>
  ...
</plugins>
```

## Dumper

It is occasionally useful to be able to get some information about the order in which Maven will
process modules in a multi-module build. This plugin will dump one of the following to STDOUT:

- Flat List: a list of artifact IDs in the order they are processed in the reactor (first to last)
- Upstream: the same as the flat list but with the addition of information of the projects that the
  given project depends on
- Downstream: the same as Upstream but listing the projects that depend on the specified project

### Configuration

- `direction`: the direction to trace dependencies; either `up`, `down` or `flat` (default: `flat`)
- `format`: the format of the output; either `text` or `dot` (default: `text`) 
- `transitive`: whether to include all transitive dependencies; either `true` or `false` (default: `false`)

### Examples

Dump the reactor list with default options (no dependencies as a text list to STDOUT):
```
mvn -q reactor:dump 
```

Dump the reactor list in [DOT] format to STDOUT: 
```
mvn -q reactor:dump -Ddirection=down -Dformat=dot
```

Dump the reactor list in [DOT] format, showing upstream dependencies to STDOUT:
```
mvn -q reactor:dump -Ddirection=up -Dformat=dot
```

## Building from source

Clone the repository:
```
git clone git@github.com:starlingbank/reactor-maven-plugin.git
```

Install to your local m2 repository (you can then use the x.y-SNAPSHOT version locally):
```
mvn clean install
```

Commits to this project will automatically be staged in maven central as snapshots. We use Github
Actions to do this (see `.github/workflows/package.yml` and `.github/workflows.release.yml`).

To release to production, push to the origin to the release branch:
```
git push origin main:release
```
The maven release plugin will then stage and deploy a new versioned release, tagging the git repo
with the version of the release.

You should then either rebase onto main or merge the changes, deleting the original `release` branch
in the process.

[DOT]: https://en.wikipedia.org/wiki/DOT_(graph_description_language)