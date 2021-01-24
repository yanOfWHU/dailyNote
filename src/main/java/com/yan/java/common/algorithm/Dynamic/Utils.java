package com.yan.java.common.algorithm.Dynamic;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class Utils {
    /**
     动态规划的模板套路：

     # 初始化 base case
     dp[0][0][...] = base
     # 进行状态转移
     for 状态1 in 状态1的所有取值：
     for 状态2 in 状态2的所有取值：
     for ...
     dp[状态1][状态2][...] = 求最值(选择1，选择2...)

     1、初始值(寻找初始状态，可能有一个或者多个)，初始状态
     2、获取状态转移方程
            1、斐波那契数列种， f(N) = f(N-1) + f(N-2)
            2、复杂的如凑零钱，c1 c2 ...cn n种零钱，凑到 amount 金额，最少数量的零钱凑法
                故而状态转移方程 f(amount) = min{f(mount-c1), f{mount-c2}, ... f{mount-cn}} + 1
                dp[i] = min{dp[i], dp[i - coin] + 1}
                dp[i] 表示，当目标金额为 i 时，至少需要 dp[i] 枚硬币凑成
     解决动态规划问题，不要看不起暴力解法，动态规划最难的就是写出暴力解，即状态转移方程

     动态规划的性质：重叠子问题

     动态规划问题的解法：
     1、暴力解法，最核心最关键的基础解法
     2、备忘录解法，存储期间重复的数据
     3、dp 解法，dp 数组进行迭代，虽然也有递归，但是不是 2^n 而只是 n 次迭代。
        3.1 遍历的过程中，所需的状态必须是已经计算出来的。
        3.2 遍历的终点必须是存储结果的那个位置。

     回溯

     */

    /**
     * 斐波那契数列
     * 这个函数还可以优化，因为只需要前两个元素，所以可以不需要 new 数组，直接使用三个变量表示即可
     */
    public int fib(int n) {
        if (n < 1) return 0;
        if (n == 1) return 1;
        if (n == 2) return 1;
        int[] dp = new int[n];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n - 1];
    }

    /**
     * 凑零钱问题
     * 如要凑 11 毛，因为凑1毛到凑10毛之间是存在重复的，所以可以从1循环到11获取最终需要凑的次数
     * @param coin 零钱种类 如 1、2、5毛
     * @param amount 凑的总额，如 11 毛
     * @return
     */
    public int collectChange(List<Integer> coin, int amount) {
        if (amount < 1) return -1;

        // dp[i] 表示凑齐 i 元需要的硬币个数
        int[] dp = new int[amount];
        dp[0] = 0;
        // 设置初始值
        coin.forEach(ele -> {
            if (ele <= amount) {
                dp[ele] = 1;
            }
        });

        for (int i = 0; i < amount; i++) {
            for (Integer ele : coin) {
                // 说明子问题无解
                if (i - ele < 0) continue;
                dp[i] = Math.min(dp[i], 1 + dp[i - ele]);
            }
        }
        return dp[amount] == amount + 1 ? -1 : dp[amount];
    }

    public static void main(String[] args) {

    }

}
