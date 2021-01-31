package com.yan.java.common.algorithm.DivideAndConquer;

import com.yan.java.common.algorithm.ADT.Node;
import com.yan.java.common.algorithm.Solution;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分治算法的核心思想是：
 * 大问题可以拆分成小问题
 * 小问题可以拆分成更小的问题
 * 最终最小的问题就是最小的单元，就是最基本的临界值元素
 *
 * 可见，分治法，其实本质上是自顶向下不断拆分子问题的过程，再拆分的过程中已经记录下了中间值
 * 拆分到了最小问题时，我们就已经获取到了一个解
 *
 * 分治问题最难的是找出拆分子问题的方式
 *
 * 同动态规划方式，分治方法寻找子问题最好的办法就是先尝试用暴力思想取写，写的过程中会比较容易找到子问题的公共点
 */
public class Utils {

    /**
     *
     给定一个含有数字和运算符的字符串，为表达式添加括号，改变其运算优先级以求出不同的结果。你需要给出所有可能的组合的结果。有效的运算符号包含 +, - 以及 * 。

     来源：力扣（LeetCode）
     链接：https://leetcode-cn.com/problems/different-ways-to-add-parentheses
     著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。



     a + b + c + d + e + f
     最宏观的描述就是 A calc B
     这样的话 就需要把 A 和 B 拆分 那么结果就是 (foreach a in A) calc (foreach b in B)
     A 和 B 怎么获取？
     遍历字符串，遇到 + - * 那么左边的字符串解析结果就是 A,右边的字符串解析结果就是 B
     临界条件就是，这个字符串没有运算符

     例如：2-1-1
     从左往右遍历
     1、第一个 '-', 那么 result add: 2 的所有结果 和 1 - 1 的所有结果 做减法 结果就是 2
     2、第二个 '-'，那么 result add： 2 - 1的所有结果 和 1 的所有结果 做减法 结果就是 0

     * @param input
     * @return
     */
    public List<Integer> diffWaysToCompute(String input) {
        return diffWaysToCompute(input, 0, input.length());
    }

    private List<Integer> diffWaysToCompute(String input, int from, int end) {
        // 我们可以肯定 from 和 end 对应的元素都是数字
        List<Integer> result = new ArrayList<>();
        char ch;
        for (int i = from; i < end; i++) {
            ch = input.charAt(i);
            if (isCaclSymbol(ch)) {
                List<Integer> leftResult = diffWaysToCompute(input.substring(0, i));
                List<Integer> rightResult = diffWaysToCompute(input.substring(i+1));
                for (int left : leftResult) {
                    for (int right: rightResult) {
                        switch (ch) {
                            case '+':
                                result.add(left + right);
                                break;
                            case '-':
                                result.add(left - right);
                                break;
                            case '*':
                                result.add(left * right);
                                break;
                        }
                    }
                }
            }
        }
        if (result.size() == 0) {
            result.add(Integer.valueOf(input));
        }
        return result;
    }

    private boolean isCaclSymbol(char ch) {
        return ch == '+' || ch == '-' || ch == '*';
    }

    /**
     * 给定一个整数 n，生成所有由 1 ... n 为节点所组成的 二叉搜索树
     *
     * 思考：二叉搜索树的特点
     * 中序遍历结果是递增的
     * 即，左节点 < 父节点 < 右节点
     *
     * 所以这个问题可以归根到如何构建 1 为根的树、2 为 根的树。。。n 为根的树
     *
     * 再构建 k 为根的树的时候，又可以按照这种方式拆分问题
     *
     * 那么寻找最小问题：当只有一个元素的时候，那么，这个时候，这棵树就只有 一个节点
     *
     *
     * @param n
     * @return
     */
    public List<Node> generateTrees(int n) {
        return generateTrees(1, n);
    }

    private List<Node> generateTrees(int from, int end) {
        List<Node> result = new ArrayList<>();
        // 最小问题，当只有一个元素的时候，就是一个最小的树
        if (from == end) {
            result.add(new Node(from));
            return result;
        }
        if (from > end) return Collections.emptyList();
        for (int i = from; i<= end; i++) {
            List<Node> left = generateTrees(from, i - 1);
            List<Node> right = generateTrees(i + 1, end);
            if (CollectionUtils.isEmpty(left)) {
                // 左子树为空
                for (Node r : right) {
                    Node head = new Node(i);
                    head.left = null;
                    head.right = r;
                    result.add(head);
                }
            } else if (CollectionUtils.isEmpty(right)) {
                // 右子树为空
                for (Node l : left) {
                    Node head = new Node(i);
                    head.left = l;
                    head.right = null;
                    result.add(head);
                }
            } else {
                // 左右子树都不为空
                for (Node l : left) {
                    for (Node r : right) {
                        Node head = new Node(i);
                        head.left = l;
                        head.right = r;
                        result.add(head);
                    }
                }
            }
        }
        return result;
    }
}
