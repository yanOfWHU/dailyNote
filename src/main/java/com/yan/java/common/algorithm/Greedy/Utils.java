package com.yan.java.common.algorithm.Greedy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.yan.java.common.util.CommonUtils.print;

public class Utils {
    /**
     * 贪心算法是动态规划的一种特例
     * 动态规划是在暴力解法的基础下，通过递归(自顶向下/自底向上)的备忘录来实现的，实现过程中通常需要进行数据之间的比较和取最值
     * 而贪心算法则是一种动态规划特例，在可以使用贪心算法的时候，每次我们都只需要取最值，最终算法可以达到线性复杂度
     */

    /**
     *
     * 给定一个区间的集合，找到需要移除区间的最小数量，使剩余区间互不重叠。

     注意:

     可以认为区间的终点总是大于它的起点。
     区间 [1,2] 和 [2,3] 的边界相互“接触”，但没有相互重叠。

     来源：力扣（LeetCode）
     链接：https://leetcode-cn.com/problems/non-overlapping-intervals
     著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。

     直接获取这个结果很难，但是可以获取其同等含义的结果
     -- 获取区间集合中，最多有几个互不相交的区间
     获取结果后，在把区间数量减去这个值，就是最少区间数量

     ** 如何获取区间集合中，互不相交的最大区间集合数量
     ---> 贪婪算法，取范围最广的，然后不断剔除交集

     思路，首先可以对区间数据集合，按照 end 进行排序
     1、取 end 值最小的区间
     2、遍历所有区间，将和这区间取交集的元素清除
     3、重复步骤1、2 直到区间数量为空
     * @param intervals
     * @return
     */
    public int eraseOverlapIntervals(int[][] intervals) {
        List<Interval> intervalList = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            intervalList.add(new Interval(intervals[i][0], intervals[i][1]));
        }
        return intervalList.size() - maxNotInsectCount(intervalList);
    }

    private int maxNotInsectCount(List<Interval> intervalList) {
        if (intervalList.size() <= 1) return intervalList.size();
        // 这里首先按照 end 大小排序(倒序排序比较好 remove)，还需要按照 from 大小排序
        intervalList.sort((e1, e2) -> {
            if (e1.end != e2.end) {
                return e2.end - e1.end;
            } else {
                return e2.from - e1.from;
            }
        });
        // 获取最后一个
        int count = intervalList.size();
        while (!intervalList.isEmpty()) {
            Interval endEle = intervalList.remove(intervalList.size() - 1);
            for (int i = intervalList.size() - 1; i >= 0; i--) {
                if (isInsect(intervalList.get(i), endEle)) {
                    intervalList.remove(i);
                    count--;
                } else {
                    break;
                }
            }
        }
        return count;
    }

    private boolean isInsect(Interval interval, Interval endEle) {
        return interval.from < endEle.end;
    }


    /**
     在二维空间中有许多球形的气球。对于每个气球，提供的输入是水平方向上，气球直径的开始和结束坐标。由于它是水平的，所以纵坐标并不重要，因此只要知道开始和结束的横坐标就足够了。开始坐标总是小于结束坐标。

     一支弓箭可以沿着 x 轴从不同点完全垂直地射出。在坐标 x 处射出一支箭，若有一个气球的直径的开始和结束坐标为 xstart，xend， 且满足  xstart ≤ x ≤ xend，则该气球会被引爆。可以射出的弓箭的数量没有限制。 弓箭一旦被射出之后，可以无限地前进。我们想找到使得所有气球全部被引爆，所需的弓箭的最小数量。

     来源：力扣（LeetCode）
     链接：https://leetcode-cn.com/problems/minimum-number-of-arrows-to-burst-balloons
     著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。


     解题：退化成
     在一个区间集合中，获取整体不重叠区间的数量 如[1, 2][2, 3] 可以看成区间 [1, 3]，这时候就只需要一根箭
     * @param points
     * @return
     */
    public int findMinArrowShots(int[][] points) {
        List<Interval> pointsList = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            pointsList.add(new Interval(points[i][0], points[i][1]));
        }
        pointsList.sort((ele1, ele2) -> {
            if (ele1.from == ele2.from) {
                return ele2.end > ele1.end ? 1 : -1;
            } else {
                return ele1.from > ele2.from ? 1 : -1;
            }
        });
        int count = 0;
        for (int i = 0; i < pointsList.size(); i++) {
            Interval current = pointsList.get(i);
            while (i < pointsList.size()-1 && canMerge(current, pointsList.get(i+1))) {
                current = new Interval(pointsList.get(i+1).from, Math.min(current.end, pointsList.get(i+1).end));
                i++;
            }
            count++;
        }
        return count;
    }

    private boolean canMerge(Interval current, Interval next) {
        // current.from <= next.from
        return next.from <= current.end;
    }

    private static class Interval {
        public int from;
        public int end;
        public Interval(int from, int end) {
            this.from = from;
            this.end = end;
        }
        public int getEnd() {
            return end;
        }
    }

    public static void main(String[] args) {
        Utils ins = new Utils();
        print(ins.eraseOverlapIntervals(new int[][]{{1,2}, {2,3}, {3, 4}, {1, 3}}));
        print(ins.eraseOverlapIntervals(new int[][]{{1,2}, {1,2}, {1, 2}}));
        print(ins.eraseOverlapIntervals(new int[][]{{1,2}, {2, 3}}));
        print(ins.findMinArrowShots(new int[][]{{-2147483646, -2147483645}, {2147483646, 2147483647}}));
    }

}
