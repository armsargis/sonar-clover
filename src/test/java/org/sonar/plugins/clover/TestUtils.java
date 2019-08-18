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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

public class TestUtils {

    private TestUtils() {
    }

    public static File getResource(Class baseClass, String path) {
        String resourcePath = StringUtils.replaceChars(baseClass.getCanonicalName(), '.', '/');
        if (!path.startsWith("/")) {
            resourcePath += "/";
        }
        resourcePath += path;
        return getResource(resourcePath);
    }

    public static File getResource(String path) {
        String resourcePath = path;
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }

        URL url = TestUtils.class.getResource(resourcePath);
        if (url != null) {
            return FileUtils.toFile(url);
        }

        throw new IllegalArgumentException("Cannot resolve file for path: " + path);
    }
}
