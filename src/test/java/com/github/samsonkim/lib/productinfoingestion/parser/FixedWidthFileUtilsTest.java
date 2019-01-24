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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for FixedWidthFileUtils class
 */
public class FixedWidthFileUtilsTest {



    @Test
    public void testGetSubStringFixedWidthColumn(){
        FixedWidthFileColumn column = FixedWidthFileColumn.builder()
                .start(1)
                .end(5)
                .build();

        String line = "hellobye";

        assertEquals(Optional.of("hello"),  FixedWidthFileUtils.getSubString(column, line));

        column.setEnd(line.length());
        assertEquals(Optional.of(line), FixedWidthFileUtils.getSubString(column, line));

        assertFalse(FixedWidthFileUtils.getSubString(column, null).isPresent());

        FixedWidthFileColumn zeroIndex = FixedWidthFileColumn.builder()
                .start(0)
                .end(5)
                .build();
        assertFalse(FixedWidthFileUtils.getSubString(zeroIndex, line).isPresent());

        FixedWidthFileColumn endIndexOutOfBounds = FixedWidthFileColumn.builder()
                .start(1)
                .end(line.length()+1)
                .build();
        assertFalse(FixedWidthFileUtils.getSubString(endIndexOutOfBounds, line).isPresent());
    }

    @Test
    public void testGetFlagValue(){
        assertEquals(Optional.of(true), FixedWidthFileUtils.getFlagValue(Arrays.asList(true, false), 1));
        assertEquals(Optional.of(false), FixedWidthFileUtils.getFlagValue(Arrays.asList(true, false), 2));
        assertEquals(Optional.empty(), FixedWidthFileUtils.getFlagValue(Arrays.asList(true, false), 3));
        assertEquals(Optional.empty(), FixedWidthFileUtils.getFlagValue(Collections.emptyList(), 1));
    }

    @Test
    public void testToBooleanListFixedWidthColumn(){
        FixedWidthFileColumn column = FixedWidthFileColumn.builder()
                .start(1)
                .end(2)
                .build();

        assertEquals(Arrays.asList(true, false),
                FixedWidthFileUtils.toBooleanList(column, "YN"));

        assertEquals(Collections.emptyList(),
                FixedWidthFileUtils.toBooleanList(column, ""));

        assertEquals(Collections.emptyList(),
                FixedWidthFileUtils.toBooleanList(column, "a"));

        FixedWidthFileColumn zeroColumn = FixedWidthFileColumn.builder()
                .start(0)
                .end(2)
                .build();
        assertEquals(Collections.emptyList(),
                FixedWidthFileUtils.toBooleanList(zeroColumn, "YN"));

        FixedWidthFileColumn outOfBoundsColumn = FixedWidthFileColumn.builder()
                .start(1)
                .end(3)
                .build();
        assertEquals(Collections.emptyList(),
                FixedWidthFileUtils.toBooleanList(outOfBoundsColumn, "YN"));
    }

    @Test
    public void testToBigDecimalFixedWidthColumn(){
        FixedWidthFileColumn column = FixedWidthFileColumn.builder()
                .start(1)
                .end(8)
                .build();

        assertEquals("3.0000",
                FixedWidthFileUtils.toBigDecimal(column, "00000300").get().toString());

        assertEquals("3.4900",
                FixedWidthFileUtils.toBigDecimal(column, "00000349").get().toString());

        assertEquals("-3.4900",
                FixedWidthFileUtils.toBigDecimal(column, "-0000349").get().toString());

        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(column, null));

        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(column, "a"));

        FixedWidthFileColumn zeroColumn = FixedWidthFileColumn.builder()
                .start(0)
                .end(2)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(zeroColumn, "00000300"));

        FixedWidthFileColumn outOfBoundsColumn = FixedWidthFileColumn.builder()
                .start(1)
                .end(9)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(outOfBoundsColumn, "00000300"));
    }

    @Test
    public void testToStringValFixedWidthColumn(){
        FixedWidthFileColumn column = FixedWidthFileColumn.builder()
                .start(1)
                .end(5)
                .build();

        assertEquals("hello",
                FixedWidthFileUtils.toStringVal(column, "hello").get());

        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(column, null));

        FixedWidthFileColumn zeroColumn = FixedWidthFileColumn.builder()
                .start(0)
                .end(2)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(zeroColumn, "hello"));

        FixedWidthFileColumn outOfBoundsColumn = FixedWidthFileColumn.builder()
                .start(1)
                .end(6)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toBigDecimal(outOfBoundsColumn, "hello"));
    }

    @Test
    public void testToIntegerFixedWidthColumn(){
        FixedWidthFileColumn column = FixedWidthFileColumn.builder()
                .start(1)
                .end(8)
                .build();

        assertEquals("300",
                FixedWidthFileUtils.toInteger(column, "00000300").get().toString());

        assertEquals("349",
                FixedWidthFileUtils.toInteger(column, "00000349").get().toString());

        assertEquals("-349",
                FixedWidthFileUtils.toInteger(column, "-0000349").get().toString());

        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toInteger(column, null));

        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toInteger(column, "a"));

        FixedWidthFileColumn zeroColumn = FixedWidthFileColumn.builder()
                .start(0)
                .end(2)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toInteger(zeroColumn, "00000300"));

        FixedWidthFileColumn outOfBoundsColumn = FixedWidthFileColumn.builder()
                .start(1)
                .end(9)
                .build();
        assertEquals(Optional.empty(),
                FixedWidthFileUtils.toInteger(outOfBoundsColumn, "00000300"));
    }




}
