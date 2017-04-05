/*
 * Copyright 2017, OnGres.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package com.ongres.scram.common.util;


import com.ongres.scram.common.GS2AttributeValue;
import com.ongres.scram.common.GS2Attributes;
import com.ongres.scram.common.SCRAMAttributeValue;
import com.ongres.scram.common.SCRAMAttributes;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CharAttributeValueCSVTest {
    private static final String[] ONE_ARG_VALUES = new String[] { "c=channel", "i=4096", "a=authzid", "n" };
    private static final String SEVERAL_VALUES_STRING = "n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL";

    @Test
    public void writeToNullOrEmpty() {
        assertTrue(CharAttributeValueCSV.writeTo(new StringBuffer()).length() == 0);
        assertTrue(CharAttributeValueCSV.writeTo(new StringBuffer(), new CharAttributeValue[]{}).length() == 0);
    }

    @Test
    public void writeToOneArg() {
        CharAttributeValue[] pairs = new CharAttributeValue[] {
                new SCRAMAttributeValue(SCRAMAttributes.CHANNEL_BINDING, "channel"),
                new SCRAMAttributeValue(SCRAMAttributes.ITERATION, "" + 4096),
                new GS2AttributeValue(GS2Attributes.AUTHZID, "authzid"),
                new GS2AttributeValue(GS2Attributes.CLIENT_NOT, null)
        };

        for(int i = 0; i < pairs.length; i++) {
            assertEquals(ONE_ARG_VALUES[i], CharAttributeValueCSV.writeTo(new StringBuffer(), pairs[i]).toString());
        }
    }

    @Test
    public void writeToSeveralArgs() {
        assertEquals(
                SEVERAL_VALUES_STRING,
                CharAttributeValueCSV.writeTo(
                        new StringBuffer(),
                        new GS2AttributeValue(GS2Attributes.CLIENT_NOT, null),
                        null,
                        new SCRAMAttributeValue(SCRAMAttributes.USERNAME, "user"),
                        new SCRAMAttributeValue(SCRAMAttributes.NONCE, "fyko+d2lbbFgONRv9qkxdawL")

                ).toString()
        );
    }

    @Test
    public void parseFromEmpty() {
        assertArrayEquals(new String[]{}, CharAttributeValueCSV.parseFrom(""));
    }

    @Test
    public void parseFromOneArgWithLimitsOffsets() {
        for(String s : ONE_ARG_VALUES) {
            assertArrayEquals(new String[] {s}, CharAttributeValueCSV.parseFrom(s));
        }

        int[] numberEntries = new int[] { 0, 1 };
        for(int n : numberEntries) {
            for(String s : ONE_ARG_VALUES) {
                assertArrayEquals(new String[] {s}, CharAttributeValueCSV.parseFrom(s, n));
            }
        }
        for(String s : ONE_ARG_VALUES) {
            assertArrayEquals(new String[] {s, null, null}, CharAttributeValueCSV.parseFrom(s, 3));
        }

        for(int n : numberEntries) {
            for(String s : ONE_ARG_VALUES) {
                assertArrayEquals(new String[] {s}, CharAttributeValueCSV.parseFrom(s, n, 0));
            }
        }
        for(String s : ONE_ARG_VALUES) {
            assertArrayEquals(new String[] {s, null, null}, CharAttributeValueCSV.parseFrom(s, 3, 0));
        }

        for(int n : numberEntries) {
            for(String s : ONE_ARG_VALUES) {
                assertArrayEquals(new String[] { null }, CharAttributeValueCSV.parseFrom(s, n, 1));
            }
        }
    }

    @Test
    public void parseFromSeveralArgsWithLimitsOffsets() {
        assertArrayEquals(
                new String[] { "n", "", "n=user", "r=fyko+d2lbbFgONRv9qkxdawL" },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING)
        );

        assertArrayEquals(
                new String[] { "n", "" },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING, 2)
        );

        assertArrayEquals(
                new String[] { "", "n=user" },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING, 2, 1)
        );

        assertArrayEquals(
                new String[] { "r=fyko+d2lbbFgONRv9qkxdawL", null },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING, 2, 3)
        );

        assertArrayEquals(
                new String[] { null, null },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING, 2, 4)
        );

        assertArrayEquals(
                new String[] { "n", "", "n=user", "r=fyko+d2lbbFgONRv9qkxdawL", null },
                CharAttributeValueCSV.parseFrom(SEVERAL_VALUES_STRING, 5)
        );
    }
}
