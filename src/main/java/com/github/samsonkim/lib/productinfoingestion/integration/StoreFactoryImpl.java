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

package com.github.samsonkim.lib.productinfoingestion.integration;

import com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreFixedWidthFileProductRecordMapper;
import com.github.samsonkim.lib.productinfoingestion.integration.samplestore.SampleStoreSettings;
import com.github.samsonkim.lib.productinfoingestion.parser.FileParser;
import com.github.samsonkim.lib.productinfoingestion.parser.FileParserImpl;

import java.util.UUID;

/**
 * Resolves store specific integrations
 */
public class StoreFactoryImpl implements StoreFactory{

    /**
     * Returns store specific FileParser instance
     *
     * @param storeId
     * @param storeJournalId
     * @return
     */
    public FileParser getFileParser(UUID storeId, UUID storeJournalId)
    {
        if (SampleStoreSettings.STORE_ID.equals(storeId)) {

            SampleStoreFixedWidthFileProductRecordMapper mapper =
                    new SampleStoreFixedWidthFileProductRecordMapper(storeId, storeJournalId);

            return new FileParserImpl<>(mapper);
        }
        throw new IllegalArgumentException(String.format("Invalid store: %s", storeId));
    }
}
