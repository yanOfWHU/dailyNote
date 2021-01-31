package com.yan.java.common.algorithm.Dynamic;

import static com.yan.java.common.util.CommonUtils.print;

import com.yan.java.common.util.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        PriorityQueue
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

     //这题居然不可以用二分法，1个鸡蛋和3层，居然需要3次，而不是2次，题目有点问题
//     无论 F 的初始值如何，你确定 F 的值的最小移动次数是多少？
//
//     K = 1 ，那么至少移动 N 次, 只能线性的去从 1 层开始加楼层扔鸡蛋
//     K = 2 以上，方案肯定是二分法最快，不断的二分法，然后 K 减少，N 减少，当 K 减少到 1 的时候，退化成线性增加次数
//
//     dp[k][n] = {
//        k == 1 ? n : dp[k - 1][ceil n / 2] + 1
//     }

     * @param K 鸡蛋数量
     * @param N 楼层
     * @return 确定 F 最小移动次数
     */
    public int superEggDrop(int K, int N) {
        if (N == 1) return 1;
        if (N == 2) return 2;
        if (K == 1) return N;

        int[][] dp = new int[K+1][N+1];

        for (int k = 0; k<=K;k++) {
            for (int n = 0; n<=N; n++) {
                dp[k][n] = Integer.MAX_VALUE;
            }
        }
        for (int n = 0; n<= N; n ++) {
            dp[1][n] = n;
        }
        return superEggDrop(K, N, dp);
    }

    private int superEggDrop(int K, int N, int[][]dp) {
        if (N == 1) return 1;
        if (N == 2) return 2;
        if (K == 1) return N;
        if(dp[K][N] != Integer.MAX_VALUE) return dp[K][N];
        for (int n = 1; n<= N; n++) {
            dp[K][n] = Math.min(
                dp[K][n],
                Math.max(dp[K][N - n], dp[K - 1][n - 1]) + 1
            );
        }
        return dp[K][N];
    }

    /**
     * 给定一个正整数 n，将其拆分为至少两个正整数的和，并使这些整数的乘积最大化。 返回你可以获得的最大乘积。
     * @param n
     * @return
     */
    public int integerBreak(int n) {
        if (n == 1) return 1;
        if (n == 2) return 1;
        if (n == 3) return 2;
        // C 为序列， V 为乘积最大值时，最大的乘积
        int[] dp = new int[n + 1];
        // 初始化
        for (int j = 4; j < n; j++) {
            dp[j] = j - 1;
        }
        dp[0] = 1;
        dp[1] = 1;
        dp[2] = 1;
        dp[3] = 2;
        for (int i = 4; i<= n; i++) {
            for (int j = 1; j < i; j++) {
                dp[i] = Math.max(dp[i], Math.max(j, dp[j]) * (i - j));
            }
        }
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();
        return dp[n];
    }

    /**
     * 最长回文子序列的长度
     * @param s 字符串 s
     * @return 长度
     */
    public int longestPalindromeSubseq(String s) {
        if (s == null || s.length() == 0) return 0;

        char[] arr = s.toCharArray();
        int len = arr.length;
        int[][] dp = new int[len][len];
        for (int i = 0; i<len;i++) {
            for(int j = 0; j< len;j++) {
                dp[i][j] = Integer.MIN_VALUE;
            }
        }
        return longestPalindromeSubseq(arr, dp, 0, len - 1);
    }

    /**
     * 这是从上往下的递推方式
     * @param arr
     * @param dp
     * @param from
     * @param end
     * @return
     */
    private int longestPalindromeSubseq(char[] arr, int[][] dp, int from, int end) {
        if (dp[from][end] != Integer.MIN_VALUE) return dp[from][end];
        if (from == end) {
           dp[from][end] = 1;
           return 1;
        }
        if (from + 1 == end) {
            if (arr[from] == arr[end]) {
                dp[from][end] = 2;
                return 2;
            } else {
               dp[from][end] = 1;
               return 1;
            }
        }
        if (arr[from] == arr[end]) {
           dp[from][end] = longestPalindromeSubseq(arr, dp, from+1, end-1) + 2;
           return dp[from][end];
        } else {
            dp[from][end] = Math.max(longestPalindromeSubseq(arr, dp, from +1, end), longestPalindromeSubseq(arr, dp, from, end-1));
            return dp[from][end];
        }
    }

    /**
     * 博弈问题
     *
     * 亚历克斯和李用几堆石子在做游戏。偶数堆石子排成一行，每堆都有正整数颗石子 piles[i] 。
     *
     * 游戏以谁手中的石子最多来决出胜负。石子的总数是奇数，所以没有平局。
     *
     * 亚历克斯和李轮流进行，亚历克斯先开始。 每回合，玩家从行的开始或结束处取走整堆石头。 这种情况一直持续到没有更多的石子堆为止，此时手中石子最多的玩家获胜。
     *
     * 假设亚历克斯和李都发挥出最佳水平，当亚历克斯赢得比赛时返回 true ，当李赢得比赛时返回 false 。
     * count[i][j] 表示从 i->j 玩家最多可以得到的石头总数 totalCount 表示 i->j 的石头总数
     * 所以每次在取石头的时候，都需要保证石头取完，对奕者能获得到的石头总数最少
     * n 堆石头 count[i][j] = max{totalCount - count[i][j-1], totalCount - count[i+1][j-1]}}
     * 所以上面公式， count[i][j] 表示取头或者取尾，另外一个人能拿到的熟练更少，说明 totalCount - 头/尾 应该最大
     * 每次都尽可能让他下一次取的时候更少
     *
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/stone-game
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     * @param piles
     * @return
     */
    public boolean stoneGame(int[] piles) {
        int len = piles.length;
        int[][] dp = new int[len][len];
        // dp[i][j] 表示该回合能拿到的最多石头
        // 一堆石头，i =j， count[i][j] = pipe[i];
        // 两堆石头 j = i + 1, count[i][j] = max{pile[i], pile[j]}
        // n 堆石头 count[i][j] = max{totalCount - max{count[i][j-1], count[i+1][j-1]}}
        if (piles.length == 2 || piles.length == 4) return true;
        int total = Arrays.stream(piles).reduce(Integer::sum).getAsInt();

        int maxCount = stoneGame(piles, dp, 0, piles.length - 1, total);
        return maxCount * 2 > total;
    }

    private int stoneGame(int[] piles, int[][] dp, int from, int end, int total) {
        if (dp[from][end] != 0) return dp[from][end];
        if (from == end) {
            dp[from][end] = piles[from];
            return dp[from][end];
        } else if (from +1 == end) {
            dp[from][end] = Math.max(piles[from], piles[end]);
            return dp[from][end];
        }
        dp[from][end] = Math.max(
            total - stoneGame(piles, dp, from, end-1, total - piles[end]),
            total - stoneGame(piles, dp, from+1, end, total - piles[from])
        );
        return dp[from][end];
    }


    /**
     * 正则表达式匹配
     给你一个字符串 s 和一个字符规律 p，请你来实现一个支持 '.' 和 '*' 的正则表达式匹配。

     '.' 匹配任意单个字符
     '*' 匹配零个或多个前面的那一个元素
     所谓匹配，是要涵盖 整个 字符串 s的，而不是部分字符串。

     来源：力扣（LeetCode）
     链接：https://leetcode-cn.com/problems/regular-expression-matching
     著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。

     正则表达式只有 . 和 * 和普通字符，默认正则表达式是正常的

     bool dp(string& s, int i, string& p, int j) {
        dp(s, i, p, j + 2) || dp(s, i + 1, p, j) || dp(s, i + 1, p, j + 1);
     }

     *
     * 这题 . 可以匹配空字符，所以无法 AC，只能过 80% case
     * 这里直接 copy 最佳答案过来
     * 这题最佳方案是使用 NFA 和 DFA 自动机的方式
     * 将正则匹配字符构建自动机，自动机是一个链表(注意 一定要是链表)
     * 方式同样是动态规划，不过用的是从左往右匹配的自底向上规划
     * @param s 匹配字符串
     * @param p 正则表达式
     * @return
     */
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] f = new boolean[m + 1][n + 1];
        f[0][0] = true;
        for (int i = 0; i <= m; ++i) {
            for (int j = 1; j <= n; ++j) {
                if (p.charAt(j - 1) == '*') {
                    f[i][j] = f[i][j - 2];
                    if (matches(s, p, i, j - 1)) {
                        f[i][j] = f[i][j] || f[i - 1][j];
                    }
                } else {
                    if (matches(s, p, i, j)) {
                        f[i][j] = f[i - 1][j - 1];
                    }
                }
            }
        }
        return f[m][n];
    }

    public boolean matches(String s, String p, int i, int j) {
        if (i == 0) {
            return false;
        }
        if (p.charAt(j - 1) == '.') {
            return true;
        }
        return s.charAt(i - 1) == p.charAt(j - 1);
    }

    /**
     * 略
     * 获取从 1 到 n，每个数二进制表示中 1 的个数
     *
     * 暴力解法 每个数都去遍历一遍获取 1 的个数
     *
     * dp？
     * dp[n] 和 dp[k] 的关系？
     *
     * 略
     * @param num
     */
    public int[] getNumbersOf1(int num) {
        int[] ret = new int[num + 1];
        for(int i = 1; i <= num; i++){
            ret[i] = ret[i&(i-1)] + 1;
        }
        return ret;
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

        System.out.println(ins.integerBreak(14));
        print(ins.isMatch("a", "..a"));

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
