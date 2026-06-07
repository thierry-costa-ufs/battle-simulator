package com.game.battlesimulator.datastructure;

import com.game.battlesimulator.model.domain.Combatant;

class Node{
    private Combatant info;
    private Node proximo;

    public Node(Combatant info) {
        this.info = info;
        this.proximo = null;
    }

    public Combatant getInfo() { return info; }
    public void setInfo(Combatant info) { this.info = info; }
    public Node getProximo() { return proximo; }
    public void setProximo(Node proximo) { this.proximo = proximo; }
}

public class CircularQueue {
    private final Node cabeca; // Nó sentinela (Fila Encabeçada)
    private Node fim;          // Ponteiro para o último elemento
    private int size;

    public CircularQueue() {
        this.cabeca = new Node(null); // Cabeça vazia
        this.fim = cabeca;
        this.fim.setProximo(cabeca); // Aponta para si mesma (circularidade)
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
            cabeca.setProximo(novo);
            fim=novo;
            fim.setProximo(cabeca.getProximo());
        }
        else {
            novo.setProximo(cabeca.getProximo());
            fim.setProximo(novo);
            fim=novo;
        }
        size++;
    }

    public Combatant dequeue() {
        if (isEmpty()) return null;

        Node primeiro = cabeca.getProximo();
        Combatant info = primeiro.getInfo();

        if (size == 1) {
            cabeca.setProximo(cabeca);
            fim = cabeca;
        }
        else {
            cabeca.setProximo(primeiro.getProximo());
            fim.setProximo(cabeca.getProximo());
        }
        size--;
        return info;
    }

    public void rotateTurn() {
        if (size > 1) {
            Combatant currentAttacker = dequeue();
            enqueue(currentAttacker);
        }
    }

    public Combatant getCombatantOnIndex(int index) {
        if (index < 0 || index >= size) return null;

        Node atual = cabeca.getProximo();
        for (int i=0; i<index; i++) {
            atual = atual.getProximo();
        }

        return atual.getInfo();
    }

    public void clear(){
        fim.setProximo(cabeca);
        cabeca.setProximo(cabeca);
        size = 0;
    }

}
