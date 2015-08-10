/*
 * Copyright (c) 2015, ARM Limited, All Rights Reserved
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbed.example.data;

/**
 * @author szymon
 */
public final class ResourcePath {
    private final String endpointName;
    private final String path;

    public static ResourcePath of(String endpointName, String path) {
        return new ResourcePath(endpointName, path);
    }

    public ResourcePath(String endpointName, String path) {
        if (endpointName == null || path == null) {
            throw new NullPointerException();
        }
        this.endpointName = endpointName;
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourcePath that = (ResourcePath) o;

        if (!endpointName.equals(that.endpointName)) {
            return false;
        }
        return path.equals(that.path);

    }

    @Override
    public int hashCode() {
        int result = endpointName.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("[endpointName='%s', path='%s']", endpointName, path);
    }
}
