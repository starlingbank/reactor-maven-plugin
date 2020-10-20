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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "dump", inheritByDefault = false, requiresProject = false)
public class ReactorDumperMojo extends AbstractMojo {

  @Parameter(defaultValue = "${session}", readonly = true)
  MavenSession mavenSession;

  @Parameter(defaultValue = "false", property = "transitive")
  Boolean transitive;

  @Parameter(defaultValue = "flat", property = "direction")
  String direction;

  @Parameter(defaultValue = "text", property = "format")
  String format;

  public void execute() throws MojoExecutionException {
    try {
      final Format actualFormat = getFormat();
      if (actualFormat == Format.DOT && transitive) {
        // We will not collect transitive dependencies when the type is DOT since this is a graph
        // format and transitive dependencies are catered for automatically in that format.
        getLog().warn("It is unnecessary to specify transitive when the format is DOT. Option ignored.");
        transitive = false;
      }

      final String result = new ReactorDumper().dump(mavenSession, transitive, getDirection(), actualFormat);

      System.out.println(result);
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to dump reactor", e);
    }
  }

  private Direction getDirection() {
    try {
      return Direction.valueOf(direction.toUpperCase());
    } catch (NullPointerException | IllegalArgumentException e) {
      getLog().error("Direction \"" + direction + "\" is not a valid direction");
      throw new IllegalArgumentException(e);
    }
  }

  private Format getFormat() {
    try {
      return Format.valueOf(format.toUpperCase());
    } catch (NullPointerException | IllegalArgumentException e) {
      getLog().error("Format \"" + direction + "\" is not a valid direction");
      throw new IllegalArgumentException(e);
    }
  }
}
