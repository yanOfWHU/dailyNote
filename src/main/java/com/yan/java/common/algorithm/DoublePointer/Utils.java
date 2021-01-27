package com.yan.java.common.algorithm.DoublePointer;


import com.yan.java.common.algorithm.ADT.Node;

/**
 * 双指针：
 *
 * 1、快慢指针
 * 常见问题：链表环、链表环大小、链表环起始位置、链表中点、链表倒数第 k 个节点
 * 2、左右指针
 * 常见问题：二分查找、排序列表的两数之和、反转数组、滑动窗口
 */
public class Utils {

    /**
     * 已知链表有环，求链表的环的起始位置
     * 快慢指针：
     * 1、快慢指针同时指向头节点，快指针一次走两步，第一次相遇时，快指针比慢指针多走一圈。
     *      即 m + y + cl = 2 (m + y) 即 m + y = cl 所以慢指针继续走 m 步就可以到达环起始位置
     *      其中 m 是表头距离环起始位置的距离，y 是慢指针距离环起始位置的就，cl 是环的长度
     * 2、慢指针指向表头，快指针继续走(不过是以正常的一倍速度行驶)，这样 快慢指针都走 m 步，就可以再次相遇，同时，也就是环起始位置
     * @param head 链表头
     * @return 环的起始位置
     */
    public static Node<Integer> detectCycleOrigin(Node<Integer> head) {
        if (head == null || head.next == null) return null;
        Node<Integer> fast = head;
        Node<Integer> slow = head;

        while (fast!= null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) break;
        }

        if (fast == null || fast.next == null) {
            // 说明没有环
            return null;
        }

        slow = head;

        while (fast != slow) {
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }

}
