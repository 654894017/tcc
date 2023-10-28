package com.damon.tcc;

import com.damon.tcc.log.TccLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class TccLogIterator implements Iterator {
    private Integer totalPage;
    private Integer currentPage = 0;
    private Function<Integer, List<TccLog>> function;

    public TccLogIterator(Integer totalPage, Function<Integer, List<TccLog>> function) {
        this.totalPage = totalPage;
        this.function = function;
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
        return function.apply(currentPage);
    }

}
