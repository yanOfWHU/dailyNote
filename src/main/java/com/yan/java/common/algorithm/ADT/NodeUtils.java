package com.yan.java.common.algorithm.ADT;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class NodeUtils {

    /**
     * 先序遍历
     */
    public <T> List<T> preOrder(Node<T> head) {
        List<T> result = new ArrayList<>();
        preOrder(head, result);
        return result;
    }

    private <T> void preOrder(Node<T> head, List<T> path) {
        if (head == null) return;
        path.add(head.data);
        preOrder(head.left, path);
        preOrder(head.right, path);
    }

    /**
     * 前序遍历 非递归 简单的借助队列即可
     */
    public <T> List<T> preOrderNonRecursive(Node<T> head) {
        if (head == null) return Collections.emptyList();

        List<T> result = new ArrayList<>();

        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(head);
        while (!queue.isEmpty()) {
            Node<T> peek = queue.poll();
            result.add(peek.data);
            if (peek.left != null) queue.add(peek.left);
            if (peek.right != null) queue.add(peek.right);
        }
        return result;
    }

    /**
     * 中序遍历
     */
    public <T> List<T> inOrder(Node<T> head) {
        List<T> result = new ArrayList<>();
        inOrder(head, result);
        return result;
    }

    private <T> void inOrder(Node<T> head, List<T> path) {
        if (head == null) return;
        inOrder(head.left, path);
        path.add(head.data);
        inOrder(head.right, path);
    }

    public <T> List<T> inOrderNonRecursive(Node<T> head) {
        if (head == null) return Collections.emptyList();

        List<T> result = new ArrayList<>();
        Deque<Node<T>> stack = new LinkedList<>();
        // 从根节点开始，将左节点压栈
        while(head != null) {
            stack.push(head);
            head = head.left;
        }
        while (!stack.isEmpty()) {
            // 弹出并获取栈顶
            Node<T> peek = stack.pop();
            result.add(peek.data);
            Node<T> rightTree = peek.right;
            // 如果右节点不为空，则视为右节点为一个树，同样对右节点树的所有子节点进行压栈
            while (rightTree != null) {
                stack.push(rightTree);
                rightTree = rightTree.left;
            }
        }
        return result;
    }

    /**
     * 后序遍历
     */
    public <T> List<T> postOrder(Node<T> head) {
        List<T> result = new ArrayList<>();
        postOrder(head, result);
        return result;
    }

    private <T> void postOrder(Node<T> head, List<T> path) {
        if (head == null) return;
        postOrder(head.left, path);
        postOrder(head.right, path);
        path.add(head.data);
    }

    public <T> List<T> postOrderNonRecursive(Node<T> head) {
        List<T> result = new ArrayList<>();
        Deque<Node<T>> stack = new LinkedList<>();

        // 需要记录上一个访问节点
        Node<T> prevVisit = null;

        while (head != null || !stack.isEmpty()) {
            // 同样从根节点开始将所有的 left 节点压栈
            while (head != null) {
                stack.push(head);
            }

            // 获取但不弹出栈顶节点
            head = stack.peek();

            // 如果栈顶节点没有右节点，或者刚被访问过，则直接弹出记录
            if (head.right == null || head.right == prevVisit) {
                result.add(stack.pop().data);
                prevVisit = head;
                head = null;
            } else {
                // 如果栈顶节点有右节点, 将右节点视为一棵树，然后压栈
                head = head.right;
            }

        }

        return result;
    }


}
