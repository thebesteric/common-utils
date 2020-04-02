package org.wesoft.common.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 红包工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-04-01 16:35
 */
public class RedPacketUtils {

    // 最小红包额度
    private static final int MIN_POINT = 1;
    // 最大红包额度
    private static final int MAX_POINT = 200 * 100;
    // 每个红包最大是平均值的倍数
    private static final double TIMES = 2.1;


    /**
     * 拆红包（整数）
     *
     * @param amount 总额
     * @param count  数量
     */
    public static synchronized Integer unwrap(int amount, int count, boolean fair) {
        if (!valid(amount, count))
            return null;
        int point = 0;
        if (count > 0) {
            if (fair) {
                int remainder = amount % count;
                point = amount / count;
                if (count == 1) {
                    point += remainder;
                }
            } else {
                // 红包最大金额为平均金额的 TIMES 倍
                int max = (int) (amount * TIMES / count);
                max = Math.min(max, MAX_POINT);
                point = random(amount, MIN_POINT, max, count);
            }
        }
        return point;
    }

    /**
     * 拆红包（小数）
     *
     * @param amount 总额
     * @param count  数量
     */
    public static synchronized Double unwrap(double amount, int count, boolean fair) {
        Double point = null;
        if (count > 0) {
            BigDecimal amountBigDecimal = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
            // 公平模式
            if (fair) {
                point = amountBigDecimal.divide(new BigDecimal(count), BigDecimal.ROUND_FLOOR).doubleValue();
                // 最后一个红包
                if (amountBigDecimal.doubleValue() < point * 2) {
                    point = amountBigDecimal.doubleValue();
                }
            }
            // 随机模式
            else {
                if (count > 1) {
                    List<Double> temp = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        double d = new SecureRandom().nextDouble() * amountBigDecimal.doubleValue() / count * 2;
                        temp.add(new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                    point = temp.get(new SecureRandom().nextInt(temp.size()));
                }
                // 最后一个红包
                else {
                    point = amountBigDecimal.doubleValue();
                }
            }
        }
        return point;
    }

    /**
     * 随机红包额度
     *
     * @param amount 积分
     * @param minS   最小额度
     * @param maxS   最大额度
     * @param count  剩余数量
     */
    private static int random(int amount, int minS, int maxS, int count) {
        // 红包数量为1，直接返回金额
        if (count == 1) {
            return amount;
        }
        // 如果最大金额和最小金额相等，直接返回金额
        if (minS == maxS) {
            return minS;
        }
        int max = Math.min(maxS, amount);
        // 随机产生一个红包
        int point = ((int) Math.rint(Math.random() * (max - minS) + minS)) % max + 1;
        int balance = amount - point;
        // 判断该种分配方案是否正确
        if (valid(balance, count - 1)) {
            return point;
        } else {
            int avg = balance / (count - 1);
            if (avg < MIN_POINT) {
                // 递归调用，修改红包最大金额
                return random(amount, minS, point, count);
            } else if (avg > MAX_POINT) {
                // 递归调用，修改红包最小金额
                return random(amount, point, maxS, count);
            }
        }
        return point;
    }


    /**
     * 此种红包是否合法
     *
     * @param point 分数
     * @param count 数量
     */
    private static boolean valid(int point, int count) {
        if (count != 0) {
            int avg = point / count;
            if (avg < MIN_POINT) {
                return false;
            }
            return avg <= MAX_POINT;
        }
        return false;
    }

    public static void main(String[] args) {
        double amount = 10;
        int count = 5;
        for (int i = 0; i < 10; i++) {
            Integer point = RedPacketUtils.unwrap((int) amount, count, false);
            // Double point = RedPacketUtils.unwrap(amount, count, true);
            System.out.println(point);
            if (point != null && point != 0) {
                amount -= point;
                count--;
            }
        }
    }
}