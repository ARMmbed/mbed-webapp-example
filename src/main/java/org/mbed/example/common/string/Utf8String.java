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

package org.mbed.example.common.string;

import java.nio.charset.Charset;

/**
 * Created by mitvah01 on 12.8.2015.
 */
public class Utf8String {
    public static String from(byte[] bytes) {
        if(isAscii(bytes)) {
            return new String(bytes, Charset.forName("UTF-8"));
        } else {
            return "0x" + HexArray.toHex(bytes);
        }
    }

    public static String from(int... bytes) {
        byte[] byteArray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteArray[i] = (byte) bytes[i];
        }
        return from(byteArray);
    }

    private static boolean isAscii(byte[] bytes) {
        for (byte aByte : bytes) {
            if (aByte < 32 || aByte > 126) {
                return false;
            }
        }
        return true;
    }
}
