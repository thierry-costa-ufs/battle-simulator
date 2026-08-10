package com.game.battlesimulator.datastructure;

import com.game.battlesimulator.model.domain.Combatant;

class Node{
    private Combatant info;
    private Node next;

    public Node(Combatant info) {
        this.info = info;
        this.next = null;
    }

    public Combatant getInfo() { return info; }
    public void setInfo(Combatant info) { this.info = info; }
    public Node getNext() { return next; }
    public void setNext(Node next) { this.next = next; }
}

public class CircularQueue {
    private Node head;
    private Node tail;
    private int size;

    public CircularQueue() {
        this.head = new Node(null);
        this.tail = head;
        this.tail.setNext(head);
        this.size = 0;
    }

    public int getSize(){
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void enqueue(Combatant info) {
        Node novo = new Node(info);

        if (isEmpty()) {
            head.setNext(novo);
            tail =novo;
            tail.setNext(head.getNext());
        }
        else {
            novo.setNext(head.getNext());
            tail.setNext(novo);
            tail =novo;
        }
        size++;
    }

    public Combatant dequeue() {
        if (isEmpty()) return null;

        Node primeiro = head.getNext();
        Combatant info = primeiro.getInfo();

        if (size == 1) {
            head.setNext(head);
            tail = head;
        }
        else {
            head.setNext(primeiro.getNext());
            tail.setNext(head.getNext());
        }
        size--;
        return info;
    }

    public boolean remove(Combatant info) {
        if (isEmpty()) return false;

        Node anterior = head;
        Node atual = head.getNext();

        for (int i = 0; i < size; i++) {
            if (atual.getInfo().equals(info)) {
                anterior.setNext(atual.getNext());

                if (atual == tail) {
                    tail = (anterior == head) ? head : anterior;
                }

                size--;

                if (size == 0) {
                    head.setNext(head);
                    tail = head;
                } else {
                    tail.setNext(head.getNext());
                }
                return true;
            }
            anterior = atual;
            atual = atual.getNext();
        }
        return false;
    }

    public void rotateTurn() {
        if (size > 1) {
            Combatant currentAttacker = dequeue();
            enqueue(currentAttacker);
        }
    }

    public Combatant getCombatantOnIndex(int index) {
        if (index < 0 || index >= size) return null;

        Node atual = head.getNext();
        for (int i=0; i<index; i++) {
            atual = atual.getNext();
        }

        return atual.getInfo();
    }

    public void clear(){
        tail.setNext(head);
        head.setNext(head);
        size = 0;
    }

}
