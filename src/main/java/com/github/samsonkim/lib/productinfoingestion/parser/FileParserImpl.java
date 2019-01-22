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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File parser that returns a List&lt;T&gt; based on mapper supplied.
 * Supports fixed width or delimited file formats based on mapper.
 *
 * @param <T>
 */
public class FileParserImpl<T> implements FileParser<T>{
    private final FileParserLineMapper<T> lineMapper;

    public FileParserImpl(FileParserLineMapper<T> lineMapper) {
        this.lineMapper = lineMapper;
    }

    /**
     * Converts an InputStream to List&lt;T&gt;
     * Parser continues processing even if there are rows that cannot be mapped to &lt;T&gt;
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public List<T> parse(InputStream inputStream) throws IOException {
        //using try-with-resources which ensures that resources will be closed after execution of the program
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
            return stream.map(l -> lineMapper.map(l))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }
}
