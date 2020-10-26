/*
 * The MIT License
 * Copyright © 2020 Starling Bank Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.starlingbank;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReactorDumper {

  public String dump(final MavenSession session, final boolean transitive, final Direction direction, final Format format) {
    final StringBuilder builder = new StringBuilder();

    if (format == Format.DOT) {
      builder.append("digraph maven_dep {\n");
    }

    final Stream<MavenProject> projectStream = session.getProjects().stream();
    builder.append(dump(projectStream, session.getProjectDependencyGraph(), format != Format.DOT && transitive, direction, format).collect(Collectors.joining()));

    if (format == Format.DOT) {
      builder.append("}\n");
    }

    return builder.toString();
  }

  private Stream<String> dump(final Stream<MavenProject> projectStream, final ProjectDependencyGraph projectDependencyGraph, final boolean transitive, final Direction direction, final Format format) {
    switch (direction) {
      case UP:
        return projectStream.map(p -> dumpUp(p, projectDependencyGraph, transitive, format));

      case DOWN:
        return projectStream.map(p -> dumpDown(p, projectDependencyGraph, transitive, format));

      case FLAT:
      default:
        return projectStream.map(p -> trimId(p) + "\n");
    }
 }

  private String dumpUp(final MavenProject project, final ProjectDependencyGraph projectDependencyGraph, final boolean transitive, final Format format) {
    return dumpDependencies(project, projectDependencyGraph.getUpstreamProjects(project, transitive), format);
  }

  private String dumpDown(final MavenProject project, final ProjectDependencyGraph projectDependencyGraph, final boolean transitive, final Format format) {
    return dumpDependencies(project, projectDependencyGraph.getDownstreamProjects(project, transitive), format);
  }

  private String dumpDependencies(final MavenProject project, final List<MavenProject> dependentProjects, final Format format) {
    switch (format) {
      case DOT:
        return dependentProjects.stream().map(p -> "  " + trimId(project, true) + " -> " + trimId(p, true) + ";\n").collect(Collectors.joining());

      case TEXT:
      default:
        return trimId(project) + " => " + dependentProjects.stream().map(this::trimId).collect(Collectors.joining(",")) + "\n";
    }
  }

  private String trimId(final MavenProject project) {
    return trimId(project, false);
  }

  private String trimId(final MavenProject project, final boolean replaceDotsAndHyphens) {
    final String trim = project.getArtifactId().trim();
    if (replaceDotsAndHyphens) {
      return trim.replaceAll("[\\.-]", "_");
    }

    return trim;
  }
}
