package com.damon.tcc.utils;

import java.text.DecimalFormat;

public class RandomNumber {
    private final int length;

    public RandomNumber(int length) {
        this.length = length;
    }

    /**
     * 生成指定长度的随机数字字符串
     *
     * @return 指定长度的随机数字字符串，不足的部分用0填充
     * @throws IllegalArgumentException 如果长度小于等于0
     */
    public String generate() {
        // 参数校验
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        // 创建格式化器，动态生成格式字符串（如"0000"表示4位）
        StringBuilder formatBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            formatBuilder.append('0');
        }
        DecimalFormat df = new DecimalFormat(formatBuilder.toString());

        // 计算最大值（如length=4，则max=10000）
        int maxValue = (int) Math.pow(10, length);

        // 生成0到maxValue-1之间的随机整数
        int randomNumber = (int) (Math.random() * maxValue);

        // 格式化为字符串
        return df.format(randomNumber);
    }

} 