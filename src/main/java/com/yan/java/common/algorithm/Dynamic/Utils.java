package com.yan.java.common.algorithm.Dynamic;

import com.yan.java.common.util.CommonUtils;

import java.util.*;

/**
 * 最优子结构并不是动态规划独有的一种性质，能求最值的问题大部分都具有这个性质；
 * 但反过来，最优子结构性质作为动态规划问题的必要条件，一定是让你求最值的。
 */
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

     dp 数组的遍历方向？
     有时候我们会正向遍历、有时候会反向、有时候会斜向遍历。
     谨记：
     1、遍历的过程中，所需的状态必须是已经计算出来的。
     2、遍历的终点必须是存储结果的那个位置。


     如何获取 dp 的过程？
     比如找零钱，我们要找零钱，肯定不能只获取最少的兑换次数，肯定还需要获取兑换的方式
     {@link DynamicNode} 使用的时候就可以定义 DynamicNode[][] 去定义 dp 了
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
        return dp[amount] == amount + 1 || dp[amount] == 0  ?  -1 : dp[amount];
    }

    /**
     * 编辑距离 两个字符串之间的编辑距离
     * 规定，删除、修改、添加都算一次编辑操作
     * 比如 aa -> bb 编辑距离为 2
     * a1 -> a2 编辑距离为 1
     *
     * 这种属于暴力解法(递归)，存在大量重叠子问题
     * @param s1 字符串1
     * @param s2 字符串2
     * @return
     */
    public int editDistance(String s1, String s2) {
        if (s1 == null || s1.length() == 0) return s2 == null ? 0 : s2.length();
        if (s2 == null || s2.length() == 0) return s1.length();
        return editDistance(s1.toCharArray(), s2.toCharArray(), s1.length() - 1, s2.length() - 1);
    }

    private int editDistance(char[] arr1, char[] arr2, int i, int j) {
        if (i == -1) return j + 1;
        if (j == -1) return i + 1;

        if (arr1[i] == arr2[j]) {
            // 说明匹配上了
            return editDistance(arr1, arr2, i - 1, j - 1);
        } else {
            return CommonUtils.min(
                    // 删除
                    editDistance(arr1, arr2, i -1, j) + 1,
                    // 插入
                    editDistance(arr1, arr2, i, j -1) + 1,
                    // 替换
                    editDistance(arr1, arr2, i - 1, j - 1) + 1
            );
        }
    }

    /**
     * 非递归实现
     * 借助 dp[i][j]
     * dp[i + 1][j] 表示 s1[0->i + 1] 到 s2[o->j+1] 的最短编辑距离
     * 所以 dp[s1.length - 1][s2.length - 1] 就是我们需要的答案
     * @param s1
     * @param s2
     * @return
     */
    public int editDistanceNonRecursive(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();

        int[][] dp = new int[l1 + 1][l2 + 1];

        String CHOICE_DELETE = "delete";
        String CHOICE_INSERT = "insert";
        String CHOICE_REPLACE = "replace";
        String CHOICE_RESERVE = "reserve";
        DynamicNode<Integer, String>[][] dn = new DynamicNode[l1 + 1][l2 + 1];

        // 初始化
        // 从空字符串到一个字符串的最短编辑距离就是字符串的长度
        for (int i = 0; i <= l1; i++) {
            dp[i][0] = i ;
            dn[i][0] = new DynamicNode<>(i, CHOICE_DELETE);
        }
        for (int j = 0; j <= l2; j++) {
            dp[0][j] = j;
            dn[0][j] = new DynamicNode<>(j, CHOICE_INSERT);
        }

        for (int i = 1; i <= l1; i ++) {
            for (int j = 1; j <= l2; j ++) {
                // 如果这两个字符相同，则 dp[i][j] = dp[i - 1][j - 1]
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                    dn[i][j] = new DynamicNode<>(dn[i - 1][j - 1].val, CHOICE_RESERVE);
                } else {
                    // 如果这两个字符不同，则编辑、操作、删除
                    dp[i][j] = CommonUtils.min(dp[i][j - 1], dp[i - 1][j], dp[i - 1][j -1]) + 1;
                    int val1 = dn[i][j - 1].val;
                    int val2 = dn[i - 1][j].val;
                    int min = CommonUtils.min(dn[i][j - 1].val, dn[i][j -1].val, dn[i - 1][j -1].val);
                    if (val1 == min) {
                        dn[i][j] = new DynamicNode<>(min + 1, CHOICE_INSERT);
                    } else if (val2 == min) {
                        dn[i][j] = new DynamicNode<>(min + 1, CHOICE_DELETE);
                    } else {
                        dn[i][j] = new DynamicNode<>(min + 1, CHOICE_REPLACE);
                    }
                }
            }
        }
        // 获取到操作之后，可以根据操作逆推获取所有的操作
        StringBuilder sb = new StringBuilder();
        int i = l1;
        int j = l2;
        DynamicNode<Integer, String> current = dn[i][j];
        while (true) {
            if (CHOICE_DELETE.equals(current.choice)) {
                sb.append(i);
                sb.append(CHOICE_DELETE);
                i--;
                if (i < 0) break;
            } else if (CHOICE_INSERT.equals(current.choice)) {
                sb.append(i);
                sb.append(CHOICE_INSERT);
                j--;
                if (j <= 0) break;
            } else {
                sb.append(i);
                if (CHOICE_REPLACE.equals(current.choice)) {
                    sb.append(CHOICE_REPLACE);
                } else {
                    sb.append(CHOICE_RESERVE);
                }
                i--;
                j--;
                if (i < 0 || j <= 0) break;
            }
            current = dn[i][j];
        }
       System.out.println("path:" + sb.toString());
        System.out.println("total square path:");
        for (i = 0; i <= l1; i ++) {
            for (j = 0; j <= l2; j++) {
                System.out.print(String.format("%10s", dn[i][j].choice) + " ");
            }
            System.out.println();
        }
        return dp[l1][l2];
    }


    /**
     * 最长递增子序列的长度
     *
     * 考虑 dp[i] 表示的是 num[0 -> i]这个数组的最长递增子序列的长度
     * 那么 dp[i+1] 和 dp[i] 什么关系呢？？？
     * 如果 num[i+1] > num[i] 则，dp[i + 1] = dp[i] + 1
     * 如果 num[i + 1] <= num[i] 呢? 遍历(n) 0 <= n <= i,
     * 只要 num[n] < num[i + 1] 那么 dp[i + 1] = max(dp[i + 1], dp[n] + 1)
     *
     * @param nums 序列数组
     * @return
     */
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int[] dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
        }


        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j ++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        // 遍历所有 dp，获取最大值
        int result = 0;
        for (int i = 0; i < nums.length; i++) {
            result = Math.max(result, dp[i]);
        }
        return result;
    }

    /**
     * 高楼扔鸡蛋
     *
     你将获得 K 个鸡蛋，并可以使用一栋从 1 到 N  共有 N 层楼的建筑。

     每个蛋的功能都是一样的，如果一个蛋碎了，你就不能再把它掉下去。

     你知道存在楼层 F ，满足 0 <= F <= N 任何从高于 F 的楼层落下的鸡蛋都会碎，从 F 楼层或比它低的楼层落下的鸡蛋都不会破。

     每次移动，你可以取一个鸡蛋（如果你有完整的鸡蛋）并把它从任一楼层 X 扔下（满足 1 <= X <= N）。

     你的目标是确切地知道 F 的值是多少。

     无论 F 的初始值如何，你确定 F 的值的最小移动次数是多少？

     K = 1 ，那么至少移动 N 次, 只能线性的去从 1 层开始加楼层扔鸡蛋
     K = 2 以上，方案肯定是二分法最快，不断的二分法，然后 K 减少，N 减少，当 K 减少到 1 的时候，退化成线性增加次数

     dp[k][n] = {
        k == 1 ? n : dp[k - 1][ceil n / 2] + 1
     }

     * @param K 鸡蛋数量
     * @param N 楼层
     * @return 确定 F 最小移动次数
     */
    public int superEggDrop(int K, int N) {
        int[][] dp = new int[K + 1][N + 1];

        for (int k = 0; k<=K; k++) {
            dp[k][0] = 1;
        }

        dp[1][1] = 1;
        for (int n = 2; n <= N; n ++) {
            dp[1][n] = n ;
        }

        for (int k = 2; k <= K; k++) {
            dp[k][1] = 1;
        }

        int log2;
        for (int k = 2; k <= K; k++) {
            for (int n = 2; n <= N; n++) {
                log2 = log2(n);
                if (k > log2 + 1) {
                    dp[k][n] = dp[log2][n];
                } else if (k == log2) {
                    // 刚好是对数
                    dp[k][n] = dp[k - 1][n / 2] + 1;
                } else {
                    dp[k][n] = dp[k - 1][intDiv2Ceil(n)] + 1;
                }
            }
        }

        return dp[K][N];
    }

    private int log2(int number) {
        if (number < 1) {
            throw new IllegalArgumentException();
        }
        return (int)(Math.floor(Math.log(number) / Math.log(2)));
    }

    private int intDiv2Ceil(int n) {
        return n % 2 == 0 ?  n / 2 - 1 : (n - 1) / 2;
    }

    public static void main(String[] args) {
        Utils ins = new Utils();
        System.out.println("两个字符串(apple pineapple)的最短编辑距离:" + ins.editDistance("apple", "pineapple") + "---" + ins.editDistanceNonRecursive("apple", "pineapple"));
        System.out.println("两个字符串(apple banana)的最短编辑距离:" + ins.editDistance("apple", "banana") + "---" + ins.editDistanceNonRecursive("apple", "banana"));
        System.out.println(ins.editDistanceNonRecursive("a1","a2"));

        System.out.println("鸡蛋次数2 " + ins.superEggDrop(1, 2));
        System.out.println("鸡蛋次数2 " + ins.superEggDrop(2, 2));
        System.out.println("鸡蛋次数3 " + ins.superEggDrop(2, 4));
        System.out.println("鸡蛋次数3 " + ins.superEggDrop(2, 6));
        System.out.println("鸡蛋次数2 " + ins.superEggDrop(2, 3));
        System.out.println("鸡蛋次数4 " + ins.superEggDrop(2, 9));
        System.out.println("鸡蛋次数4 " + ins.superEggDrop(3, 14));
    }

    /**
     * 动态规划的路径记录
     * @param <V> 记录的信息的值
     * @param <C> 记录信息的当前选择
     */
    public static class DynamicNode<V extends Comparable, C> {
        V val;
        C choice;

        public DynamicNode(V val, C choice) {
            this.val = val;
            this.choice = choice;
        }

    }

}
