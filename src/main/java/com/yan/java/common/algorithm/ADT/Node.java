package com.yan.java.common.algorithm.ADT;

import lombok.Data;

/**
 * 树的节点
 */


@Data
public class Node<T> {

    /**
     * left 和 right 适用于树
     */
    public Node<T> left;

    public Node<T> right;

    // 存储的数据
    public T data;

    /**
     * prev 和 next 适用于链表元素
     */
    public Node<T> prev;

    public Node<T> next;


}
