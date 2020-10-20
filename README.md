# Maven Reactor Plugin

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

[DOT]: https://en.wikipedia.org/wiki/DOT_(graph_description_language)