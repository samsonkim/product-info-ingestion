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

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Collection of methods that convert field data types to java data types
 * Optional values are returned to allow caller to determine to set default value
 * or to throw exception
 */
public class FixedWidthFileUtils {

    /**
     * Converts zero left-padded string to integer
     *
     * @param value
     * @return
     */
    public static Optional<Integer> toInteger(String value) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .map(s -> Integer.parseInt(s));
    }

    /**
     * Spaces are trimmed both sides although spec lists string fields
     * being right-padded
     *
     * @param value
     * @return
     */
    public static Optional<String> toString(String value) {
        return Optional.ofNullable(value)
                .map(v -> StringUtils.trimToNull(v));
    }

    /**
     * Converts string to BigDecimal for currency file column type
     * with last 2 digits representing cents.
     * BigDecimal values are rounded to 4 decimal places half down
     *
     * @param value
     * @return
     */
    public static Optional<BigDecimal> toBigDecimal(String value) {
        return Optional.ofNullable(value)
                .map(StringBuilder::new)
                .map(s -> s.insert(s.length() - 2, "."))
                .map(StringBuilder::toString)
                .map(s -> new BigDecimal(s).setScale(4, RoundingMode.HALF_DOWN));
    }

    /**
     * Convert Y/N flag string to List&lt;Boolean&gt;
     *
     * @param value
     * @return
     */
    public static List<Boolean> toBooleanList(String value) {
        return Optional.ofNullable(value)
                .map(v -> v.chars()
                        .mapToObj(c -> {
                            if (c == 'Y') {
                                return true;
                            } else {
                                return false;
                            }
                        }).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }


    /**
     * Converts substring of column to Integer
     *
     * @param column
     * @param line
     * @return
     */
    public static Optional<Integer> toInteger(FixedWidthFileColumn column, String line) {
        return getSubString(column, line)
                .flatMap(s -> FixedWidthFileUtils.toInteger(s));
    }

    /**
     * Converts subtring of column to String
     *
     * @param column
     * @param line
     * @return
     */
    public static Optional<String> toStringVal(FixedWidthFileColumn column, String line) {
        return getSubString(column, line)
                .flatMap(s -> FixedWidthFileUtils.toString(s));
    }

    /**
     * Converts substring of column to BigDecimal
     *
     * @param column
     * @param line
     * @return
     */
    public static Optional<BigDecimal> toBigDecimal(FixedWidthFileColumn column, String line) {
        return getSubString(column, line)
                .flatMap(s -> FixedWidthFileUtils.toBigDecimal(s));
    }

    /**
     * Converts substring of column to Boolean list of (Y|N) values
     * returns empty list if it does not fit that pattern
     *
     * @param column
     * @param line
     * @return
     */
    public static List<Boolean> toBooleanList(FixedWidthFileColumn column, String line) {
        return getSubString(column, line)
                .map(s -> FixedWidthFileUtils.toBooleanList(s))
                .orElse(Collections.emptyList());
    }

    /**
     * Get flag value for list of flags.
     * Position is 1 or greater
     *
     * @param flags
     * @param position
     * @return
     */
    public static Optional<Boolean> getFlagValue(List<Boolean> flags, int position) {
        if(position < 1){
            return Optional.empty();
        }

        // Adjust position to account for 0 start index
        int adjustedPosition = position - 1;


        return Optional.ofNullable(flags)
                .flatMap(l -> l.stream().skip(adjustedPosition).findFirst());
    }

    /**
     * Returns substring defined within the start and end column attributes.
     * Start column index starts at 1 not 0
     * End column index is inclusive
     *
     * @param column
     * @param line
     * @return Substring of line.  Returns empty if column parameters are out of bounds
     */
    public static Optional<String> getSubString(FixedWidthFileColumn column, String line) {

        if (line == null
                || column.getStart() < 1
                || column.getEnd() > line.length()) {
            return Optional.empty();
        }

        return Optional.ofNullable(line.substring(column.getStart() - 1, column.getEnd()));
    }
}
