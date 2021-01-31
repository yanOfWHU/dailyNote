package com.yan.java.common.algorithm.BitCalculate;

import static com.yan.java.common.util.CommonUtils.print;

/**
 * 位运算
 *
 * &
 * 0 & 0 = 0
 * 0 & 1 = 0
 * 1 & 1 = 1
 *
 * |
 * 0 | 0 = 0
 * 0 | 1 = 1
 * 1 | 1 = 1
 *
 * ^ 亦或 同则 0 异则 1
 * 0 ^ 0 = 0
 * 1 ^ 1 = 0
 * 1 ^ 0 = 1
 *
 * 左移和右移(需要注意还会有溢出问题 位号过大溢出，位号过小溢出, 溢出后，对应位都是 0)
 * >>
 * <<
 * 1 << 2 = 4
 *
 * >>>
 * <<<
 * 无符号左移和右移
 * 忽略符号位 即无论最左边是什么书，都会变成 0
 *
 * a 和 -a 的区别： 二进制表示就是非符号位全相反，而后再加 1
 * 如 1 = 0000 0000 0000 0000 0000 0000 0000 0001
 * 如 2 = 0000 0000 0000 0000 0000 0000 0000 0010
 * 如 3 = 0000 0000 0000 0000 0000 0000 0000 0011
 * 那么 -1 = x111 1111 1111 1111 1111 1111 1111 1110 + 1 = x111 1111 1111 1111 1111 1111 1111 1111 其中符号位为 1 表示负数
 * 1111 1111 1111 1111 1111 1111 1111 1111 -1:
 * x111 1111 1111 1111 1111 1111 1111 1110 -2:
 * x111 1111 1111 1111 1111 1111 1111 1101 -3
 * 同理 正数的二进制为负数的二进制取相反再 加1 也是加一 {@link Note.Note 负数变正数也是取相反值 + 1 }
 * 可以到的一个关系
 * a & -a 可以得到 a 最右侧为 1 的位
 *
 * 那么如果 a ^ b = s, 则令 s = s & -s, 这样 a & s 和 b & s ，必有且一个为 0
 *
 * 常用公式：
 * {@link Note.Note a & 1 可以获取 a 的最后一位是不是 1, a & 1 要么是 1 要么是 0}
 */
public class Utils {

    /**
     两个整数之间的汉明距离指的是这两个数字对应二进制位不同的位置的数目。

     给出两个整数 x 和 y，计算它们之间的汉明距离。

     同则 1，异则 0
     计算 1 的数量
     * @param x
     * @param y
     * @return
     */
    public int hammingDistance(int x, int y) {
        int ret = x ^ y;
        int count = 0;
        int tmp = ret;
        for (int i = 1; i <= 31; i++) {
            // %2 == 1 也可以做成位与操作
            if (tmp % 2 == 1) count++;
            tmp = tmp >> 1;
        }
        return count;
    }

    /**
     给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。

     全部亦或，同则 1 ，异则0
     a ^ a = 0
     * @param nums
     * @return
     */
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int ele : nums) {
            result ^= ele;
        }
        return result;
    }

    /**
     * 给定一个包含 [0, n] 中 n 个数的数组 nums ，找出 [0, n] 这个范围内没有出现在数组中的那个数。

     singleNum 的变形

     将 0 -> n 和数组内的所有元素 共 2n - 1 个元素全部亦或即可
     * @param nums
     * @return
     */
    public int missingNumber(int[] nums) {
        int result = 0;
        for (int ele : nums) {
            result ^= ele;
        }
        for (int i = 0; i <= nums.length; i++) {
            result ^= i;
        }
        return result;
    }

    /**
     * 一堆偶数个的数组中，有且仅有两个数是不同的
     * 求出这两个数
     *
     *
     * 假设这两个数为 a 和 b
     * ^ 全部，可以得到 s，即 s = a ^ b (则s 为 a 和 b 不相同的位)
     * 令 s = s & -s，这样可以得到原 s 最后一个为 1 的位(其他位均为 0)
     *
     * 注意 ^ 异或的本质，同则 0， 异则 1。
     * 故而现在的 s 和 a 或者 b 进行 & 操作，必定有且只有一个值为 0
     *
     * 验证一下：
     * a = 1 0000 0000 0000 0000 0000 0000 0000 0001
     * b = 2 0000 0000 0000 0000 0000 0000 0000 0010
     * s = a ^ b = 0000 0000 0000 0000 0000 0000 0000 0011
     * s &= -s = 0000 0000 0000 0000 0000 0000 0000 0010
     *
     * 故而 s & a == 0 而 s & b != 0
     *
     * @param nums
     * @return
     */
    public int[] singleNumber2(int[] nums) {
        int result = 0;
        for (int ele : nums) {
            result ^= ele;
        }
        // 说明这两个数 a ^ b = result
        int a = 0;
        int b = 0;
        result &= -result;
        for (int ele : nums) {
            if ((ele & result) == 0) a ^= ele;
            else b ^= ele;
        }
        return new int[]{a, b};
    }

    /**
     * 颠倒给定的 32 位无符号整数的二进制位。
     *  00000010100101000001111010011100 -> 00111001011110000010100101000000
     * @param n
     * @return
     */
    public int reverseBits(int n) {
        int ret = 0;
        for (int i = 0; i < 32; i++) {
            // ret 左移一位，为第二步做准备
            ret <<= 1;
            // 设置 ret 的最后一位
            ret |= (n & 1);
            // n 右移 去除最后一位
            n >>>= 1;
        }
        return ret;
    }

    /**
     * 交换 a 和 b
     * 0101
     * 1010
     * s = a ^ b = 1111 不用看值，1 的位数表示 a 和 b 对应的位不同
     * a = s ^ b 可以得到 0101 即开始的 a
     * b = s ^ a 可以得到 1010 可以得到原始的 b
     */
    public int[] exchange(int a, int b) {
        // a 换为 a 和 b 位数不同的值
        a = a ^ b;
        // 这里再次执行，则 b 替换为 a
        b = a ^ b;
        // b 已经替换为之前的 a，那么这里 b 和 现在的 a 异或和第二步是一致的
        a = a ^ b;
        return new int[]{a, b};
    }

    /**
     * n 是否是 2 的 n 次方
     * 2 的 n 次方，说明只有一位是 1，但不可以是 1000 0000 0000xx... 即必须要大于 0
     *
     * 同理，如何判断一个数是否是 4 的 n 次方
     * 枚举出来可知 4 的 n 次方，奇数位有且只有一个是 1
     */
    public boolean isPowerOfTwo(int n) {
        if (n <= 0) return false;
        boolean ret = false;
        for (int i = 1; i <= 31; i++) {
            if ((n & 1) == 1) {
                if (ret) return false;
                ret = true;
            }
            n >>= 1;
        }
        // 简单的使用 n > 0 && Integer.bitCount(n) == 1
        return ret;
    }

    public static void main(String[] args) {
        print(3 ^ 1);
    }
}
