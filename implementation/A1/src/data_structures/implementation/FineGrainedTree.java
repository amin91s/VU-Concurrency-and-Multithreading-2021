package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {


    private class Node{
        T item;
        Node left, right;
        Lock lock;

        public Node(T t){
            this.item = t;
            this.left = this.right = null;
            this.lock = new ReentrantLock();
        }
        public void lock(){
            this.lock.lock();
        }
        public void unlock(){
            this.lock.unlock();
        }

    }
   /*headNode's right child points to the root of the tree. it'd be NULL if tree is empty
    the left child is always NULL*/
   private final Node headNode = new Node(null);

    public void add(T t) {
        Node node = new Node(t);
        Node pred = headNode;
        Node curr ;
        pred.lock();
        try{
           if(pred.right == null){
               pred.right = node;
           } else {
               curr = pred.right;
               curr.lock();
               try{
                   while(true){
                       if (curr.item.compareTo(node.item) > 0){ /* newNode is smaller than the current node*/
                           if (curr.left == null){
                               curr.left = node; /* left child node is null, add it there */
                               return;
                           } else {
                               pred.unlock();
                               pred = curr;
                               curr = curr.left;
                               curr.lock();
                           }
                       } else { /* newNode is equal or greater than the current node */
                           if (curr.right == null){
                               curr.right = node; /* right child node is null, add it there */
                               return;
                           } else {
                               pred.unlock();
                               pred = curr;
                               curr = curr.right;
                               curr.lock();
                           }
                       }
                   }
               } finally {
                   curr.unlock();
               }
           }
        } finally {
            pred.unlock();
        }
    }

    public void remove(T t) {
        Node parent = headNode;
        Node curr ;
        parent.lock();
        try {
            if (parent.right != null){  /* tree is not empty */
                curr = parent.right;
                curr.lock();
                try{
                    while (curr != null){
                        if (curr.item.compareTo(t) == 0) break;
                        else{
                            parent.unlock();
                            parent = curr;
                            curr = curr.item.compareTo(t) > 0 ? curr.left : curr.right;
                            curr.lock();
                        }
                    }
                    if (curr != null) {    /* node is in the tree */
                        if(isLeaf(curr))   /* node is a leaf */
                            removeLeaf(curr,parent);
                        else if (has2Child(curr)) /* node with 2 children */
                            remove2Child(curr);
                        else
                            remove1Child(curr,parent); /* node with 1 child */
                    }
                } finally {
                    curr.unlock();
                }

            }
        } finally {
            parent.unlock();
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList<>();
        //inOrderAddToList(headNode.right,list);
        inOrderAddMorris(list);
        return list;
    }

    /**
     * This method removes a leaf node from the tree.
     * @param curr the node to be removed
     * @param parent parent node, pointing to the node to be deleted
     */
    private void removeLeaf(Node curr, Node parent){
        if (parent == headNode) /* curr is the only node in the tree */
            parent.right = null;
        else if (parent.left == curr)
            parent.left = null;
        else
            parent.right = null;

    }

    /**
     * This method removes a node which has 1 child.
     * @param curr the node to be removed
     * @param parent parent node, pointing to curr
     */
    private void remove1Child(Node curr, Node parent){
        if (curr == headNode.right)
            headNode.right = getChild(headNode.right);
        else if(parent.left == curr)
            parent.left = getChild(curr);
        else
            parent.right = getChild(curr);
    }

    /**
     * This method is used to remove a node that has 2 child nodes,
     * by copying its successor's value, and removing that node.
     * @param curr the node to be removed
     */
    private void remove2Child(Node curr){
        Node parent = curr;

        Node successor = curr.right;
        if ( successor.left == null ){ /* right child node is the successor */
            curr.item = successor.item;
            curr.right = successor.right; /* either a child node or null */
            return;
        }
        while ( successor.left != null ) /* right child node was not the successor. get the MIN of the subtree */
        {
            parent = successor;
            successor = successor.left;
        }
        curr.item = successor.item;
        parent.left = successor.right; /* parent node points to successor's right child */

    }




    /**
     * this method gets the child node of the current
     * node. Only used when current node has exactly one child node.
     * @param node node with one child node
     * @return child node of the current node
     */
    private Node getChild(Node node){
        return node.left != null ? node.left : node.right;
    }
    private boolean isLeaf(Node node){
        return (node.left == null && node.right == null);
    }
    private boolean has2Child(Node node){
        return (node.left != null && node.right != null);
    }

    /**
     * This method adds all of the elements in the tree to the arrayList,
     * using Morris in-order traversal algorithm, which is iterative, and does not
     * use stacks.
     * source: en.wikipedia.org/wiki/Threaded_binary_tree
     *
     * @param list An ArrayList where tree elements are added to
     */
    private void inOrderAddMorris(ArrayList<T> list){
        if (headNode.right == null)
            return;
        Node curr, pred;
        curr = headNode.right;
        while (curr != null){
            if (curr.left == null){
                list.add(curr.item);
                curr = curr.right;
            }
            else {
                pred = curr.left;
                while (pred.right != null && pred.right != curr)
                    pred = pred.right;
                if (pred.right == null){
                    pred.right = curr;
                    curr = curr.left;
                }
                else {
                    pred.right = null;
                    list.add(curr.item);
                    curr = curr.right;
                }
            }
        }
    }


//    /**
//     * This method adds all of the elements in the tree to the arrayList, using in-order
//     * recursive tree traversal.
//     * @param root  Root of the tree
//     * @param list  An ArrayList where tree elements are added to
//     */
//    private void inOrderAddToList(Node root, ArrayList<T> list){
//        if (root != null){
//            inOrderAddToList(root.left, list);
//            list.add(root.item);
//            inOrderAddToList(root.right,list);
//        }
//    }


}
