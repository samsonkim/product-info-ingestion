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

import com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreFixedWidthFileProductRecordMapper;
import com.github.samsonkim.lib.productinfoingestion.model.ProductRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileParserImplTest {

    private FileParserImpl<ProductRecord> instance;

    @Before
    public void setUp() throws Exception {
        UUID storeId = UUID.randomUUID();
        UUID storeJournalId = UUID.randomUUID();
        SampleStoreFixedWidthFileProductRecordMapper mapper = new SampleStoreFixedWidthFileProductRecordMapper(storeId, storeJournalId);
        instance = new FileParserImpl<>(mapper);
    }

    /**
     * One record should be skipped due to no pricing data
     *
     * @throws IOException
     */
    @Test
    public void testParseProductRecord() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("test-sample.txt");

        List<ProductRecord> results = instance.parse(inputStream);

        assertNotNull(results);
        assertEquals(5, results.size());
    }
}
