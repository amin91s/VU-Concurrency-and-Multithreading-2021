package data_structures.implementation;

import java.util.ArrayList;

import data_structures.Sorted;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private class Node{
        T item;
        Node next;
        Lock lock;

        public Node(T t){
            this.item = t;
            this.next = null;
            this.lock = new ReentrantLock();
        }
        public void lock(){
            this.lock.lock();
        }
        public void unlock(){
            this.lock.unlock();
        }

    }
    private final Node head = new Node(null);
    private final Node tail = new Node(null);
    public FineGrainedList(){
        head.next = tail;
    }

    public void add(T t) {
        Node newNode = new Node(t);
        head.lock();
        Node curr;
        Node pred = head;
        try{
            curr = pred.next;
            curr.lock();
            try {
                while (!curr.equals(tail) ){
                    if(curr.item.compareTo(t) > 0){
                        pred.next = newNode;
                        newNode.next = curr;
                        //break;
                        return;
                    }
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                newNode.next = curr;
                pred.next = newNode;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public void remove(T t) {
        Node pred = head, curr;
        pred.lock();
        try {
            curr = pred.next;
            curr.lock();
            try {
                while(!curr.equals(tail) && !curr.item.equals(t)){
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if(!curr.equals(tail)){
                    pred.next = curr.next;
                }
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList<>();
        Node temp = head.next;
        while (temp != tail){
            list.add(temp.item);
            temp = temp.next;

        }
        return list;
    }
}
