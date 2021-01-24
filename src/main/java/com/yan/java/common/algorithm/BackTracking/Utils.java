package com.yan.java.common.algorithm.BackTracking;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class Utils {
    /**
     回溯算法: 解决一个回溯问题，实际上就是一个决策树的遍历过程
     思考三个问题：
        1、路径：也就是已经做出的选择。

        2、选择列表：也就是你当前可以做的选择。

        3、结束条件：也就是到达决策树底层，无法再做选择的条件。

        框架：
            result = []
            def backtrack(路径, 选择列表):
                if 满足结束条件:
                    result.add(路径)
                    return

                for 选择 in 选择列表:
                    做选择
                    backtrack(路径, 选择列表)
                    撤销选择

     其核心就是 for 循环里面的递归，在递归调用之前「做选择」，在递归调用之后「撤销选择」


     不管怎么优化，都符合回溯框架，而且时间复杂度都不可能低于 O(N!)，因为穷举整棵决策树是无法避免的。
     这也是回溯算法的一个特点，不像动态规划存在重叠子问题可以优化，回溯算法就是纯暴力穷举，复杂度一般都很高。
     */

    /**
     * 全排列
     * @param elements 元素
     * @return 所有全排列的 list
     */
    public static List<String> permutation(List<Character> elements) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean[] visited = new boolean[elements.size()];
        permutation(elements, result, sb, visited,  0, elements.size() - 1);
        return result;
    }

    private static void permutation(List<Character> elements, List<String> path,
                                    StringBuilder sb, boolean[] visited, int depth, int finalDepth) {
        if (depth == finalDepth) {
            // 说明满足了结束条件
            path.add(sb.toString());
            return;
        }

        for (int i = 0 ; i < elements.size() ; i ++ ) {
            if (visited[i]) continue;
            sb.append(elements.get(i));
            visited[i] = true;
            permutation(elements, path, sb, visited, depth + 1, finalDepth);
            visited[i] = false;
            sb.deleteCharAt(sb.length() - 1);
        }

    }

    /**
     * N 皇后问题
     * N * N 棋盘中，N 个皇后
     * 皇后的 8 个方向可以任意攻击
     * 求皇后的摆法
     * @param n 皇后数量
     * @return Q 表示皇后， '.' 表示空位
     */
    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        boolean[][] set = new boolean[n][n];
        solveNQueens(n, result, set, 0);
        return result;
    }

    // 低 row 行决策
    private static void solveNQueens(int n, List<List<String>> result, boolean[][] set, int row) {
        // 决策完最后一行
        if (row == n) {
            // 采集记录
            record(result, set);
            return;
        }

        // 遍历第 row 行所有的列
        for (int i = 0; i < n ; i ++) {
            if (!isValid(set, row, i)) continue;
            set[row][i] = true;
            solveNQueens(n, result, set, row + 1);
            set[row][i] = false;
        }

    }

    private static boolean isValid(boolean[][] set, int row, int col) {
        int n = set.length;
        for (int j = 0; j < n; j++) {
            if (set[j][col]) return false;
        }
        // 不在其他皇后的左上方
        for (int i = row - 1, j = col + 1; i >= 0 && j < n ; i--, j++) {
            if (set[i][j]) return false;
        }

        // 不在其他皇后的右上方
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (set[i][j]) return false;
        }

        return true;
    }

    private static void record(List<List<String>> result, boolean[][] set) {
        List<String> tmp = new ArrayList<>();
        int n = set.length;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < n ; j++) {
            for (int i = 0; i < n; i++) {
                if (set[i][j]) {
                    sb.append("Q");
                } else {
                    sb.append(".");
                }
            }
            tmp.add(sb.toString());
            sb.delete(0, sb.length());
        }
        result.add(tmp);
    }


    public static int missingNumber(int[] nums) {
        int n = nums.length;
        int res = 0;
        // 先和新补的索引异或一下
        res ^= n;
        // 和其他的元素、索引做异或
        for (int i = 0; i < n; i++)
            res ^= i ^ nums[i];
        return res;
    }

    public static void main(String[] args) {
        System.out.println(permutation(Lists.newArrayList('a', 'b', 'c', 'd', 'e')));
        System.out.println(solveNQueens(4));
        System.out.println(missingNumber(new int[]{0, 1, 2, 4}));
    }
}
