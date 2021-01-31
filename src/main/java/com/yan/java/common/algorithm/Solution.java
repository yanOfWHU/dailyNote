package com.yan.java.common.algorithm;

import com.google.common.collect.Lists;
import com.yan.java.common.util.CommonUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yanxujiang on 2020-08-24.
 * TODO
 * basic knowledge:
 * '0'->'9' -> 48->57
 * 'A'->'Z' -> 65->90
 * 'a'->'z' -> 97->122
 * @see java.util.PriorityQueue 优先级队列 默认是最小堆 可以传递 Comparator 为最大堆
 * @see java.util.Queue 为队列
 * @see java.util.Deque 为循环队列
 * @see java.util.LinkedList 为链表，同时支持队列和栈操作
 *
 */
public class Solution {

  static class Node<T> {

    public Node(T val) {
      this.val = val;
      this.next = null;
    }

    T val;
    Node<T> next;
  }


  /**
   * 这种链表实现方式是没有带哨兵头节点的
   */
  static class MyLinkedList<T> {
    int size;
    Node<T> head;

    public MyLinkedList(Node<T> head) {
      this.head = head;
      size = 1;
    }

    public Node<T> get(int index) {
      if (index < 0) throw new IllegalArgumentException();
      Node<T> ret = head;
      int curIndex = 0;
      while (curIndex != index && ret != null) {
        ret = ret.next;
        curIndex++;
      }
      return ret;
    }

    public void addAtHead(T val) {
      Node<T> node = new Node<>(val);
      node.next = head;
      head = node;
      size++;
    }

    public void addAtTail(T val) {
      Node<T> node = new Node<>(val);
      Node<T> tmp = head;
      while (tmp.next != null) {
        tmp = tmp.next;
      }
      tmp.next = node;
      size++;
    }

    public void addAtIndex(int index, T val) {
      if (index < 0) throw new IllegalArgumentException();
      if (index == 0) {
        addAtHead(val);
      } else {
        Node<T> node = new Node<>(val);
        Node<T> tmp = head;
        int curIndex = 0;
        while (curIndex < index - 1 && tmp != null) {
          tmp = tmp.next;
          curIndex++;
        }
        if (tmp == null) return;
        node.next = tmp.next;
        tmp.next = node;
        size++;
      }
    }

    public void removeElements(T val) {
      Node<T> vHead = new Node<>(null);
      vHead.next = head;
      Node<T> tmp = head;
      while (tmp != null && Objects.equals(tmp.val, val)) {
        vHead.next = tmp.next;
        tmp = tmp.next;
        size--;
      }
      Node<T> next;
      while (tmp != null) {
        next = tmp.next;
        if (next != null && Objects.equals(next.val, val)) {
          tmp.next = next.next;
          size--;
        } else {
          tmp = tmp.next;
        }
      }
    }

    public void deleteAtIndex(int index) {
      if (index > size) throw new IllegalArgumentException();
      Node<T> tmp = head;
      if (index == 0) {
        head = head.next;
        tmp.next = null;
      }
      int curIndex = 0;
      while (curIndex < index - 1) {
        tmp = tmp.next;
        curIndex++;
      }
      Node<T> next = tmp.next;
      assert next != null;
      tmp.next = next.next;
      next.next = null;
      size--;
    }

    public void reverse() {
      if (head == null) throw new IllegalArgumentException();
      Node<T> virtualHead = new Node<>(null);
      virtualHead.next = head;
      Node<T> cur = head;
      Node<T> before = null;
      while (cur != null) {
        virtualHead.next = cur.next;
        cur.next = before;
        before = cur;
        cur = virtualHead.next;
      }
      head = before;
    }

    public void oddEvenList() {
      if (head == null || head.next == null) return;
      Node<T> even = new Node<>(null);
      even.next=null;
      Node<T> odd = new Node<>(null);
      odd.next = null;

      int index = 0;
      Node tmp = head;
      Node evenTmp = even;
      Node oddTmp = odd;
      while (tmp != null) {
        if (index % 2 == 0) {
          // 奇数
          evenTmp.next = tmp;
          evenTmp = evenTmp.next;
        } else {
          // 偶数
          oddTmp.next = tmp;
          oddTmp = oddTmp.next;
        }
        Node node = tmp.next;
        // 此处需要断开节点破坏原链表结构 不然会形成环
        tmp.next = null;
        tmp = node;
        index++;
      }
      evenTmp.next = odd.next;
      head = even.next;
    }


    @Override
    public String toString() {
      if (size == 0) return "null LinkedList";
      StringBuilder sb = new StringBuilder();
      Node<T> tmp = head;
      for (int i = 0; i < size; i++) {
        sb.append(tmp.val);
        if (i != size - 1) {
          sb.append(" --> ");
        }
        tmp = tmp.next;
      }
      return sb.toString();
    }

    public boolean isPalindrome() {
      // 为了不破坏原结构 深拷贝一份
      MyLinkedList<T> copy = deepCopy();
      if (copy.head == null || copy.head.next == null) return true;
      MyLinkedList<T> half = getHalf().right;
      half.reverse();
      Node<T> rHead = half.head;
      Node<T> lHead = head;
      while (rHead != null) {
        if (Objects.equals(rHead.val, lHead.val)) {
          rHead = rHead.next;
          lHead = lHead.next;
        } else {
          return false;
        }
      }
      return true;
    }

