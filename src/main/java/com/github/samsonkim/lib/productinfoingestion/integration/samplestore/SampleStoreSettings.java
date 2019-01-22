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

import com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileColumn;
import com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileColumnType;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents the settings for Sample Store
 */
public class SampleStoreSettings {

    /**
     * Mock way to represent store id
     */
    public static final UUID STORE_ID = UUID.randomUUID();

    /**
     * Default Store Locale
     */
    public static final Locale DEFAULT_STORE_LOCALE = Locale.US;

    /**
     * Tax rate
     */
    public static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.07775);

    /**
     * Fixed width columns
     */
    public static final FixedWidthFileColumn FIXED_WIDTH_PRODUCT_ID_COLUMN = FixedWidthFileColumn.builder()
            .start(1)
            .end(8)
            .name("Product ID")
            .type(FixedWidthFileColumnType.NUMBER)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_PRODUCT_DESCRIPTION_COLUMN = FixedWidthFileColumn.builder()
            .start(10)
            .end(68)
            .name("Product Description")
            .type(FixedWidthFileColumnType.STRING)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_REGULAR_SINGULAR_PRICE_COLUMN = FixedWidthFileColumn.builder()
            .start(70)
            .end(77)
            .name("Regular Singular Price")
            .type(FixedWidthFileColumnType.CURRENCY)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_PROMOTIONAL_SINGULAR_PRICE_COLUMN = FixedWidthFileColumn.builder()
            .start(79)
            .end(86)
            .name("Promotional Singular Price")
            .type(FixedWidthFileColumnType.CURRENCY)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_REGULAR_SPLIT_PRICE_COLUMN = FixedWidthFileColumn.builder()
            .start(88)
            .end(95)
            .name("Regular Split Price")
            .type(FixedWidthFileColumnType.CURRENCY)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_PROMOTIONAL_SPLIT_PRICE_COLUMN = FixedWidthFileColumn.builder()
            .start(97)
            .end(104)
            .name("Promotional Split Price")
            .type(FixedWidthFileColumnType.CURRENCY)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_REGULAR_FOR_X_COLUMN = FixedWidthFileColumn.builder()
            .start(106)
            .end(113)
            .name("Regular For X")
            .type(FixedWidthFileColumnType.NUMBER)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_PROMOTIONAL_FOR_X_COLUMN = FixedWidthFileColumn.builder()
            .start(115)
            .end(122)
            .name("Promotional For X")
            .type(FixedWidthFileColumnType.NUMBER)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_FLAGS_COLUMN = FixedWidthFileColumn.builder()
            .start(124)
            .end(132)
            .name("Flags")
            .type(FixedWidthFileColumnType.FLAGS)
            .build();

    public static final FixedWidthFileColumn FIXED_WIDTH_PRODUCT_SIZE_COLUMN = FixedWidthFileColumn.builder()
            .start(134)
            .end(142)
            .name("Product Size")
            .type(FixedWidthFileColumnType.STRING)
            .build();

    /**
     * Fixed width flags
     */
    public static final int FIXED_WIDTH_PER_WEIGHT_ITEM_FLAG = 3;
    public static final int FIXED_WIDTH_TAXABLE_FLAG = 5;
}
