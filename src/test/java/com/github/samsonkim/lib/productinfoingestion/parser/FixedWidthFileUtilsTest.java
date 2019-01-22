/*
 * MIT License
 *
 * Copyright (c) 2019 samsonkim
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.samsonkim.lib.productinfoingestion.parser;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for FixedWidthFileUtils class
 */
public class FixedWidthFileUtilsTest {

    @Test
    public void testToInteger() {
        assertEquals(Optional.of(567), FixedWidthFileUtils.toInteger("00000567"));

        assertEquals(Optional.of(0), FixedWidthFileUtils.toInteger("00000000"));

        assertEquals(Optional.of(11111111), FixedWidthFileUtils.toInteger("11111111"));

        assertFalse(FixedWidthFileUtils.toInteger(null).isPresent());
    }

    @Test
    public void testToString() {
        assertEquals(Optional.of("abc"), FixedWidthFileUtils.toString("abc"));

        assertEquals(Optional.of("abc"), FixedWidthFileUtils.toString("abc    "));

        assertEquals(Optional.of("abc"), FixedWidthFileUtils.toString("  abc    "));

        assertEquals(Optional.of("abc"), FixedWidthFileUtils.toString("  abc"));

        assertFalse(FixedWidthFileUtils.toString(null).isPresent());
    }

    /**
     * Test fixed width columns that map to BigDecimal datatype rounded to 4 decimal places, half down
     */
    @Test
    public void testToBigDecimal() {
        assertEquals(Optional.of("5.6700"), FixedWidthFileUtils.toBigDecimal("00000567").map(BigDecimal::toString));

        assertEquals(Optional.of("0.6700"), FixedWidthFileUtils.toBigDecimal("00000067").map(BigDecimal::toString));

        assertEquals(Optional.of("1.0000"), FixedWidthFileUtils.toBigDecimal("00000100").map(BigDecimal::toString));

        assertEquals(Optional.of("-5.6700"), FixedWidthFileUtils.toBigDecimal("-0000567").map(BigDecimal::toString));

        assertFalse(FixedWidthFileUtils.toBigDecimal(null).isPresent());
    }

    @Test
    public void testToBooleanList(){
        assertEquals(Arrays.asList(false, true),
                FixedWidthFileUtils.toBooleanList("NY"));

        assertEquals(Arrays.asList(false, false, true, false, false, false, false, false, false),
                FixedWidthFileUtils.toBooleanList("NNYNNNNNN"));

        assertEquals(Arrays.asList(false, false, false, false, false, false, false, false, false),
                FixedWidthFileUtils.toBooleanList("NNNNNNNNN"));

        assertTrue(FixedWidthFileUtils.toBooleanList(null).isEmpty());
    }
}
