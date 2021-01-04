package com.klasdq.demo.persistence.mybatis;



import com.klasdq.demo.common.utils.BeanMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询结果对象
 *
 */
public class Page<T> implements Serializable {
    private long page;
    private long size;
    private long totalElements;
    private List<T> content;

    private Object appendant;

    public Page(long page, long size, long totalElements) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public Page(long page, long size, long totalElements, List<T> content) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.content = content;
    }

    public long getPage() {
        return page;
    }

    public long getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getContent() {
        return content == null ? new ArrayList<>() : content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Object getAppendant() {
        return appendant;
    }

    public Page<T> setAppendant(Object appendant) {
        this.appendant = appendant;
        return this;
    }

    public long getTotalPages() {
        return this.totalElements > 0 ? ((this.totalElements - 1) / this.size + 1) : 0;
    }

    public boolean isFirstPage() {
        return this.page <= 1;
    }

    public boolean isLastPage() {
        return this.page >= this.getTotalPages();
    }

    public boolean hasContent() {
        return this.content != null && this.content.size() > 0;
    }

    public <V> Page<V> map(Class<T> originClass, Class<V> distClass) {
        Page<V> page = new Page<>(this.page, this.size, this.totalElements);
        if (this.content != null) {
            List<V> data = BeanMapper.mapList(this.content,originClass, distClass);
            page.setContent(data);
        }
        return page;
    }
    public <V> Page<V> map(Class<V> distClass) {
        Page<V> page = new Page<>(this.page, this.size, this.totalElements);
        if (this.content != null) {
            List<V> data = BeanMapper.copyList(this.content, distClass);
            page.setContent(data);
        }
        return page;
    }
}
