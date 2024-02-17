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

import org.eclipse.jkube.maven.plugin.mojo.build.HelmMojo;

import static org.eclipse.jkube.kit.resource.helm.HelmServiceUtil.initHelmConfig;

import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * This goal forks the install goal and then installs the generated Helm chart to the current cluster.
 *
 * Note that the goals k8s:resource, k8s:helm and k8s:build must be bound to the proper execution phases.
 */

@Mojo(name = "helm-install", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.VALIDATE)
@Execute(phase = LifecyclePhase.INSTALL)
public class HelmInstallMojo extends HelmMojo {

    @Parameter(property = "jkube.skip.deploy", defaultValue = "false")
    protected boolean skipDeploy;

    @Override
    protected boolean shouldSkip() {
        return super.shouldSkip() || skipDeploy;
    }
    
    @Override
    public void executeInternal() throws MojoExecutionException {
      if (shouldSkip()) {
        return;
      }
      try {
        helm = initHelmConfig(getDefaultHelmType(), javaProject, getKubernetesTemplate(), helm)
            .build();
        final String chartDirectory = Paths.get(helm.getOutputDir()).resolve(getDefaultHelmType().getOutputDir()).toAbsolutePath().toString();
        jkubeServiceHub.getHelmService().install(helm, chartDirectory);
      } catch (Exception exp) {
        getKitLogger().error("Error performing Helm install", exp);
        throw new MojoExecutionException(exp.getMessage(), exp);
      }
    }
}
