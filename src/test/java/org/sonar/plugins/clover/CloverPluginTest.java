/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.clover;

import org.junit.Test;
import org.sonar.api.Plugin.Context;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.internal.PluginContextImpl.Builder;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.fest.assertions.Assertions.assertThat;

public class CloverPluginTest {

    @Test
    public void test_getExtensions() {
        MapSettings settings = new MapSettings().setProperty("foo", "bar");
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(
                Version.create(7, 9), SonarQubeSide.SCANNER, SonarEdition.COMMUNITY
        );

        Context context = new Builder()
                .setSonarRuntime(runtime)
                .setBootConfiguration(settings.asConfig())
                .build();

        CloverPlugin cloverPlugin = new CloverPlugin();
        cloverPlugin.define(context);
        assertThat(context.getExtensions()).hasSize(1);
    }
}