    private MutablePair<MyLinkedList<T>, MyLinkedList<T>> getHalf() {
      MutablePair<MyLinkedList<T>, MyLinkedList<T>> ret = new MutablePair<>();
      if (head == null) {
        ret.setLeft(new MyLinkedList<>(null));
        ret.setRight(new MyLinkedList<>(null));
        return ret;
      }
      if (head.next == null) {
        ret.setLeft(this);
        ret.setRight(new MyLinkedList<>(null));
        return ret;
      }

      Node<T> slow = new Node<>(null);
      slow.next = head;
      Node<T> fast = new Node<>(null);
      fast.next = head;
      while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
      }
      ret.right = new MyLinkedList<>(slow.next);
      ret.right.size = this.size / 2;
      this.size = (this.size + 1) / 2;
      slow.next = null;
      ret.left = this;
      return ret;
    }

    public MyLinkedList<T> deepCopy() {
      if (head == null) return new MyLinkedList<>(null);
      Node<T> node = new Node<>(head.val);
      MyLinkedList<T> copy = new MyLinkedList<>(node);
      copy.size = 1;
      Node<T> tmp = head;
      while (tmp.next != null) {
        // TODO 这里使用 addAtTail 有点效率问题
        copy.addAtTail(tmp.next.val);
        tmp = tmp.next;
      }
      return copy;
    }

  }

  public class ListNode {
    int val;
    ListNode next;
    ListNode prev;
    ListNode(int x) { val = x; }
  }


  /**
   * 双向链表实现
   */
  class MyDoubleLinkedList {
    int size;
    // sentinel nodes as pseudo-head and pseudo-tail
    ListNode head, tail;
    public MyDoubleLinkedList() {
      size = 0;
      head = new ListNode(0);
      tail = new ListNode(0);
      head.next = tail;
      tail.prev = head;
    }

    /** Get the value of the index-th node in the linked list. If the index is invalid, return -1. */
    public int get(int index) {
      // if index is invalid
      if (index < 0 || index >= size) return -1;

      // choose the fastest way: to move from the head
      // or to move from the tail
      ListNode curr = head;
      if (index + 1 < size - index)
        for(int i = 0; i < index + 1; ++i) curr = curr.next;
      else {
        curr = tail;
        for(int i = 0; i < size - index; ++i) curr = curr.prev;
      }

      return curr.val;
    }

    /** Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list. */
    public void addAtHead(int val) {
      ListNode pred = head, succ = head.next;

      ++size;
      ListNode toAdd = new ListNode(val);
      toAdd.prev = pred;
      toAdd.next = succ;
      pred.next = toAdd;
      succ.prev = toAdd;
    }

    /** Append a node of value val to the last element of the linked list. */
    public void addAtTail(int val) {
      ListNode succ = tail, pred = tail.prev;

      ++size;
      ListNode toAdd = new ListNode(val);
      toAdd.prev = pred;
      toAdd.next = succ;
      pred.next = toAdd;
      succ.prev = toAdd;
    }

    /** Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted. */
    public void addAtIndex(int index, int val) {
      // If index is greater than the length,
      // the node will not be inserted.
      if (index > size) return;

      // [so weird] If index is negative,
      // the node will be inserted at the head of the list.
      if (index < 0) index = 0;

      // find predecessor and successor of the node to be added
      ListNode pred, succ;
      if (index < size - index) {
        pred = head;
        for(int i = 0; i < index; ++i) pred = pred.next;
        succ = pred.next;
      }
      else {
        succ = tail;
        for (int i = 0; i < size - index; ++i) succ = succ.prev;
        pred = succ.prev;
      }

      // insertion itself
      ++size;
      ListNode toAdd = new ListNode(val);
      toAdd.prev = pred;
      toAdd.next = succ;
      pred.next = toAdd;
      succ.prev = toAdd;
    }

    /** Delete the index-th node in the linked list, if the index is valid. */
    public void deleteAtIndex(int index) {
      // if the index is invalid, do nothing
      if (index < 0 || index >= size) return;

      // find predecessor and successor of the node to be deleted
      ListNode pred, succ;
      if (index < size - index) {
        pred = head;
        for(int i = 0; i < index; ++i) pred = pred.next;
        succ = pred.next.next;
      }
      else {
        succ = tail;
        for (int i = 0; i < size - index - 1; ++i) succ = succ.prev;
        pred = succ.prev.prev;
      }

      // delete pred.next
      --size;
      pred.next = succ;
      succ.prev = pred;
    }
  }

  /**
   * 最长回文字符串 longest-palindromic-substring
   * 动态规划解法
   */
  public static String longestPalindrome(String s) {
    int len = s.length();
    // dp[i][j] 表示 s.subString(i, j+1) 是否为回文串
    boolean[][] dp = new boolean[len][len];
    String ret = "";
    // 枚举子串的长度 i+1
    for (int i = 0; i < len; i++) {
      // 枚举子串的初始位置 子串的结束位置为 begin + i
      for (int begin = 0; begin < len; begin++) {
        int end = begin + i;
        if (end >= len) break;
        if (i == 0) {
          dp[begin][end] = true;
        } else if (i == 1) {
          dp[begin][end] = (s.charAt(begin) == s.charAt(begin + 1));
        } else {
          dp[begin][end] = (dp[begin + 1][end - 1] && s.charAt(begin) == s.charAt(end));
        }
        if (dp[begin][end] && i + 1 > ret.length()) {
          ret = s.substring(begin, end + 1);
        }
      }
    }
    return ret;
  }

  /**
   * 最长回文字符串 中心扩展方法
   * 长度为1 和 长度为2 的字符串为最小字符串，然后向两边扩展
   */
  public static String longestPalindrome2(String s) {
    if (s.length() == 1) return s;
    if (s.length() == 2) return s.charAt(0) == s.charAt(1) ? s : s.charAt(0) + "";
    int start = 0;
    int end = 0;
    for (int i = 0; i < s.length(); i++) {
      int len1 = centerExpand(s, i, i);
      int len2 = centerExpand(s, i, i + 1);
      int len = Math.max(len1, len2);
      if (len > end - start) {
        start = i - (len - 1) / 2;
        end = i + len / 2;
      }
    }
    return s.substring(start, end + 1);
  }

  private static int centerExpand(String s, int from, int end) {
    int l = from;
    int r = end;
    while (l >= 0 && r <= s.length() - 1 && s.charAt(l) == s.charAt(r)) {
      l--;
      r++;
    }
    return r - l + 1;
  }

  /**
   * implement-strstr
   * 实现 strStr() 即 string.indexOf(str)
   * 在一个字符串中寻找 target 字符串第一次出现的位置
   * 如果没有，则返回 -1
   */
  public static int strStr(String origin, String target) {
    // 构造 next 数组
    int[] next = buildNext(target);
    int i = 0; // 文本串指针用来指示文本串
    int lenOri = origin.length();
    int j = 0; // 模式串指针用来指示模式串
    int lenTar = target.length();
    while (i < lenOri && j < lenTar) { // 从左向右匹配
      if (j < 0 || origin.charAt(i) == target.charAt(j)) { // 若匹配，或者 origin 已经移除最左侧
        i++;
        j++;
      } else {
        j = next[j]; // 模式串又移
      }
    }
    return j == lenTar ? i - j : -1;
  }

  // 构造 next 数组，字符串中最长公共前后缀的长度
  public static int[] buildNext(String s) {
    if (s == null || s.length() == 0) return new int[0];
    int len = s.length();
    int point = 0;
    int[] next = new int[len];

    // 规定 next[0] = -1
    next[0] = -1;

    int t = -1;

    while (point < len - 1) {
      if (t < 0 || s.charAt(point) == s.charAt(t)) {
        point++;
        t++;
        next[point] = t;
      } else {
        t = next[t];
      }
    }
    return next;
  }

  /**
   * 给定一个只有正整数的数组 获取最短长度的子数组其满足 sum >= s
   * 暴力解法
   */
  public static int minSubArrayLen(int s, int[] nums) {
    int min = Integer.MAX_VALUE;
    int l = 0;
    int r = 0;
    int sum = 0;
    while (l < nums.length) {
      sum += nums[r];
      while (r < nums.length - 1 && sum < s) {
        r++;
        sum += nums[r];
      }
      if (sum < s) return min == Integer.MAX_VALUE ? 0 : min;
      int t_min = r - l + 1;
      min = min < t_min ? min : t_min;
      l++;
      r = l;
      sum = 0;
    }
    return min == Integer.MAX_VALUE ? 0 : min;
  }

  public static List<List<Integer>> generate(int numRows) {
    List<List<Integer>> ret = new ArrayList<>();
    if (numRows <= 0) return ret;
    for (int i = 1; i <= numRows; i++) {
      ret.add(generate(ret, i));
    }
    return ret;
  }

  public static List<Integer> generate(List<List<Integer>> ret, int row) {
    List<Integer> list = new ArrayList<>();
    if (row == 1) {
      list.add(1);
    } else if (row == 2) {
      list.add(1);
      list.add(1);
    } else {
      List<Integer> upList = ret.get(row - 2);
      for (int i = 1; i <= row; i++) {
        if (i == 1 || i == row) {
          list.add(1);
        } else {
          int l = upList.get(i - 2);
          int r = upList.get(i - 1);
          list.add(l + r);
        }
      }
    }
    return list;
  }

  /**
   * 寻找旋转数组中的最小值
   * 1 2 3 4 5 6 7 --> 4 5 6 7 1 2 3
   *
   * @param nums
   * @return
   */
  public static int findMin(int[] nums) {
    return findMin(nums, 0, nums.length - 1);
  }

  public static int findMin(int[] nums, int l, int r) {
    if (l == r) return nums[l];
    if (l + 1 == r) return Math.min(nums[l], nums[r]);
    int mid = (l + r) / 2;
    int lv = nums[l];
    int rv = nums[r];
    int mv = nums[mid];
    int minV;
    if (mv > lv) {
      minV = lv;
      return Math.min(minV, findMin(nums, mid, r));
    } else {
      minV = rv;
      return Math.min(minV, findMin(nums, l, mid));
    }
  }

  public static int removeDuplicates(int[] nums) {
    if (nums == null || nums.length == 0) return 0;
    if (nums.length == 1) return 1;

    int l = 0;
    int r = 0;
    int retLen = 0;
    while (r < nums.length) {
      int curV = nums[l];
      if (curV == nums[r]) {
        // 二者相等
        r++;
      } else {
        nums[retLen++] = curV;
        l = r;
        r = r + 1;
      }
    }
    nums[retLen++] = nums[l];
    return retLen;
  }

  public static <T> MyLinkedList<T> constructLinkedList(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    Node<T> head = new Node<>(list.get(0));
    MyLinkedList<T> myLinkedList = new MyLinkedList<>(head);
    int len = list.size();
    for (int i = 1; i < len; i++) {
      myLinkedList.addAtTail(list.get(i));
    }
    return myLinkedList;
  }

  /**
   * 判断一个数是否是快乐数
   * 使用双指针方法
   * 快乐数定义：每一次将该数替换为它每个位置上的数字的平方和，然后重复这个过程直到这个数变为 1，也可能是 无限循环 但始终变不到 1。如果 可以变为  1，那么这个数就是快乐数。
   */
  public static boolean isHappy(int n) {
    if (n == 1) return true;
    int slow = getRet(n);
    int fast = getRet(slow);
    while (slow != fast) {
      slow = getRet(slow);
      fast = getRet(getRet(fast));
    }
    if (slow == 1) {
      return true;
    } else {
      return false;
    }
  }

  public static int getRet(int n) {
    int cur = n;
    int ret = 0;
    int mod;
    while(cur > 0) {
      mod = cur % 10;
      ret += mod * mod;
      cur = (cur - mod)/10;
    }
    return ret;
  }

  /**
   * 判断两个字符串是否是同构字符串
   * 给定两个字符串 s 和 t，判断它们是否是同构的。
   * 如果 s 中的字符可以被替换得到 t ，那么这两个字符串是同构的。
   */
  public static boolean isIsomorphic(String s, String t) {
    return isIsomorphicHelper(s, t) && isIsomorphicHelper(t, s);
  }

  private static boolean isIsomorphicHelper(String s, String t) {
    if (s.length() != t.length()) return false;
    int n = s.length();
    HashMap<Character, Character> map = new HashMap<>();
    for (int i = 0; i < n; i++) {
      char c1 = s.charAt(i);
      char c2 = t.charAt(i);
      if (map.containsKey(c1)) {
        if (map.get(c1) != c2) {
          return false;
        }
      } else {
        map.put(c1, c2);
      }
    }
    return true;
  }

  /**
   * 另一种方式判定两个字符串是否是同构字符串
   * 类比现实中数字一个说英文，一个说法语，中国人如何知道他们说的是什么呢？
   * 将其都翻译成中国人知道的
   * 如 add/egg a/e -> 1 d/g -> 2 所以是
   * 如 aa/ab -> a/a ->1 a/b 因为a映射成了1，b尚未映射，所以不是
   */
  public static boolean isIsomorphic2(String s, String t) {
    int n = s.length();
    int[] mapS = new int[128];
    int[] mapT = new int[128];
    for (int i = 0; i < n; i++) {
      char c1 = s.charAt(i);
      char c2 = t.charAt(i);
      //当前的映射值是否相同
      if (mapS[c1] != mapT[c2]) {
        return false;
      } else {
        //是否已经修改过，修改过就不需要再处理
        if (mapS[c1] == 0) {
          mapS[c1] = i + 1;
          mapT[c2] = i + 1;
        }
      }
    }
    return true;
  }


  public static class TreeNode<T> {
    T val;
    TreeNode<T> left;
    TreeNode<T> right;
    TreeNode() {}
    TreeNode(T val) { this.val = val; }
    TreeNode(T val, TreeNode<T> left, TreeNode<T> right) {
      this.val = val;
      this.left = left;
      this.right = right;
    }



    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      preOrderTraversal(sb);
      return sb.toString();
    }

    public void preOrderTraversal(StringBuilder sb) {
      sb.append(val);
      sb.append(" ");
      if (this.left != null) {
        this.left.preOrderTraversal(sb);
        sb.append(" ");
      } else {
        sb.append("null");
        sb.append(" ");
      }
      if (this.right != null ){
        this.right.preOrderTraversal(sb);
        sb.append(" ");
      } else {
        sb.append("null");
        sb.append(" ");
      }
    }
  }


  /**
   * 寻找重复子树
   * https://leetcode-cn.com/problems/find-duplicate-subtrees/solution/xun-zhao-zhong-fu-de-zi-shu-by-leetcode/
   * 给定一棵二叉树，返回所有重复的子树。对于同一类的重复子树，你只需要返回其中任意一棵的根结点即可。
   * 两棵树重复是指它们具有相同的结构以及相同的结点值。
   */
  public static List<TreeNode<Integer>> findDuplicateSubtrees(TreeNode<Integer> root) {
    Map<String, Integer> trees = new HashMap<>();
    Map<Integer, Integer> count = new HashMap<>();
    List<TreeNode<Integer>> ans = new ArrayList<>();
    MutableInt t = new MutableInt(1);
    lookup(root, trees, count, ans, t);
    return ans;
  }

  private static int lookup(TreeNode<Integer> node, Map<String, Integer> trees,
      Map<Integer, Integer> count, List<TreeNode<Integer>> ans, MutableInt t) {
    if (node == null) return 0;
    // 前序遍历 获取一个树的前序遍历序列号
    String serial = node.val + "," + lookup(node.left, trees, count, ans, t) + "," + lookup(node.right, trees, count,
        ans, t);
    // 每个节点的 uid 都是不同的
    int uid = trees.computeIfAbsent(serial, ori -> {
      int ret = t.getValue();
      t.add(1);
      return ret;
    });

    count.put(uid, count.getOrDefault(uid, 0) + 1);
    if (count.get(uid) == 2)
      ans.add(node);
    return uid;
  }
  public static void clear(StringBuilder sb) {
    sb.delete(0, sb.length());
  }

  private static TreeNode<Integer> constructDuplicateSubTree() {
    TreeNode<Integer> root = new TreeNode<>(1);
    TreeNode<Integer> node = new TreeNode<>(4);
    node = new TreeNode<>(2, node, null);
    root.left = node;

    TreeNode<Integer> rNode = new TreeNode<>(4);
    rNode = new TreeNode<>(2, rNode, null);
    TreeNode<Integer> rrNode = new TreeNode<>(4);
    rrNode = new TreeNode<>(3, rNode, rrNode);
    root.right = rrNode;

    return root;
  }

  /**
   * 四数之和II 给定四个长度一致的数组 找出4数之和为0的数量 包括重复的对数s
   */
  public static int fourSumCount(int[] A, int[] B, int[] C, int[] D) {
    int len = A.length;
    Map<Integer, Integer> map1 = new HashMap<>();
    int a;
    int b;
    int c;
    int d;
    int sum;
    for (int item : A) {
      a = item;
      for (int j = 0; j < len; j++) {
        b = B[j];
        sum = a + b;
        if (map1.get(sum) == null) {
          map1.put(sum, 1);
        } else {
          map1.put(sum, map1.get(sum) + 1);
        }
      }
    }
    Map<Integer, Integer> map2 = new HashMap<>();
    for (int i = 0;i<len;i++) {
      c = C[i];
      for (int j = 0;j<len;j++) {
        d = D[j];
        sum = c + d;
        if (map2.get(sum) == null) {
          map2.put(sum, 1);
        } else {
          map2.put(sum, map2.get(sum) + 1);
        }
      }
    }

    int[] total = new int[1];
    map1.keySet().forEach(key -> {
      int value = map1.get(key);
      Integer match = map2.get(0 - key);
      if (match != null) {
        total[0] += value * match;
      }
    });
    return total[0];
  }

  /**
   * 前 k 个高频词
   * 假定给出的数组有唯一解(即第 k 个高频词只有一个)
   * 方法可以修改为 先遍历一遍获取 k->count 的 hashMap 然后再利用最小堆
   * 边遍历边添加不是很好的方式
   */
  public static int[] topKFrequent(int[] nums, int k) {
    if (k < 1) return new int[0];
    class InternalPair {

      Integer k;

      Integer v;

      public InternalPair(Integer k, Integer v) {
        this.k = k;
        this.v = v;
      }

      public Integer getV() {
        return v;
      }

      public Integer getK() {
        return k;
      }

      public void incV() {
        this.v = v + 1;
      }
    }
    PriorityQueue<InternalPair> queue = new PriorityQueue<>(k, Comparator.comparing(InternalPair::getV));
    Map<Integer, InternalPair> count = new HashMap<>();
    Map<Integer, Boolean> inQueue = new HashMap<>();
    int size = 0;
    InternalPair tmp;
    InternalPair cur;
    for (int i : nums) {
      cur = count.get(i);
      if (cur == null) {
        tmp = new InternalPair(i, 1);
        count.put(i, tmp);
        cur = tmp;
      } else {
        // 正常情况下不要这样操作 提交到堆(即优先级队列后)，不要修改对象值 否则就要像下面的操作一下 先删除再添加一遍
        cur.incV();
        if (Boolean.TRUE.equals(inQueue.get(i))) {
          queue.remove(cur);
          queue.offer(cur);
        }
      }
      if (size < k && !Boolean.TRUE.equals(inQueue.get(i))) {
        queue.add(cur);
        size++;
        inQueue.put(i, true);
      } else {
        // 队列满了
        if (cur.getV() > queue.peek().getV() && !Boolean.TRUE.equals(inQueue.get(i))) {
          inQueue.put(queue.remove().getK(), false);
          queue.add(cur);
          inQueue.put(i, true);
        }
      }
    }

    return listToArr(queue.stream().map(InternalPair::getK).collect(Collectors.toList()));
  }

  private static int[] listToArr(List<Integer> list) {
    int[] ret = new int[list.size()];
    for(int i = 0; i < list.size(); i++) {
      ret[i] = list.get(i);
    }
    return ret;
  }

  private static List<Integer> arrToList(int[] arr) {
    List<Integer> list = new ArrayList<>(arr.length);
    for(int i : arr) {
      list.add(i);
    }
    return list;
  }

  /**
   * number-of-islands
   * 在一个平面数组中 '1' 表示陆地， '0' 表示海洋
   * 获取岛屿的数量(单陆地，或者陆地相连为岛屿)
   */
  public static int numOfIslands(char[][] grid) {
    if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;
    int n = grid.length;
    int m = grid[0].length;
    boolean[][] visited = new boolean[n][m];
    int timesBFS = 0;
    char cur;
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n;j++) {
        if(visited[j][i]) continue;
        cur = grid[j][i];
        if (cur == '1') {
          // 其实这里遍历更像 DFS
          BFSGrid(i, j, grid, m, n, visited);
          timesBFS++;
        }
        visited[j][i] = true;
      }
    }
    return timesBFS;
  }

  private static void BFSGrid(int i, int j, char[][] grid, int m, int n, boolean[][] visited) {
    if (i < 0 || i >= m || j < 0 || j >= n) return;
    char cur = grid[j][i];
    if (cur == '0' || visited[j][i]) return;
    visited[j][i] = true;
    BFSGrid(i - 1, j, grid, m, n, visited);
    BFSGrid(i + 1, j, grid, m, n, visited);
    BFSGrid(i, j - 1, grid, m, n, visited);
    BFSGrid(i, j + 1, grid, m, n, visited);
  }

  /**
   * 广度优先遍历
   * 用途：
   *    1、遍历
   *    2、寻找最短路径  因为是广度遍历，所以访问到某一个结点时, 其访问的 depth 就是最短路径
   */
  public static <T> Pair<List<TreeNode<T>>, Integer> BFSTemplate(TreeNode<T> root, TreeNode<T> target) {
    List<TreeNode<T>> ret = new ArrayList<>();
    Queue<TreeNode<T>> queue = new LinkedList<>();
    queue.offer(root);
    TreeNode<T> tmp;
    int retStep = 0;
    int step = 0;
    while (!queue.isEmpty()) {
      step += 1;
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        tmp = queue.poll();
        ret.add(tmp);
        if (Objects.equals(tmp, target)) retStep = step;
        // 这里是二叉树 所以只需要判断两个 left/right
        // 如果是图，这里就是结点的 neighbor，遍历所有的 neighbor 然后加到 队列中
        assert tmp != null;
        if (tmp.left != null) {
          queue.offer(tmp.left);
        }
        if (tmp.right != null) {
          queue.offer(tmp.right);
        }
      }
    }
    return new MutablePair<>(ret, retStep);
  }

  /**
   * @see com.yan.java.common.algorithm.Solution#BFSTemplate(TreeNode, TreeNode)
   * 锁的初始为 0000 每次可以旋转1位置到相邻位置 如 0001 0009 等
   * deadEnd 为旋转到该位置就会出现死锁 无法继续打开锁不可通的道路
   * @param deadEnds
   * @param target
   * @return
   */
  public static int openLock(String[] deadEnds, String target) {
    if (target == null || target.length() != 4) return -1;
    Set<String> dead = new HashSet<>(Arrays.asList(deadEnds));
    Set<String> visited = new HashSet<>();
    if (dead.contains("0000")) return -1;
    Queue<String> queue = new LinkedList<>();
    queue.offer("0000");
    visited.add("0000");
    int step = 0;
    StringBuilder sb = new StringBuilder();
    String s;
    while (!queue.isEmpty()) {
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        s = queue.poll();
        if (target.equals(s)) return step;
        inQueue(s, dead, queue, sb, visited);
      }
      step++;
    }
    return -1;
  }

  private static void inQueue(String cur, Set<String> dead, Queue<String> queue, StringBuilder sb,
      Set<String> visited) {
    String s;
    for (int index = 0; index < 4; index++) {
      char[] arr = cur.toCharArray();
      char ori = arr[index];
      arr[index] = roundChar(ori, true);
      sb.append(arr);
      s = sb.toString();
      if (!dead.contains(s) && !visited.contains(s)) {
        queue.offer(s);
        visited.add(s);
      }
      clear(sb);
      arr[index] = roundChar(ori, false);
      sb.append(arr);
      s = sb.toString();
      if (!dead.contains(s) && !visited.contains(s)) {
        queue.offer(s);
        visited.add(s);
      }
      clear(sb);
    }
  }

  /**
   * '0' -> '9' 的相邻 char
   * '0' 的相邻为 '1' 和 '9'
   */
  private static char roundChar(char a, boolean left) {
    if (a < 48 || a > 57) throw new IllegalArgumentException();
    if (left) {
      if (a == '0') return '9';
      return (char)(a - 1);
    } else {
      if (a == '9') return '0';
      return (char)(a + 1);
    }
  }


  /**
   * @see com.yan.java.common.algorithm.Solution#BFSTemplate(TreeNode, TreeNode)
   * 给出一个正整数 n 找到若干个完全平方数(1, 4, 9, 16。。。) 使得他们的和等于 n
   * 返回组成和的完全平方数最少的个数
   * 如 25 = 9 + 16 return 2
   */
  public static int numSquares(int n) {
    Queue<Integer> queue = new LinkedList<>();
    if (n == 1) return 1;
    if (n == 2) return 2;
    if (n == 3) return 3;
    int step = 0;
    queue.offer(n);
    int peek;
    int sqrt;
    int remain;
    while (!queue.isEmpty()) {
      step++;
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        peek = queue.poll();
        sqrt = (int)Math.sqrt(peek);
        remain = peek - sqrt * sqrt;
        // 判断该数是否是完全平方数
        if (remain == 0) return step;
        for (int j = sqrt; j > 0; j--) {
          queue.offer(peek - j * j);
        }
      }
    }
    return step;
  }

  /**
   * 动态规划思想解决最少完全平方数个数问题
   * dp(n) = min(dp(n-k)) + 1  [k 为任意数满足 0<k<=n]
   * 因为动态规划期间很多数的完全平方会重复用到
   * 可以用一个 int[] usedDp 记录其对应所所需要的最少完全平方数
   * 规定 dp[0] = 0
   * 其中可以看出临界值 dp[1] = 1, dp[2] = 2, dp[3] = 3
   *
   * 时间复杂度 O(n√n)
   * 空间复杂度 O(n)
   */
  public static int numSquaresDP(int n) {
    int[] dp = new int[n+1];
    dp[0] = 0;
    // 时间复杂度 O(n)
    for(int i = 1; i< n+1 ;i++) {
      // 最坏的情况 i = 1 + 1 + 1 + 1 ... 即由 i 个 1 组成
      dp[i] = i;
      // i = j * j + dp[i - j*j] dp[i - j*j] 代表这个数的最少完全平方数
      for (int j = 1; i - j * j >= 0; j ++) {
        // 这里的 for 循环时间复杂度 O(√i)
        dp[i] = Math.min(dp[i], dp[i - j*j] + 1);
      }
    }
    return dp[n];
  }

  /**
   * 贪心算法
   * 记录 1 -> 最接近 n 的完全平方数
   * 然后 count 从 1 不断增加，判断该数是否可以由 count 个完全平方数累加
   * 之所以叫做贪心算法，就是先贪婪的认为该数就是完全平方数 count = 1
   * 之后退一步认为该数是 2 个完全平方数去验证
   * 。。。。
   */
  public static int numSquaresGreedy(int n) {
    Set<Integer> squareNums = new HashSet<>();
    for (int i = 1; ; i++) {
      if (i * i > n) break;
      squareNums.add(i * i);
    }

    int count = 1;
    for (; count <= n; count++) {
      if (canDividedBy(n, count, squareNums)) return count;
    }
    return count;
  }

  /**
   * 判断一个数是否能被 n 个 完全平方数 divided
   *
   */
  private static boolean canDividedBy(int n, int count, Set<Integer> squareNums) {
    if (count == 1) return squareNums.contains(n);

    for (Integer num : squareNums) {
      if (canDividedBy(n - num, count - 1, squareNums)) return true;
    }

    return false;
  }

  /**
   * 判断一个字符串是否是有效括号
   * 字符串只包括 {} () [] 等6个字符
   */
  public static boolean isBracketValid(String s) {
    if (s == null || s.length() == 0) return true;
    if (s.length() % 2 != 0) return false;
// '('，')'，'{'，'}'，'['，']'
    char[] arr = s.toCharArray();
    LinkedList<Character> stack = new LinkedList<>();
    for(char c : arr) {
      switch(c) {
        case '(':
        case '{':
        case '[':
          stack.push(c);
          break;
        case ')':
          if (stack.isEmpty() || stack.pop() != '(') return false;
          break;
        case '}':
          if (stack.isEmpty() || stack.pop() != '{') return false;
          break;
        case ']':
          if (stack.isEmpty() || stack.pop() != '[') return false;
          break;
        default:
          return false;
      }
    }
    return stack.isEmpty();
  }


  /**
   * 下一个更大元素I
   * 使用单调栈
   *
   * 倒序方式去使用排序
   *
   * 变题，如果数组是可以循环的，比如最后一个元素，虽然都是-1，但是如果数据是循环的，则它开头可以寻找前面的元素
   * [1,2,1] -> [2, -1, 2]
   * 解法：将数组 copy 一份，再延长即可, 或者做 % 处理，就是比较绕
   * @param arr
   * @return
   */
  public static int[] nextGreaterInteger(int[] arr) {
    int[] ret = new int[arr.length];
    // 存放最小栈d
    LinkedList<Integer> stack = new LinkedList<>();
    for (int i = arr.length - 1; i >= 0; i--) {

      while (!stack.isEmpty() && arr[i] >= stack.peek()) {
       stack.pop();
      }
      ret[i] = stack.isEmpty() ? -1 : stack.peek();
      // 这个时候stack peek 一定是比当前数更大的 所以把当前数压栈
      stack.push(arr[i]);
    }
    return ret;
  }

  /**
   * 请根据每日 气温 列表，重新生成一个列表。对应位置的输出为：要想观测到更高的气温，至少需要等待的天数。如果气温在这之后都不会升高，请在该位置用 0 来代替。
   *
   * 例如，给定一个列表 temperatures = [73, 74, 75, 71, 69, 72, 76, 73]，你的输出应该是 [1, 1, 4, 2, 1, 1, 0, 0]。
   */
  public static int[] dailyTemperatures(int[] arr) {
    int[] ret = new int[arr.length];

    class innerObj {
      int value;
      int index;

      public innerObj(int value, int index) {
        this.value = value;
        this.index = index;
      }
    }

    // 存放最小栈
    LinkedList<innerObj> stack = new LinkedList<>();

    for (int i = arr.length -1; i>=0;i--) {
      while (!stack.isEmpty() && arr[i] >= stack.peek().value) {
        stack.pop();
      }
      ret[i] = stack.isEmpty() ? 0 : stack.peek().index - i;
      stack.push(new innerObj(arr[i], i));
    }
    return ret;
  }

  /**
   * 给你一个整数数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
   *
   * 返回滑动窗口中的最大值。
   *
   * 来源：力扣（LeetCode）
   * 链接：https://leetcode-cn.com/problems/sliding-window-maximum
   * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
   *
   * 输入：nums = [1,3,-1,-3,5,3,6,7], k = 3
   * 输出：[3,3,5,5,6,7]
   *
   * 解法：类似于单调栈，单调队列中的元素都是递增的或者递减的，
   *
   *
   * @param nums
   * @param k
   * @return
   */
  public static int[] maxSlidingWindow(int[] nums, int k) {
    if (k == 1) return nums;
    if (k <= 0) return null;

    // 使用一个双端队列
    Deque<Integer> deque = new LinkedList<>();
    // 存储结果的结果
    int[] result = new int[nums.length - k + 1];
    int index = 0;
    // 从头到尾遍历
    for (int i = 0; i < nums.length; i++) {
      // 先将前 k - 1 个元素入队列，入队列的时候，需要清空队列头小于该元素的所有元素，这样队列就是一个递减队列
      if (i <= k - 2) {
        while (!deque.isEmpty() && deque.getLast() < nums[i]) {
          deque.removeLast();
        }
        deque.addLast(nums[i]);
      } else {
        // 同理，入队列的时候，也要删除队头小于该元素的所有元素
        while (!deque.isEmpty() && deque.getLast() < nums[i]) {
          deque.removeLast();
        }
        deque.addLast(nums[i]);
        // 队列头就是我们需要的值
        result[index++] = deque.getFirst();
        // 使用完毕之后，需要判断之前的元素是否就是刚移出窗口的元素，如果是，则也要移除队头元素
        if (!deque.isEmpty() &&  deque.getFirst() == nums[i - k + 1]) {
          deque.removeFirst();
        }
      }
    }
    return result;
  }


  /**
   * 大数相乘
   *
   * 考虑，两个数相乘，s1 第 i 位和 s2 第 j 位，二者的值会最终落地于结果的第 i + j 到 第 i + j + 1 位
   *
   * 那么，设置
   * @param s1 大数1
   * @param s2 大数2
   * @return
   */
  public String multiply(String s1, String s2) {
    if ("0".equals(s1) || "0".equals(s2)) return "0";

    // 计算末尾 0 的数量
    int c1 = 0;
    int c2 = 0;
    int idx = s1.length() - 1;
    while (s1.charAt(idx) == 48) {
      c1++;
      s1 = s1.substring(0, idx--);
    }
    idx = s2.length() - 1;
    while (s2.charAt(idx) == 48) {
      c2++;
      s2 = s2.substring(0, idx--);
    }
    if (c1 + c2 != 0) {
      StringBuilder sb = new StringBuilder();
      for (int i= 0; i < c1 + c2; i++) {
        sb.append("0");
      }
      return multiply(s1, s2) + sb.toString();
    }



    List<Integer> arr1 = new ArrayList<>();
    List<Integer> arr2 = new ArrayList<>();
    for (int i = 0; i < s1.length(); i++) {
      arr1.add(s1.charAt(i) - 48);
    }

    for (int i = 0; i < s2.length(); i++) {
      arr2.add(s2.charAt(i) - 48);
    }

    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < s1.length() + s2.length();i++) {
      result.add(0);
    }

    int mul;
    int idx1;
    int idx2;
    // arr1 从右往左计算
    for (int i = s1.length() - 1; i >= 0; i--) {
      // arr2 从右往左计算
      for (int j = s2.length() - 1; j >= 0; j--) {
        // 获取数的相乘结果
        mul = arr1.get(i) * arr2.get(j);
        idx1 = i + j + 1;
        idx2 =  i + j;
        int add1 = mul % 10 + result.get(idx1);
        result.set(idx1, add1 % 10);
        int add2 = mul / 10 + result.get(idx2) + add1 / 10;
        result.set(idx2, add2 % 10);
        while (idx2 != 0) {
          add2 = add2 / 10 + result.get(idx2 - 1);
          if (add2 < 10) {
            result.set(idx2 - 1, add2);
            break;
          } else {
            result.set(idx2 - 1, add2 % 10);
            idx2--;
          }
        }
      }
    }
    int index = 0;
    while (result.get(index) == 0) {
      index++;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = index; i < result.size(); i++) {
      sb.append(result.get(i));
    }
    return sb.toString();
  }

  public static void main(String[] args) {

    Solution ins = new Solution();

    StringBuilder sb = new StringBuilder();

    // 最长回文字符串
    System.out.println("最长回文字符串" + longestPalindrome("abac"));

    // strStr KMP 算法 核心为 buildNext
    System.out.println(strStr("ACTGPACTGKACTGPACY", "ACTGPACY"));

    System.out.println("正整数数组中子数组和大于s, 且长度最短" + minSubArrayLen(3, new int[] {1, 1}));

    System.out.println("杨辉三角:" + generate(5));

    System.out.println("寻找旋转数组的最小值:" + findMin(new int[] {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3}));

    int[] removeDuplicateArr = new int[] {0, 0, 0, 1, 1, 2, 3, 3, 4, 6};
    int size = removeDuplicates(removeDuplicateArr);
    clear(sb);
    for (int i = 0; i < size; i++) {
      sb.append(removeDuplicateArr[i]);
    }
    System.out.println("原地删除排序数组中重复的元素: length=" + size + " arr: " + sb.toString());
    clear(sb);

    MyLinkedList<Integer> myLinkedList = constructLinkedList(Lists.newArrayList(1, 2, 3, 4, 5));
    myLinkedList.addAtHead(0);
    myLinkedList.addAtTail(6);
    myLinkedList.deleteAtIndex(1);
    System.out.println("创建一个链表:" + myLinkedList.toString());
    myLinkedList.reverse();
    System.out.println("翻转链表后:" + myLinkedList.toString());
    myLinkedList.oddEvenList();
    System.out.println("奇偶链表操作后:" + myLinkedList.toString());
    System.out.println("回文串:?" + myLinkedList.isPalindrome());

    System.out.println("119是否是快乐数: " + isHappy(2));
    System.out.println("两个字符串是否是同构字符串: ab 和 aa: " + isIsomorphic("ab", "aa"));
    System.out.println("两个字符串是否是同构字符串: ab 和 aa: " + isIsomorphic2("ab", "aa"));
    TreeNode<Integer> root = constructDuplicateSubTree();
    List<TreeNode<Integer>> duplicateSubtreeList = findDuplicateSubtrees(root);
    duplicateSubtreeList.forEach(ele -> sb.append(ele + " "));
    System.out.println("寻找二叉树的重复子树: 见代码" + sb.toString() );
    clear(sb);
    int[] fourSumA = new int[]{0};
    int[] fourSumB = new int[]{0};
    int[] fourSumC = new int[]{0};
    int[] fourSumD = new int[]{0};
    System.out.println("四数相加II:" + fourSumCount(fourSumA, fourSumB, fourSumC, fourSumD));
    List<Integer> topKList = arrToList(topKFrequent(new int[]{5,2,5,3,5,3,1,1,3}, 2));
    topKList.forEach(ele -> sb.append(ele + " "));
    System.out.println("前 k 个高频词(优先级队列 PriorityQueue):" + sb.toString());
    clear(sb);

    char[][] numIslands = new char[][]{{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}};
    System.out.println("BFS 岛屿数量:" + numOfIslands(numIslands));
    System.out.println("打开转盘锁:" + openLock(new String[]{"0201", "0101", "0102", "1212", "2002"}, "0202"));

    System.out.println("完全平方数(BFS):" + numSquares(13));
    System.out.println("完全平方数(DP):" + numSquaresDP(115345));
    System.out.println("完全平方数(GREEDY)" + numSquaresGreedy(115345));
    System.out.println("[({[]})[{()}]] 是否是有效的括号:" + isBracketValid("[({[]})[{()}]]"));

    System.out.println("单调栈");
    System.out.println("下一个更大的数(1, 4, 5, 3, 2, 6, 2, 7)" + concat(nextGreaterInteger(new int[] {1, 4, 5, 3, 2, 6, 2, 7})));

    System.out.println("等待下一个更高温度需要等待的天数，0表示没有：" + concat(dailyTemperatures(new int[] {1, 4, 5, 3, 2, 6, 2, 7})));

    CommonUtils.print("单调队列");
    System.out.println("滑动窗口最大值" + concat(maxSlidingWindow(new int[]{3,1,2,0,5}, 3)));

    System.out.println("大数相乘 999 * 999 = " + ins.multiply("999", "999"));
  }

  private static String concat(int[] arr) {
    StringBuilder sb = new StringBuilder();
    for (int a : arr) {
      sb.append(a+ " ");
    }
    return sb.toString();
  }


}

