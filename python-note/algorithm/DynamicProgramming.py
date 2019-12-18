from typing import List


class DynamicSolution:

    def find_max_count_path_in_triangle(self, list):
        """
        从一个整形三角形数组中，找到一条从顶部到地步的最长路径(数字之和最大)
        规则限制: 每一步只能往左下或者右下走(数组的话，即只能往下或者右下走)
                    7
                3       8
            8       1       0
        2       7       4       4
    4       5       2       6       5
        7
        3   8
        8   1   0
        2   7   4   4
        4   5   2   6   5
        :param list:
        :return:
        """
        return self._max_sum(0, 0, list, len(list) - 1, [[-1 for i in range(len(list))] for i in range(len(list))])

    def _max_sum(self, i, j, list, height, cached_memory):
        # D(i, j) 表示第 i 行 第 j 个数字，
        # MaxSum(i, j) 表示 D(i, j) 到底部的最佳路径数字之和
        # 对于 N 行的三角形
        # if i == N:
        #   MaxSum(i, j) = D(i, j)
        # else:
        #   MaxSum(i, j) = Max{MaxSum(i + 1, j), MaxSum(i + 1, j + 1)} + D(i ,j)
        if cached_memory[i][j] != -1:
            return cached_memory[i][j]
        if i == height:
            cached_memory[i][j] = list[i][j]
        else:
            x = self._max_sum(i + 1, j, list, height, cached_memory)
            y = self._max_sum(i + 1, j + 1, list, height, cached_memory)
            cached_memory[i][j] = max(x, y) + list[i][j]
        return cached_memory[i][j]

    def find_longest_common_sequence(self, list1:List, list2:List):
        """
        寻找最长公共子序列的长度
        子序列的任意一个字符都可以在原两个序列中找到
        且字符的先后顺序和原串一致
        MaxLen(n, 0) = 0
        MaxLen(0, n) = 0
        if list1[x] == list2[y]:
            MaxLen(x, y) = Max(x-1, y-1) + 1
        else:
            MaxLen(x, y) = max(MaxLen(x, y-1), MaxLen(x-1, y))
        :param list1: 序列1
        :param list2: 序列2
        :return: 
        """
        len1 = len(list1)
        len2 = len(list2)
        cached_memory = [[-1 for i in range(len2 + 1)] for i in range(len1 + 1)]
        self._max_len(list1, list2, 0, 0, cached_memory)
        return cached_memory[0][0]

    def _max_len(self, list1, list2, index1, index2, cached_memory):
        len1 = len(list1)
        len2 = len(list2)
        if len1 == 0 or len2 == 0:
            return 0
        if cached_memory[index1][index2] != -1:
            return cached_memory[index1][index2]
        if len1 == index1 or len2 == index2:
            cached_memory[index1][index2] = 0
        elif list1[index1] == list2[index2]:
            cached_memory[index1][index2] = self._max_len(list1, list2, index1 + 1, index2 + 1, cached_memory) + 1
        else:
            cached_memory[index1][index2] = max(
                self._max_len(list1, list2, index1 + 1, index2, cached_memory),
                self._max_len(list1, list2, index1, index2 + 1, cached_memory)
            )
        return cached_memory[index1][index2]

    def find_longest_increase_sequence(self, list:List):
        """
        在一个序列中 寻找最长递增序列
        输出最长递增序列的长度
        max_len(n) 初始值都为 1
        max_len(x) = max(max_len(y)) + 1 ; 其中 y < x 且 list[y] <= list[x], 如果不存在这样的 y, 则为 1
        使用非递归模式，2 层便利
        每层循环都循环设置 max_len()，为其值加 1
        :param list:
        :return:
        """
        if list is None or len(list) == 0:
            return 0
        cached_memory = [1 for i in range(len(list))]
        return self._max_increase_len(list, 0, cached_memory)

    def _max_increase_len(self, list, index, cached_memory):
        _INDEX = index
        while _INDEX < len(list):
            tmp = _INDEX + 1
            while tmp < len(list):
                if list[_INDEX] < list[tmp]:
                    cached_memory[tmp] = max(cached_memory[tmp], cached_memory[_INDEX] + 1)
                tmp = tmp + 1
            _INDEX = _INDEX + 1
        return max(cached_memory.__iter__())

if __name__ == '__main__':
    ds = DynamicSolution()
    print("计算三角形自顶向下的最长路径")
    list = [[0 for i in range(5)] for i in range(5)]
    list[0][0] = 7
    list[1][0] = 3
    list[1][1] = 8
    list[2][0] = 8
    list[2][1] = 1
    list[2][2] = 0
    list[3][0] = 2
    list[3][1] = 7
    list[3][2] = 4
    list[3][3] = 4
    list[4][0] = 4
    list[4][1] = 5
    list[4][2] = 2
    list[4][3] = 6
    list[4][4] = 5
    print("list: %r" % list)
    print("最长路径: %d" % ds.find_max_count_path_in_triangle(list))

    print("计算最长公共子序列的长度")
    list1 = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
    list2 = [1, 0, 2, 4, 5, 6, 3, 7, 10, 9, 8, 11, 15, 14, 12, 20, 13, 19, 16, 17, 18, 21, 22, 23]
    print("list1: %r" % list1)
    print("list2: %r" % list2)
    print("maxCommonSubSequenceLen %d" % ds.find_longest_common_sequence(list1, list2))

    print("最长递增序列")
    increase_list = [2, 3, 4, 0 ,6, 1, -1, 7, 8, 9, 10, -5, 7, 8, 9, 10, 11, 12, 13 ,14 ,12, 15]
    # increase_list = [  1, 10, -5, 7,  11]
    print('list: %r' % increase_list)
    print("maxIncreaseSequenceLen %d" % ds.find_longest_increase_sequence(increase_list))

