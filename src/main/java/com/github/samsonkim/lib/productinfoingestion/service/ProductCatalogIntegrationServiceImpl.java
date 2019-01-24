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

package com.github.samsonkim.lib.productinfoingestion.service;

import com.github.samsonkim.lib.productinfoingestion.exception.ProductInfoIngestionException;
import com.github.samsonkim.lib.productinfoingestion.integration.StoreFactory;
import com.github.samsonkim.lib.productinfoingestion.model.ProductRecord;
import com.github.samsonkim.lib.productinfoingestion.parser.FileParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class implementation responsible for ingesting store product catalogs
 */
public class ProductCatalogIntegrationServiceImpl implements ProductCatalogIntegrationService {
    private final StoreFactory storeFactory;

    public ProductCatalogIntegrationServiceImpl(StoreFactory storeFactory) {
        this.storeFactory = storeFactory;
    }

    /**
     * Ingests store product catalog into the product info ingestion service
     *
     * @param storeId
     * @param fileName
     * @param user
     * @return
     * @throws ProductInfoIngestionException
     */
    @Override
    public List<ProductRecord> ingestProductCatalog(UUID storeId,
                                                    String fileName,
                                                    String user) throws ProductInfoIngestionException {

        //This will be retrieved from DB system that records this info
        UUID storeJournalId = UUID.randomUUID();

        FileParser<ProductRecord> fileParser =
                storeFactory.getFileParser(storeId, storeJournalId);

        try {
            InputStream inputStream = new FileInputStream(new File(fileName));
            List<ProductRecord> productRecords = fileParser.parse(inputStream);

            /*
             * Add logic to persist to db
             * - generates id, and other audit attributes (createdBy, createdDateTime, modifiedBy, modifiedDateTime)
             *
             *  Simulate DB interaction
             */
            Instant now = Instant.now();
            productRecords.forEach(p -> {
                p.setId(Optional.of(UUID.randomUUID()));
                p.setCreatedBy(user);
                p.setCreatedDateTime(now);
            });

            return productRecords;
        } catch (IOException e) {
            throw new ProductInfoIngestionException(
                    String.format("Unable to ingest. storeId=%s, fileName=%s", storeId, fileName), e);
        }
    }
}
