/*
 * Copyright (c) 2019 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at:
 *
 *     https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.jkube.kit.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateUtil {

  private TemplateUtil() {
  }

  public static String escapeYamlTemplate(String template) {
    template = escapeYamlTemplateValues(template);
    template = escapeYamlTemplateLines(template);
    return template;
  }

  public static String unescapeYamlTemplate(String template) {
    template = unescapeYamlTemplateValues(template);
    template = unescapeYamlTemplateLines(template);
    return template;
  }

  private static String escapeYamlTemplateLines(String template) {
    long escapedYamlIndex = 0;
    final Pattern compile = Pattern.compile("^( *-? *)(\\{\\{.*\\}\\}.*)$", Pattern.MULTILINE);
    Matcher matcher = compile.matcher(template);
    while (matcher.find()) {
      final String indentation = matcher.group(1);
      final String base64Line = Base64Util.encodeToString(matcher.group(2));
      template = matcher.replaceFirst(indentation + "escapedHelm" + escapedYamlIndex + ": " + base64Line);
      matcher = compile.matcher(template);
      escapedYamlIndex++;
    }
    return template;
  }

  private static String unescapeYamlTemplateLines(String template) {
    final Pattern compile = Pattern.compile("^( *-? *)escapedHelm[\\d]+: \"?(.*?)\"?$", Pattern.MULTILINE);
    Matcher matcher = compile.matcher(template);
    while (matcher.find()) {
      final String indentation = matcher.group(1);
      final String helmLine = Base64Util.decodeToString(matcher.group(2));
      template = matcher.replaceFirst(indentation + helmLine.replace("$", "\\$"));
      matcher = compile.matcher(template);
    }
    return template;
  }

  private static String escapeYamlTemplateValues(String template) {
    final Pattern compile = Pattern.compile("^( *[^ ]+ *): *(\\{\\{.*\\}\\}.*)$", Pattern.MULTILINE);
    Matcher matcher = compile.matcher(template);
    while (matcher.find()) {
      final String indentation = matcher.group(1);
      final String base64Value = Base64Util.encodeToString(matcher.group(2));
      template = matcher.replaceFirst(indentation + ": escapedHelmValue" + base64Value);
      matcher = compile.matcher(template);
    }
    return template;
  }

  private static String unescapeYamlTemplateValues(String template) {
    final Pattern compile = Pattern.compile("^( *[^ ]+ *): *\"?escapedHelmValue(.*?)\"?$", Pattern.MULTILINE);
    Matcher matcher = compile.matcher(template);
    while (matcher.find()) {
      final String indentation = matcher.group(1);
      final String helmValue = Base64Util.decodeToString(matcher.group(2));
      template = matcher.replaceFirst(indentation + ": " + helmValue.replace("$", "\\$"));
      matcher = compile.matcher(template);
    }
    return template;
  }
}
