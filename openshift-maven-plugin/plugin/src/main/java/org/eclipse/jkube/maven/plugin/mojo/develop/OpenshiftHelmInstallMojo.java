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
package org.eclipse.jkube.maven.plugin.mojo.develop;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.jkube.kit.resource.helm.HelmConfig;
import org.eclipse.jkube.maven.plugin.mojo.OpenShift;

import java.io.File;

/**
 * This goal forks the install goal and then installs the generated Helm chart to the current cluster.
 *
 * Note that the goals oc:resource, oc:helm and oc:build must be bound to the proper execution phases.
 */
@Mojo(name = "helm-install", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.VALIDATE)
@Execute(phase = LifecyclePhase.INSTALL)
public class OpenshiftHelmInstallMojo extends HelmInstallMojo {

  /**
   * The generated kubernetes YAML file
   */
  @Parameter(property = "jkube.kubernetesManifest", defaultValue = "${basedir}/target/classes/META-INF/jkube/openshift.yml")
  private File openShiftManifest;

  /**
   * The generated kubernetes YAML file
   */
  @Parameter(property = "jkube.kubernetesManifest", defaultValue = "${basedir}/target/classes/META-INF/jkube/openshift")
  private File openShiftTemplate;

  @Override
  protected File getKubernetesManifest() {
    return openShiftManifest;
  }

  @Override
  protected File getKubernetesTemplate() {
    return openShiftTemplate;
  }

  @Override
  protected HelmConfig.HelmType getDefaultHelmType() {
    return HelmConfig.HelmType.OPENSHIFT;
  }

  @Override
  protected String getLogPrefix() {
    return OpenShift.DEFAULT_LOG_PREFIX;
  }
}
