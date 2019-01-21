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

import com.github.samsonkim.lib.productinfoingestion.integration.StoreFactory;
import com.github.samsonkim.lib.productinfoingestion.model.ProductRecord;
import com.github.samsonkim.lib.productinfoingestion.parser.FileParser;
import com.github.samsonkim.lib.productinfoingestion.parser.FileParserLineMapper;
import com.github.samsonkim.lib.productinfoingestion.writer.JacksonJsonWriter;
import com.github.samsonkim.lib.productinfoingestion.writer.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Command line application to demonstrate parsing a file to a Collection of ProductRecords
 */
public class ProductInfoIngestionApp {

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.err.println("Filename is required");
            System.exit(1);
        }

        String fileName = args[0];

        ProductInfoIngestionApp app = new ProductInfoIngestionApp();
        app.run(fileName);
    }

    public void run(String fileName) throws IOException {
        JsonWriter jsonWriter = new JacksonJsonWriter();

        /*
        TODO storeName, json file handling
         */

        String storeName = "sample";
        FileParserLineMapper fileParserLineMapper = StoreFactory.getFileParserLineMapper(storeName);

        FileParser<ProductRecord> fileParser = new FileParser<>();

        InputStream inputStream = new FileInputStream(new File(fileName));
        List<ProductRecord> productRecords = fileParser.parse(inputStream, fileParserLineMapper);

        String json = jsonWriter.writeValueAsString(productRecords);

        try (PrintWriter out = new PrintWriter("sample.json")) {
            out.println(json);
        }

        System.out.println(json);
    }
}
