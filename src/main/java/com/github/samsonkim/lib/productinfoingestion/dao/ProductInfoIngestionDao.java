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

package com.github.samsonkim.lib.productinfoingestion.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Main interface for all dao's of product-info-ingestion library
 *
 * @param <T>
 */
public interface ProductInfoIngestionDao<T> {

    /**
     * Get [T] by id
     *
     * @param uuid
     * @return
     */
    Optional<T> get(UUID uuid);

    /**
     * Insert [T]
     *
     * @param t
     * @return
     */
    T insert(T t);

    /**
     * Update [T]
     *
     * @param t
     * @return
     */
    T update(T t);


    /**
     * Save (insert/update) [T]
     *
     * @param t
     * @return
     */
    T save(T t);

    /**
     * Delete [T]
     *
     * @param uuid
     */
    void delete(UUID uuid);

    /**
     * Get List[T] based on queryParams
     *
     * @param queryParams
     * @return
     */
    List<T> find(Map<String, String> queryParams);
}
