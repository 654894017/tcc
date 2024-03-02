package com.damon.tcc.function;

@FunctionalInterface
public interface TwoParameterFunction<P1, P2, R> {
    R apply(P1 p1, P2 p2);
}
