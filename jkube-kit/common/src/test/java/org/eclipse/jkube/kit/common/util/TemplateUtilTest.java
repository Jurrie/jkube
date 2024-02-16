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

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jkube.kit.common.util.TemplateUtil.escapeYamlTemplate;
import static org.eclipse.jkube.kit.common.util.TemplateUtil.unescapeYamlTemplate;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TemplateUtilTest {

  public static Stream<Object[]> data() {
    return Stream.of(new Object[][] {
        // No Helm directive
        { "abcd", "abcd" },

        // When the Helm directive is not the first on the line
        { "abc{de}f}", "abc{de}f}" },
        { "abc{{de}f", "abc{{de}f" },
        { "abc{{$def}}", "abc{{$def}}" },
        { "abc{{de}}f", "abc{{de}}f" },
        { "abc{{de}f}}", "abc{{de}f}}" },
        { "abc{{def}}ghi{{jkl}}mno", "abc{{def}}ghi{{jkl}}mno" },

        // When the Helm directive is the first on the line
        { "{de}f}", "{de}f}" },
        { "{{de}f", "{{de}f" },
        { "{{$def}}", "escapedHelm0: " + Base64Util.encodeToString("{{$def}}") },
        { "{{de}}f", "escapedHelm0: " + Base64Util.encodeToString("{{de}}f") },
        { "{{de}f}}", "escapedHelm0: " + Base64Util.encodeToString("{{de}f}}") },
        { "{{def}}ghi{{jkl}}mno", "escapedHelm0: " + Base64Util.encodeToString("{{def}}ghi{{jkl}}mno") },
        { "hello\n{{def}}\nworld", "hello\nescapedHelm0: " + Base64Util.encodeToString("{{def}}") + "\nworld" },
        { "- hello\n- {{def}}\n- world", "- hello\n- escapedHelm0: " + Base64Util.encodeToString("{{def}}") + "\n- world" },

        // When the Helm directive is the first on the line, but indented
        { "  {de}f}", "  {de}f}" },
        { "  {{de}f", "  {{de}f" },
        { "  {{$def}}", "  escapedHelm0: " + Base64Util.encodeToString("{{$def}}") },
        { "  {{de}}f", "  escapedHelm0: " + Base64Util.encodeToString("{{de}}f") },
        { "  {{de}f}}", "  escapedHelm0: " + Base64Util.encodeToString("{{de}f}}") },
        { "  {{def}}ghi{{jkl}}mno", "  escapedHelm0: " + Base64Util.encodeToString("{{def}}ghi{{jkl}}mno") },
        { "hello:\n  {{def}}\n  world", "hello:\n  escapedHelm0: " + Base64Util.encodeToString("{{def}}") + "\n  world" },
        { "hello:\n  - {{def}}\n  - world",
            "hello:\n  - escapedHelm0: " + Base64Util.encodeToString("{{def}}") + "\n  - world" },

        // When the Helm directive is a value
        { "key: {de}f}", "key: {de}f}" },
        { "key: {{de}f", "key: {{de}f" },
        { "key: {{$def}}", "key: escapedHelmValue" + Base64Util.encodeToString("{{$def}}") },
        { "key: {{de}}f", "key: escapedHelmValue" + Base64Util.encodeToString("{{de}}f") },
        { "key: {{de}f}}", "key: escapedHelmValue" + Base64Util.encodeToString("{{de}f}}") },
        { "key: {{def}}ghi{{jkl}}mno", "key: escapedHelmValue" + Base64Util.encodeToString("{{def}}ghi{{jkl}}mno") },

        // When the Helm directive is a value, but indented
        { "  key: {de}f}", "  key: {de}f}" },
        { "  key: {{de}f", "  key: {{de}f" },
        { "  key: {{$def}}", "  key: escapedHelmValue" + Base64Util.encodeToString("{{$def}}") },
        { "  key: {{de}}f", "  key: escapedHelmValue" + Base64Util.encodeToString("{{de}}f") },
        { "  key: {{de}f}}", "  key: escapedHelmValue" + Base64Util.encodeToString("{{de}f}}") },
        { "  key: {{def}}ghi{{jkl}}mno", "  key: escapedHelmValue" + Base64Util.encodeToString("{{def}}ghi{{jkl}}mno") },
    });
  }

  @ParameterizedTest(name = "{0} is {1}")
  @MethodSource("data")
  void escapeYamlTemplateTest(String input, String expected) {
    final String escapedYaml = escapeYamlTemplate(input);
    assertThat(escapedYaml).isEqualTo(expected);

    final String unescapedYaml = unescapeYamlTemplate(escapedYaml);
    assertThat(input).isEqualTo(unescapedYaml);
  }
}