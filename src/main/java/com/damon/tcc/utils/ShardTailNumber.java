package com.damon.tcc.utils;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分片尾号生成器
 * 用于生成特定分片下的任务ID尾号列表
 */
public class ShardTailNumber {
    private final int shardTotal;    // 总分片数
    private final int shardIndex;    // 当前分片索引（0-based）
    private final int tailLength;    // 尾号长度
    private final int maxTailNumber; // 最大尾号值（如尾号长度为2，则最大为99）

    /**
     * 构造函数
     *
     * @param shardTotal 总分片数
     * @param shardIndex 当前分片索引（0-based）
     * @param tailLength 尾号长度
     */
    public ShardTailNumber(int shardTotal, int shardIndex, int tailLength) {
        if (shardTotal <= 0 || shardIndex < 0 || shardIndex >= shardTotal || tailLength <= 0) {
            throw new IllegalArgumentException("分片参数不合法");
        }
        this.shardTotal = shardTotal;
        this.shardIndex = shardIndex;
        this.tailLength = tailLength;
        this.maxTailNumber = (int) Math.pow(10, tailLength) - 1;
    }

    /**
     * 生成当前分片的尾号列表
     *
     * @return 尾号列表（如 ["00", "03", "06", ...]）
     */
    public List<String> generateTailNumbers() {
        List<String> tailNumbers = new ArrayList<>();
        for (int i = 0; i <= maxTailNumber; i++) {
            if (i % shardTotal == shardIndex) {
                tailNumbers.add(StrUtil.padPre(String.valueOf(i), tailLength, "0"));
            }
        }
        return tailNumbers;
    }

    // 其他可能的方法：获取总分片数、当前分片索引等
    public int getShardTotal() {
        return shardTotal;
    }

    public int getShardIndex() {
        return shardIndex;
    }

}