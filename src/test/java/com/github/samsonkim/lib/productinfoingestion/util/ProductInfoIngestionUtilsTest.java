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

package com.github.samsonkim.lib.productinfoingestion.util;

import io.vavr.Tuple2;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.calculateSplitPricing;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.determinePricing;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.hasValue;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.toBigDecimal;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.toBooleanList;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.toInteger;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.toStringVal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductInfoIngestionUtilsTest {
    private NumberFormat currencyFormatter;

    @Before
    public void setUp() throws Exception {
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Test
    public void testHasValue() {
        assertTrue(hasValue(BigDecimal.valueOf(1)));

        assertFalse(hasValue(BigDecimal.ZERO));

        assertFalse(hasValue(BigDecimal.valueOf(-1)));

        assertFalse(hasValue(null));
    }

    @Test
    public void testCalculateSplitPricing() {

        Optional<Tuple2<String, BigDecimal>> splitPricing = calculateSplitPricing(currencyFormatter,
                BigDecimal.valueOf(10), Optional.of(2));

        assertTrue(splitPricing.isPresent());
        assertEquals("2 for $10.00", splitPricing.map(Tuple2::_1).get());
        assertEquals("5.0000", splitPricing.map(Tuple2::_2).get().toString());

        splitPricing = calculateSplitPricing(currencyFormatter,
                BigDecimal.ZERO, Optional.of(2));
        assertFalse(splitPricing.isPresent());

        splitPricing = calculateSplitPricing(currencyFormatter,
                null, Optional.of(2));
        assertFalse(splitPricing.isPresent());

        splitPricing = calculateSplitPricing(currencyFormatter,
                BigDecimal.valueOf(10), Optional.empty());
        assertFalse(splitPricing.isPresent());
    }

    @Test
    public void testDeterminePricing() {

        Optional<Tuple2<String, BigDecimal>> pricingTuple = determinePricing(currencyFormatter,
                Optional.of(BigDecimal.valueOf(10)),
                Optional.empty(),
                Optional.empty());

        assertTrue(pricingTuple.isPresent());
        assertEquals("$10.00", pricingTuple.get()._1);
        assertTrue(BigDecimal.valueOf(10).compareTo(pricingTuple.get()._2) == 0);

        pricingTuple = determinePricing(currencyFormatter,
                Optional.empty(),
                Optional.of(BigDecimal.valueOf(10)),
                Optional.of(2));

        assertTrue(pricingTuple.isPresent());
        assertEquals("2 for $10.00", pricingTuple.get()._1);
        assertTrue(BigDecimal.valueOf(5).compareTo(pricingTuple.get()._2) == 0);

        pricingTuple = determinePricing(currencyFormatter,
                Optional.empty(),
                Optional.of(BigDecimal.valueOf(10)),
                Optional.of(0));

        assertFalse(pricingTuple.isPresent());
    }

    @Test
    public void testToInteger() {
        assertEquals(Optional.of(567), toInteger("00000567"));

        assertEquals(Optional.of(0), toInteger("00000000"));

        assertEquals(Optional.of(11111111), toInteger("11111111"));

        assertFalse(toInteger(null).isPresent());
    }

    @Test
    public void testToString() {
        assertEquals(Optional.of("abc"), toStringVal("abc"));

        assertEquals(Optional.of("abc"), toStringVal("abc    "));

        assertEquals(Optional.of("abc"), toStringVal("  abc    "));

        assertEquals(Optional.of("abc"), toStringVal("  abc"));

        assertFalse(toStringVal(null).isPresent());
    }

    /**
     * Test fixed width columns that map to BigDecimal datatype rounded to 4 decimal places, half down
     */
    @Test
    public void testToBigDecimal() {
        assertEquals(Optional.of("5.6700"), toBigDecimal("00000567").map(BigDecimal::toString));

        assertEquals(Optional.of("0.6700"), toBigDecimal("00000067").map(BigDecimal::toString));

        assertEquals(Optional.of("1.0000"), toBigDecimal("00000100").map(BigDecimal::toString));

        assertEquals(Optional.of("-5.6700"), toBigDecimal("-0000567").map(BigDecimal::toString));

        assertFalse(toBigDecimal(null).isPresent());
    }

    @Test
    public void testToBooleanList() {
        assertEquals(Arrays.asList(false, true),
                toBooleanList("NY"));

        assertEquals(Arrays.asList(false, false, true, false, false, false, false, false, false),
                toBooleanList("NNYNNNNNN"));

        assertEquals(Arrays.asList(false, false, false, false, false, false, false, false, false),
                toBooleanList("NNNNNNNNN"));

        assertTrue(toBooleanList(null).isEmpty());
    }
}
