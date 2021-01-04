package com.klasdq.demo.persistence.mybatis;

import java.util.Collection;

/**
 * <p>批量保存对象类</p>
 * <p>可以是执行新增或更新，但所有对象的执行方式必须一致</p>
 */
public class BatchSaveParameter<T> {
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final Collection<T> data;
    private final int batchSize;

    private BatchSaveParameter(Collection<T> data, int batchSize) {
        this.data = data;
        this.batchSize = batchSize;
    }

    public static <T> BatchSaveParameter<T> of(Collection<T> data) {
        return of(data, DEFAULT_BATCH_SIZE);
    }

    public static <T> BatchSaveParameter<T> of(Collection<T> data, int batchSize) {
        if (batchSize < 10){ batchSize = 10; }
        else if (batchSize > 500){ batchSize = 500;}
        return new BatchSaveParameter<>(data, batchSize);
    }

    public Collection<T> getData() {
        return data;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
