
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
    print(ds.find_max_count_path_in_triangle(list))

