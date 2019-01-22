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
import com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileColumn;
import com.github.samsonkim.lib.productinfoingestion.parser.FixedWidthFileUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
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

/**
 * Mapper class to map a file line to ProductRecord
 */
public class SampleStoreFixedWidthFileProductRecordMapper
        implements FileParserLineMapper<ProductRecord> {

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

        //Get pricing data
        Optional<Tuple4<String,
                BigDecimal,
                Optional<String>,
                Optional<BigDecimal>>> pricingTupleOpt = getPricing(line);

        //Skip record if pricing data is not found
        if (!pricingTupleOpt.isPresent()) {
            return Optional.empty();
        }

        Tuple4<String,
                BigDecimal,
                Optional<String>,
                Optional<BigDecimal>> pricingTuple = pricingTupleOpt.get();

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

        Integer productId = toInt(FIXED_WIDTH_PRODUCT_ID_COLUMN, line);
        String productDescription = toString(FIXED_WIDTH_PRODUCT_DESCRIPTION_COLUMN, line)
                .orElse(null);

        Optional<String> productSize = toString(FIXED_WIDTH_PRODUCT_SIZE_COLUMN, line);

        Instant now = Instant.now();

        return Optional.of(ProductRecord.builder()
                .storeId(storeId)
                .storeJournalId(storeJournalId)
                .productID(productId)
                .productDescription(productDescription)
                .regularDisplayPrice(pricingTuple._1)
                .regularCalculatorPrice(pricingTuple._2)
                .promotionalDisplayPrice(pricingTuple._3)
                .promotionalCalculatorPrice(pricingTuple._4)
                .unitOfMeasure(unitOfMeasure)
                .productSize(productSize)
                .taxRate(taxRate)
                .createdDateTime(now)
                .modifiedDateTime(now)
                .locale(locale)
                .build());
    }

    /**
     * Determines whether to use singular or split pricing.
     *
     * @param line
     * @return Optional Pricing tuple representing regular display price, regular calculator price
     * promotional display price, promotional calculator price.  Empty tuple is returned if no pricing
     * is defined
     */
    protected Optional<Tuple4<String, BigDecimal, Optional<String>, Optional<BigDecimal>>> getPricing(String line) {

        Optional<BigDecimal> regularSingularPrice = toBigDecimal(FIXED_WIDTH_REGULAR_SINGULAR_PRICE_COLUMN, line);
        Optional<BigDecimal> promotionalSingularPrice = toBigDecimal(FIXED_WIDTH_PROMOTIONAL_SINGULAR_PRICE_COLUMN, line);

        Optional<BigDecimal> regularSplitPrice = toBigDecimal(FIXED_WIDTH_REGULAR_SPLIT_PRICE_COLUMN, line);
        Optional<BigDecimal> promotionalSplitPrice = toBigDecimal(FIXED_WIDTH_PROMOTIONAL_SPLIT_PRICE_COLUMN, line);

        //Check if we have pricing data
        Optional<Boolean> useSingularPricing = useSingularPricing(regularSingularPrice,
                promotionalSingularPrice,
                regularSplitPrice,
                promotionalSplitPrice);

        if (useSingularPricing.isPresent()) {
            if (useSingularPricing.get()) {
                //Singular price
                String regularDisplaySingularPrice = currencyFormatter.format(regularSingularPrice.get());

                Optional<String> promotionalDisplaySingularPrice =
                        promotionalSingularPrice.map(x -> currencyFormatter.format(x));

                return Optional.of(Tuple.of(regularDisplaySingularPrice, regularSingularPrice.get(),
                        promotionalDisplaySingularPrice, promotionalSingularPrice));
            } else {
                //Split price
                int regularForX = toInt(FIXED_WIDTH_REGULAR_FOR_X_COLUMN, line);
                int promotionalForX = toInt(FIXED_WIDTH_PROMOTIONAL_FOR_X_COLUMN, line);

                Tuple2<String, BigDecimal> regularSplitPriceTuple =
                        calculateSplitPricing(regularSplitPrice.orElse(BigDecimal.ZERO), regularForX);

                Tuple2<String, BigDecimal> promotionalSplitPriceTuple =
                        calculateSplitPricing(promotionalSplitPrice.orElse(BigDecimal.ZERO), promotionalForX);

                return Optional.of(Tuple.of(regularSplitPriceTuple._1,
                        regularSplitPriceTuple._2,
                        Optional.ofNullable(promotionalSplitPriceTuple._1),
                        Optional.ofNullable(promotionalSplitPriceTuple._2)));
            }
        }

        //No pricing data is available
        return Optional.empty();
    }

    /**
     * Function to build displaySplitPrice and calculatedSplitPrice
     *
     * @param price
     * @param forX
     * @return Tuple of displaySplitPrice and calculatedSplitPrice
     */
    protected Tuple2<String, BigDecimal> calculateSplitPricing(BigDecimal price, int forX) {
        return Optional.ofNullable(forX)
                .filter(x -> x > 0)
                .map(x -> {
                    String displayPrice = String.format("%s for %s", x, currencyFormatter.format(price));
                    BigDecimal calculatedPrice = price.divide(BigDecimal.valueOf(x), 4, RoundingMode.HALF_DOWN);
                    return Tuple.of(displayPrice, calculatedPrice);
                })
                .orElse(Tuple.of(currencyFormatter.format(BigDecimal.ZERO),
                        BigDecimal.ZERO.setScale(4, RoundingMode.HALF_DOWN)));
    }

    /**
     * Determine whether or not to use singular pricing.
     * False indicates split pricing
     * Empty result indicates no pricing exists
     * Note: Promotional pricing takes precedence
     *
     * @param regularSingularPrice
     * @param promotionalSingularPrice
     * @param regularSplitPrice
     * @param promotionalSplitPrice
     * @return
     */
    protected static Optional<Boolean> useSingularPricing(Optional<BigDecimal> regularSingularPrice,
                                                          Optional<BigDecimal> promotionalSingularPrice,
                                                          Optional<BigDecimal> regularSplitPrice,
                                                          Optional<BigDecimal> promotionalSplitPrice) {

        Boolean hasRegularSingularPrice = hasValue(regularSingularPrice);
        Boolean hasPromotionalSingularPrice = hasValue(promotionalSingularPrice);
        Boolean hasRegularSplitPrice = hasValue(regularSplitPrice);
        Boolean hasPromotionalSplitPrice = hasValue(promotionalSplitPrice);

        if (hasPromotionalSingularPrice) {
            return Optional.of(Boolean.TRUE);
        }

        if (hasPromotionalSplitPrice) {
            return Optional.of(Boolean.FALSE);
        }

        if (hasRegularSingularPrice) {
            return Optional.of(Boolean.TRUE);
        }

        if (hasRegularSplitPrice) {
            return Optional.of(Boolean.FALSE);
        }

        return Optional.empty();
    }

    protected static Boolean hasValue(Optional<BigDecimal> price) {
        return price.filter(p -> p.compareTo(BigDecimal.ZERO) > 0)
                .map(p -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }

    //TODO add tests for below this
    protected int toInt(FixedWidthFileColumn column, String line) {
        return FixedWidthFileUtils.toInteger(getSubString(column, line))
                .orElse(0);
    }

    protected Optional<String> toString(FixedWidthFileColumn column, String line) {
        return FixedWidthFileUtils.toString(getSubString(column, line));
    }

    protected Optional<BigDecimal> toBigDecimal(FixedWidthFileColumn column, String line) {
        return FixedWidthFileUtils.toBigDecimal(getSubString(column, line));
    }

    protected List<Boolean> toBooleanList(FixedWidthFileColumn column, String line) {
        return FixedWidthFileUtils.toBooleanList(getSubString(column, line));
    }

    protected Optional<Boolean> getFlagValue(List<Boolean> flags, int position) {
        // Adjust position to account for 0 start index
        if (position < flags.size()) {
            return Optional.of(flags.get(position - 1));
        }
        return Optional.empty();
    }

    //Needs to be inclusive
    private String getSubString(FixedWidthFileColumn column, String line) {
        return line.substring(column.getStart() - 1, column.getEnd());
    }
}
