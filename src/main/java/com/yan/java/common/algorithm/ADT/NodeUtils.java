package com.yan.java.common.algorithm.ADT;

import java.util.*;

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

    /**
     * 通过中序和前序遍历构建树
     * @param pre
     * @param in
     * @return
     */
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        return construct(pre, 0, pre.length - 1, in, 0, in.length - 1);
    }

    public TreeNode construct(int [] pre, int f1, int t1, int [] in, int f2, int t2) {
        if (t1 < f1 || t2 < f2) {
            return null;
        }

        if (t1 == f1 || t2 == f2) {
            return new TreeNode(pre[f1]);
        }
        // 根节点是 pre[f1]
        int f2_f = f2;
        Set<Integer> left = new HashSet<>();
        while (f2_f < pre.length && in[f2_f] != pre[f1]) {
            left.add(in[f2_f]);
            f2_f++;
        }

        int f1_f = f1 + 1;
        while (f1_f < pre.length && left.contains(pre[f1_f])) {
            f1_f++;
        }

        // 这时候 pre[f1] == in[f2_f]
        // 根节点就是 pre[f1]
        // 左子树为 construct(pre, f1, f1_f - 1, in, f2, f2_f - 1 )
        // 右子树为 construct(pre, f1_f, t1, in, f2_f, t2)

        TreeNode root = new TreeNode(pre[f1]);
        root.left = construct(pre, f1 + 1, f1_f - 1, in, f2, f2_f - 1 );
        root.right = construct(pre, f1_f, t1, in, f2_f, t2);
        return root;
    }

    public static void main(String[] args) {
        NodeUtils ins = new NodeUtils();
        ins.reConstructBinaryTree(new int[]{1,2,3,4,5,6,7}, new int[]{3,2,4,1,6,5,7});
    }


}
