package com.damon;

public class TestRepository {

    public Test get(Long id) {
        return new Test(id);
    }

}
