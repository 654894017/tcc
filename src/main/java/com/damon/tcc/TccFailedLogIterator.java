package com.damon.tcc;

import com.damon.tcc.log.TccLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class TccFailedLogIterator implements Iterator {
    private final Integer totalPage;
    private final Function<Integer, List<TccLog>> failedLogFunction;
    private Integer currentPage = 0;

    public TccFailedLogIterator(Integer totalPage, Function<Integer, List<TccLog>> failedLogFunction) {
        this.totalPage = totalPage;
        this.failedLogFunction = failedLogFunction;
    }

    @Override
    public boolean hasNext() {
        return currentPage < totalPage;
    }

    @Override
    public List<TccLog> next() {
        if (currentPage > totalPage) {
            return new ArrayList<>(0);
        }
        currentPage++;
        return failedLogFunction.apply(currentPage);
    }

}
