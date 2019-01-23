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

package com.github.samsonkim.lib.productinfoingestion;

import com.github.samsonkim.lib.productinfoingestion.exception.ProductInfoIngestionException;
import com.github.samsonkim.lib.productinfoingestion.integration.StoreFactoryImpl;
import com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings;
import com.github.samsonkim.lib.productinfoingestion.model.ProductRecord;
import com.github.samsonkim.lib.productinfoingestion.service.ProductCatalogIntegrationService;
import com.github.samsonkim.lib.productinfoingestion.service.ProductCatalogIntegrationServiceImpl;
import com.github.samsonkim.lib.productinfoingestion.writer.JacksonJsonWriter;
import com.github.samsonkim.lib.productinfoingestion.writer.JsonWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Command line application to demonstrate parsing a file to a Collection of ProductRecords
 */
public class ProductInfoIngestionApp {

    public static void main(String[] args) throws ProductInfoIngestionException, IOException {
        if (args.length == 0 ||
                Optional.of(args[0])
                        .filter(s -> s.length() > 0)
                        .equals(Optional.empty())) {
            System.err.println("Filename is required");
            System.exit(1);
        }

        String fileName = args[0];

        ProductInfoIngestionApp app = new ProductInfoIngestionApp();
        app.run(fileName);
    }

    /**
     * Main entry point of application.  Input filename needs "sample" to resolve to
     * sample store integration
     *
     * @param fileName
     * @throws IOException
     * @throws ProductInfoIngestionException
     */
    public void run(String fileName) throws IOException, ProductInfoIngestionException {
        JsonWriter jsonWriter = new JacksonJsonWriter();

        UUID storeId = null;
        String jsonFileName = null;

        if (fileName.contains("sample")) {
            storeId = SampleStoreSettings.STORE_ID;
            jsonFileName = "sample.json";
        }

        ProductCatalogIntegrationService productCatalogIntegrationService =
                new ProductCatalogIntegrationServiceImpl(new StoreFactoryImpl());
        List<ProductRecord> productRecords = productCatalogIntegrationService.ingestProductCatalog(storeId, fileName);

        String json = jsonWriter.writeValueAsString(productRecords);
        try (PrintWriter out = new PrintWriter(jsonFileName)) {
            out.println(json);
        }

        System.out.println(String.format("Processed %s records", productRecords.size()));
    }
}
