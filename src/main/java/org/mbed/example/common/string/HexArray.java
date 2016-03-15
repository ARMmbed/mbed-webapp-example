/*
 * Copyright (c) 2016, ARM Limited, All Rights Reserved
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

package org.mbed.example.common.string;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Utility class to convert byte array to string.
 */
public final class HexArray implements Serializable {

    private final static String HEX_DIGIT_STRING = "0123456789abcdef";
    private final static char[] HEX_DIGITS = HEX_DIGIT_STRING.toCharArray();
    private final byte[] data;

    /**
     * Converts byte array to hex string.
     *
     * @param data byte array data
     * @return hex string
     */
    public static String toHex(final byte[] data) {
        return data != null ? toHex(data, data.length) : null;
    }

    /**
     * Converts byte array to hex string.
     *
     * @param data byte array data
     * @param len byte array length
     * @return hex string
     */
    private static String toHex(final byte[] data, final int len) {
        final char[] retVal = new char[len * 2];
        int k = 0;
        for (int i = 0; i < len; i++) {
            retVal[k++] = HEX_DIGITS[(data[i] & 0xf0) >>> 4];
            retVal[k++] = HEX_DIGITS[data[i] & 0x0f];
        }
        return new String(retVal);
    }

    public HexArray(byte... data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return toHex(data, data.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HexArray other = (HexArray) obj;
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Arrays.hashCode(this.data);
        return hash;
    }

}
