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

package com.github.samsonkim.lib.productinfoingestion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.samsonkim.lib.productinfoingestion.dao.Persistable;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents ProductRecord model for Product Information Ingestion Library
 *
 * Lombok @Data is used to generate Constructor and Getter/Setters
 * Lombok @Builder allows for builder design pattern to be employed
 */
@Builder
@Data
public class ProductRecord implements Persistable {

    /**
     * Id used for Product Catalog Integration service
     */
    @JsonProperty
    private UUID id;

    /**
     * Store id
     */
    @JsonProperty
    private UUID storeId;

    /**
     * Store Journal Id
     */
    @JsonProperty
    private UUID storeJournalId;

    /**
     * External Store product id
     */
    @JsonProperty
    private Integer productID;

    @JsonProperty
    private String productDescription;

    @JsonProperty
    private String regularDisplayPrice;

    @JsonProperty
    private BigDecimal regularCalculatorPrice;

    @JsonProperty
    private Optional<String> promotionalDisplayPrice;

    @JsonProperty
    private Optional<BigDecimal> promotionalCalculatorPrice;

    @JsonProperty
    private UnitOfMeasure unitOfMeasure;

    @JsonProperty
    private Optional<String> productSize;

    @JsonProperty
    private Optional<BigDecimal> taxRate;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdDateTime;

    @JsonProperty
    private String createdBy;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant modifiedDateTime;

    @JsonProperty
    private String modifiedBy;
}