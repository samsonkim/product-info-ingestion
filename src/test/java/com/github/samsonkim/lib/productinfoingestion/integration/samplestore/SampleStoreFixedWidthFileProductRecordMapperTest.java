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

package com.github.samsonkim.lib.productinfoingestion.integration.samplestore;

import com.github.samsonkim.lib.productinfoingestion.model.ProductRecord;
import com.github.samsonkim.lib.productinfoingestion.model.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SampleStoreFixedWidthFileProductRecordMapperTest {

    private SampleStoreFixedWidthFileProductRecordMapper instance;

    private UUID storeId;

    @Before
    public void setUp() throws Exception {
        storeId = UUID.randomUUID();
        instance = new SampleStoreFixedWidthFileProductRecordMapper(storeId);
    }

    @Test
    public void testMapProductRecord() {
        String line = "80000001 Kimchi-flavored white rice                                  00000567 00000000 00000000 00000000 00000000 00000000 NNNNNNNNN      18oz";

        ProductRecord productRecord = instance.map(line);

        assertNotNull(productRecord);

        assertNotNull(productRecord.getId());
        assertEquals(storeId, productRecord.getStoreId());
        assertNotNull(productRecord.getCreatedDateTime());
        assertNotNull(productRecord.getModifiedDateTime());
        assertEquals(80000001, productRecord.getProductID().intValue());
        assertEquals("Kimchi-flavored white rice", productRecord.getProductDescription());

        assertEquals("$5.67", productRecord.getRegularDisplayPrice());
        assertEquals("5.6700", productRecord.getRegularCalculatorPrice().toString());

        assertEquals(Optional.of("$0.00"), productRecord.getPromotionalDisplayPrice());
        assertEquals("0.0000", productRecord.getPromotionalCalculatorPrice().get().toString());

        assertEquals(UnitOfMeasure.EACH, productRecord.getUnitOfMeasure());

        //TODO verify product size column is formatted correctly
        assertEquals("     18oz", productRecord.getProductSize().get());
        assertFalse(productRecord.getTaxRate().isPresent());
    }

    @Test
    public void testMapProductRecordTaxRate() {
        String line = "14963801 Generic Soda 12-pack                                        00000000 00000549 00001300 00000000 00000002 00000000 NNNNYNNNN   12x12oz";

        ProductRecord productRecord = instance.map(line);

        assertNotNull(productRecord);
        assertEquals(Optional.of(BigDecimal.valueOf(.07775)), productRecord.getTaxRate());
    }

    @Test
    public void testMapProductRecordUnitOfMeasureWeight() {
        String line = "50133333 Fuji Apples (Organic)                                       00000349 00000000 00000000 00000000 00000000 00000000 NNYNNNNNN        lb";

        ProductRecord productRecord = instance.map(line);

        assertNotNull(productRecord);
        assertEquals(UnitOfMeasure.POUND, productRecord.getUnitOfMeasure());
    }

    @Test
    public void testMapProductRecordPromotionalSingularPrices() {
        String line = "40123401 Marlboro Cigarettes                                         00001000 00000549 00000000 00000000 00000000 00000000 YNNNNNNNN          ";

        ProductRecord productRecord = instance.map(line);

        assertNotNull(productRecord);

        assertEquals("$10.00", productRecord.getRegularDisplayPrice());
        assertEquals("10.0000", productRecord.getRegularCalculatorPrice().toString());

        assertEquals(Optional.of("$5.49"), productRecord.getPromotionalDisplayPrice());
        assertEquals("5.4900", productRecord.getPromotionalCalculatorPrice().get().toString());
    }

    @Test
    public void testMapProductRecordSplitPrices() {
        String line = "14963801 Generic Soda 12-pack                                        00000000 00000549 00001300 00001800 00000002 00000003 NNNNYNNNN   12x12oz";

        ProductRecord productRecord = instance.map(line);

        assertNotNull(productRecord);

        assertEquals("2 for $13.00", productRecord.getRegularDisplayPrice());
        assertEquals("6.5000", productRecord.getRegularCalculatorPrice().toString());

        assertEquals(Optional.of("3 for $18.00"), productRecord.getPromotionalDisplayPrice());
        assertEquals("6.0000", productRecord.getPromotionalCalculatorPrice().get().toString());
    }
}
