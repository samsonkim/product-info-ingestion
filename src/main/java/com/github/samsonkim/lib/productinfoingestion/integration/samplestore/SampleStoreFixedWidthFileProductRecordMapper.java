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
import com.github.samsonkim.lib.productinfoingestion.parser.FileParserLineMapper;
import io.vavr.Tuple2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_FLAGS_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PER_WEIGHT_ITEM_FLAG;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PRODUCT_DESCRIPTION_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PRODUCT_ID_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PRODUCT_SIZE_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PROMOTIONAL_FOR_X_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PROMOTIONAL_SINGULAR_PRICE_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_PROMOTIONAL_SPLIT_PRICE_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_REGULAR_FOR_X_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_REGULAR_SINGULAR_PRICE_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_REGULAR_SPLIT_PRICE_COLUMN;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.FIXED_WIDTH_TAXABLE_FLAG;
import static com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings.TAX_RATE;
import static com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils.getFlagValue;
import static com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils.toBigDecimal;
import static com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils.toBooleanList;
import static com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils.toInteger;
import static com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils.toStringVal;
import static com.github.samsonkim.lib.productinfoingestion.util.ProductInfoIngestionUtils.determinePricing;

/**
 * Mapper class to map a file line to ProductRecord
 */
public class SampleStoreFixedWidthFileProductRecordMapper
        implements FileParserLineMapper<ProductRecord> {

    private static BigDecimal ZERO_VALUE = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_DOWN);

    private final NumberFormat currencyFormatter;
    private final Locale locale;
    private final UUID storeId;
    private final UUID storeJournalId;

    public SampleStoreFixedWidthFileProductRecordMapper(UUID storeId, UUID storeJournalId) {
        this.storeId = storeId;
        this.storeJournalId = storeJournalId;
        this.locale = SampleStoreSettings.DEFAULT_STORE_LOCALE;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(locale);
    }

    /**
     * Maps file line to Product Record.
     * Returns Empty if no pricing information is available
     *
     * @param line
     * @return ProductRecord object
     */
    @Override
    public Optional<ProductRecord> map(String line) {

        Optional<BigDecimal> regularSingularPrice = toBigDecimal(FIXED_WIDTH_REGULAR_SINGULAR_PRICE_COLUMN, line);
        Optional<BigDecimal> regularSplitPrice = toBigDecimal(FIXED_WIDTH_REGULAR_SPLIT_PRICE_COLUMN, line);
        Optional<Integer> regularForX = toInteger(FIXED_WIDTH_REGULAR_FOR_X_COLUMN, line);

        Optional<BigDecimal> promotionalSingularPrice = toBigDecimal(FIXED_WIDTH_PROMOTIONAL_SINGULAR_PRICE_COLUMN, line);
        Optional<BigDecimal> promotionalSplitPrice = toBigDecimal(FIXED_WIDTH_PROMOTIONAL_SPLIT_PRICE_COLUMN, line);
        Optional<Integer> promotionalForX = toInteger(FIXED_WIDTH_PROMOTIONAL_FOR_X_COLUMN, line);

        Optional<Tuple2<String, BigDecimal>> regularPricing = determinePricing(currencyFormatter, regularSingularPrice, regularSplitPrice, regularForX);
        Optional<Tuple2<String, BigDecimal>> promotionalPricing = determinePricing(currencyFormatter, promotionalSingularPrice, promotionalSplitPrice, promotionalForX);

        //Skip record if pricing data is not found
        if (!regularPricing.isPresent() && !promotionalPricing.isPresent()) {
            return Optional.empty();
        }

        String regularDisplayPrice = regularPricing.map(Tuple2::_1).orElse(currencyFormatter.format(ZERO_VALUE));
        BigDecimal regularCalculatorPrice = regularPricing.map(Tuple2::_2).orElse(ZERO_VALUE);
        String promotionalDisplayPrice = promotionalPricing.map(Tuple2::_1).orElse(currencyFormatter.format(ZERO_VALUE));
        BigDecimal promotionalCalculatorPrice = promotionalPricing.map(Tuple2::_2).orElse(ZERO_VALUE);

        //Get flags
        List<Boolean> flags = toBooleanList(FIXED_WIDTH_FLAGS_COLUMN, line);

        //Per Weight flag determines unit of measure
        UnitOfMeasure unitOfMeasure = getFlagValue(flags, FIXED_WIDTH_PER_WEIGHT_ITEM_FLAG)
                .filter(f -> f.equals(true))
                .map(f -> UnitOfMeasure.POUND)
                .orElse(UnitOfMeasure.EACH);

        //Tax rate flag determines tax
        Optional<BigDecimal> taxRate = getFlagValue(flags, FIXED_WIDTH_TAXABLE_FLAG)
                .filter(f -> f.equals(true))
                .map(f -> TAX_RATE);

        Integer productId = toInteger(FIXED_WIDTH_PRODUCT_ID_COLUMN, line)
                .orElse(null);
        String productDescription = toStringVal(FIXED_WIDTH_PRODUCT_DESCRIPTION_COLUMN, line)
                .orElse(null);

        Optional<String> productSize = toStringVal(FIXED_WIDTH_PRODUCT_SIZE_COLUMN, line);

        return Optional.of(ProductRecord.builder()
                .storeId(storeId)
                .storeJournalId(storeJournalId)
                .productID(productId)
                .productDescription(productDescription)
                .regularDisplayPrice(regularDisplayPrice)
                .regularCalculatorPrice(regularCalculatorPrice)
                .promotionalDisplayPrice(promotionalDisplayPrice)
                .promotionalCalculatorPrice(promotionalCalculatorPrice)
                .unitOfMeasure(unitOfMeasure)
                .productSize(productSize)
                .taxRate(taxRate)
                .locale(locale)
                .build());
    }
}
