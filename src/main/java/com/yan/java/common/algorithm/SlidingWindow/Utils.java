package com.yan.java.common.algorithm.SlidingWindow;

import java.util.*;

public class Utils {
    /**
     * 滑动窗口使用
     * 1、固定left，right不断往右移动，存储中间数据
     * 2、当满足条件后，存储满足条件的状态 left 和 right
     * 3、left 右移，直到 [left，right]范围内数据不满足条件
     *
     * 重复上述过程直到，right到了数组末尾。
     *
     *
     *
     抽象思想

     // 初始 left 和 right 一致
     int left = 0, right = 0;

     // 循环直到 right 到达末尾
     while (right < s.size()) {
        // 窗口添加 right 指定的元素
        window.add(s[right]);
        right++;

        // 当当前窗口满足条件 [left，right]，记录当前信息
        while (window valid) {
            // 窗口移除 left 指定元素
            window.remove(s[left]);
            // left 增加
            left++;
        }
     }
     */

    /**
     * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
     *
     * @param s 给的字符串 s
     * @param t 需要判断s子串是否满足条件的标准字符串 t
     * @return
     */
    public static String minWindow(String s, String t) {
        if (t == null || t.length() == 0) return "";
        Map<Character, Integer> needMap = new HashMap<>();
        t.chars().mapToObj(ele -> (char)ele).forEach(ele -> needMap.merge(ele, 1, Integer::sum ));

        Map<Character, Integer> current = new HashMap<>();

        int length = s.length();
        int left = 0;
        int right = -1;

        String res = "";

        while (right != length - 1) {
            right++;
            // 放入 map
            current.merge(s.charAt(right), 1, Integer::sum);
            if (!isContainsAll(current, needMap)) {
                continue;
            }
            while(left <= right) {
                // 说明当前 [left, right] 满足条件了
                if (res.length() == 0 || right - left + 1 < res.length()) {
                    // 更替
                    res = s.substring(left, right + 1);
                }
                current.merge(s.charAt(left), -1, Integer::sum);
                left++;
                if (!isContainsAll(current, needMap)) {
                    break;
                }
            }
        }

        return res;
    }

    private static boolean isContainsAll(Map<Character, Integer> current, Map<Character, Integer> need) {
        Integer currentCount;
        for (Map.Entry<Character, Integer> ele : need.entrySet()) {
            currentCount = current.get(ele.getKey());
            if (currentCount == null || currentCount == 0 || currentCount < ele.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 无重复最长子串的长度
     * @param s 字符串
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) return 0;
        int left = 0;
        int right = 0;
        String res = "";
        Map<Character, Integer> count = new HashMap<>();
        while (right < s.length()) {
            count.merge(s.charAt(right), 1, Integer::sum);
            right++;

            while (count.get(s.charAt(right - 1)) > 1) {
                // 说明出现重复字符了
                count.merge(s.charAt(left), -1, Integer::sum);
                left++;
            }
            res = res.length() > right - left ? res : s.substring(left, right);
        }


        return res.length();
    }


    public static void main(String[] args) {
        System.out.println(minWindow("ADOBECODEBANC", "ABC"));
        System.out.println(minWindow("A", "A"));
    }
}
