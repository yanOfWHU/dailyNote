# 分而治之
# 分治法的三个步骤
# 1. 将原先的问题拆分为若干个规模较小，相互独立，但是与问题形式一致的问题
# 2. 如果子问题规模还是较大，则继续拆分；否则直接求解子问题
# 3. 合并子问题的解为原先问题的解
# MapReduce 思想就是基于此的，不断的 map 以及 reduce，将大问题拆分合并解决
from typing import List
import sys


class MergeSolution:

    def merge_sort(self, sort_list):
        """
        3.归并排序
        :param sort_list:  需要排序的列表
        :return:
        """
        if len(sort_list) <= 1:
            return sort_list
        mid = len(sort_list) // 2
        sort_left = self.merge_sort(sort_list[:mid])
        sort_right = self.merge_sort(sort_list[mid:])
        return self._merge(sort_left, sort_right)

    def _merge(self, left_list, right_list):
        i, j = 0, 0
        ret = []
        while i < len(left_list) and j < len(right_list):
            if left_list[i] <= right_list[j]:
                ret.append(left_list[i])
                i += 1
            else:
                ret.append(right_list[j])
                j += 1
        ret.extend(left_list[i:])
        ret.extend(right_list[j:])
        return ret

    def compute_expression(self, input: str) -> List[int]:
        """
        2.给定一个算数表达式，给其中成对插入括号，计算表达式结果
        不考虑除号为 0 的情况
        :param input: 算数表达式，如 1 * 2 - 3 * 4
        :return:
        """

        if input.isdigit():
            return [int(input)]

        ret = []

        for index, char in enumerate(input):
            if char in ['+', '-', '*', '/']:
                left = self.compute_expression(input[:index])
                right = self.compute_expression(input[index + 1:])

                for l in left:
                    for r in right:
                        if char == '+':
                            ret.append(l + r)
                        elif char == '-':
                            ret.append(l - r)
                        elif char == '/':
                            ret.append(l * r)
                        else:
                            if r == 0:
                                return sys.maxsize
                            ret.append(l / r)
        return ret

    def find_max_sequence_sum(self, left_index: int, right_index: int,
                              arr: List):
        """
        在一个整数数组中，求出最大的连续子数组之和
        1. 将序列划分成长度尽可能相等的两半
        2. 求出左半序列和右半序列的最佳序列
        3. 合并起点位于左半，终点位于右半的最大连续和序列，和子问题最优解进行比较
        :param left_index:
        :param right_index:
        :param arr:
        :return:
        """
        if left_index == right_index:
            return arr[left_index]
        mid = (left_index + right_index) // 2
        # 递归求解, 求出左序列和右序列的最大连续数组和
        MAX = max(self.find_max_sequence_sum(left_index, mid, arr),
                  self.find_max_sequence_sum(mid + 1, right_index, arr))
        # 从中间往两边进行计算
        sum_left = arr[mid]
        sum_right = arr[mid + 1]
        t1, t2 = 0, 0
        i = mid
        k = mid + 1
        while i >= left_index:
            t1 += arr[i]
            sum_left = max(sum_left, t1)
            i -= 1
        while k <= right_index:
            t2 += arr[k]
            sum_right = max(sum_right, t2)
            k += 1
        return max(MAX, sum_left + sum_right)

    def HanoiTower(self, count: int, ret_step: list):
        """
        汉诺塔问题
        有 ABC 三个柱子
        A 柱子自底向上有 n->0 共 n+1 个大小递减的圆盘
        要将圆盘全部移动到 C 柱子
        规则是，大圆盘不能叠在小圆盘上
        # 最简单的 考虑只有一个圆盘，直接 0:A->C
        # 其次 两个圆盘,三个步骤 1. 0:A->B 2. 1:A->C 3. 0:B->C
        # 思路, 由于大圆盘不能叠在小圆盘上
        # 那么当有 n 个圆盘时，先把 0->n-2号的圆盘全放在B，再把 n-1号的圆盘放在C
        # 即，要解决有 n 个圆盘的情况，必须要解决有 n-1 的情况(BC两个柱子是等价的)
        # 递归，解决只有 2个的情况
        :param count: A 柱子圆盘数目
        :param ret_step: 移动的步骤
        :return:
        """
        self._move(count, ret_step, 'X', 'Y', 'Z')

    def _move(self, count, ret_step, x, y, z):
        if count == 1:
            ret_step.append((x, z))
            return
        # 先将 count-1 个的圆盘，从 x 移到 y
        self._move(count - 1, ret_step, x, z, y)
        # 最终将最底层的圆盘，从 x 移到 z
        ret_step.append((x, z))
        # 此时问题变更为，y上 count-1 个圆盘，移动到 z 上
        self._move(count - 1, ret_step, y, x, z)


if __name__ == '__main__':
    print('归并排序')
    ms = MergeSolution()
    ls = [5, 10, 2, 70, 24, 16, 56, 65, 26]
    print('origin list: %r' % ls)
    print('sort ret')
    ls = ms.merge_sort(ls)
    print(ls)

    print('计算表达式')
    expr = '5 * 4 - 2 * 3 / 10'.replace(' ', '')
    print('express: %s' % expr)
    print('ret: %r' % ms.compute_expression(expr))

    int_arr = [6, -1, -3, 5, 4, -7, -9, 10, 12, 1, -11, 8, 100]
    print('计算最大连续和')
    print('arr: %r' % int_arr)
    print(ms.find_max_sequence_sum(0, len(int_arr) - 1, int_arr))

    print('汉诺塔问题')
    print('4 个圆盘')
    ret_tuple = []
    ms.HanoiTower(4, ret_tuple)
    print('step count: %s, step: %r' % (len(ret_tuple), ret_tuple))
    print('10 个圆盘')
    ret_tuple = []
    ms.HanoiTower(10, ret_tuple)
    print('step count: %s, step: %r' % (len(ret_tuple), ret_tuple))
