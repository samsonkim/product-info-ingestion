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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductCatalogIntegrationServiceImplTest {

    @Mock
    private StoreFactory storeFactory;

    @Mock
    private FileParser fileParser;

    private ProductCatalogIntegrationServiceImpl instance = null;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        instance = new ProductCatalogIntegrationServiceImpl(storeFactory);
    }

    @After
    public void tearDown() throws Exception {
        reset(storeFactory, fileParser);
    }

    @Test
    public void testIngestProductCatalog() throws ProductInfoIngestionException, IOException {

        UUID storeId = UUID.randomUUID();
        String fileName = "src/test/resources/test-sample.txt";

        ProductRecord productRecord = ProductRecord.builder()
                .build();

        List<ProductRecord> list = Arrays.asList(productRecord);

        when(fileParser.parse(any(InputStream.class)))
                .thenReturn(list);

        when(storeFactory.getFileParser(any(UUID.class), any(UUID.class)))
            .thenReturn(fileParser);

        List<ProductRecord> response = instance.ingestProductCatalog(storeId, fileName);

        assertEquals(list, response);

        verify(storeFactory).getFileParser(any(UUID.class), any(UUID.class));
        verify(fileParser).parse(any(InputStream.class));
    }

    @Test(expected = ProductInfoIngestionException.class)
    public void testIngestProductCatalogProductInfoIngestionException() throws ProductInfoIngestionException, IOException {

        UUID storeId = UUID.randomUUID();
        String fileName = "unknown";

        when(storeFactory.getFileParser(any(UUID.class), any(UUID.class)))
                .thenReturn(fileParser);

        instance.ingestProductCatalog(storeId, fileName);

        verify(storeFactory).getFileParser(any(UUID.class), any(UUID.class));
    }
}
