package com.game.battlesimulator.datastructure;

import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularQueueTest {

    private Combatant combatant(String name) {
        return new Enemy("Enemy-" + name, name, 10, 2);
    }

    @Test
    void enqueueKeepsFifoOrder() {
        CircularQueue q = new CircularQueue();
        Combatant a = combatant("A");
        Combatant b = combatant("B");
        Combatant c = combatant("C");

        q.enqueue(a);
        q.enqueue(b);
        q.enqueue(c);

        assertEquals(3, q.getSize());
        assertSame(a, q.dequeue());
        assertSame(b, q.dequeue());
        assertSame(c, q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    void dequeueEmptyReturnsNull() {
        assertNull(new CircularQueue().dequeue());
    }

    @Test
    void isEmptyTransitions() {
        CircularQueue q = new CircularQueue();
        assertTrue(q.isEmpty());
        q.enqueue(combatant("A"));
        assertFalse(q.isEmpty());
        q.dequeue();
        assertTrue(q.isEmpty());
    }

    @Test
    void rotateTurnNoOpOnSingleElement() {
        CircularQueue q = new CircularQueue();
        Combatant only = combatant("only");
        q.enqueue(only);

        q.rotateTurn();

        assertEquals(1, q.getSize());
        assertSame(only, q.getCombatantOnIndex(0));
    }

    @Test
    void rotateTurnMovesHeadToTail() {
        CircularQueue q = new CircularQueue();
        Combatant a = combatant("A");
        Combatant b = combatant("B");
        q.enqueue(a);
        q.enqueue(b);

        q.rotateTurn();

        assertSame(b, q.getCombatantOnIndex(0));
        assertSame(a, q.getCombatantOnIndex(1));
    }

    @Test
    void removeFirstMiddleLast() {
        Combatant a = combatant("A");
        Combatant b = combatant("B");
        Combatant c = combatant("C");

        CircularQueue q = new CircularQueue();
        q.enqueue(a);
        q.enqueue(b);
        q.enqueue(c);
        assertTrue(q.remove(a));
        assertEquals(2, q.getSize());
        assertSame(b, q.getCombatantOnIndex(0));
        assertSame(c, q.getCombatantOnIndex(1));

        q = new CircularQueue();
        q.enqueue(a);
        q.enqueue(b);
        q.enqueue(c);
        assertTrue(q.remove(b));
        assertEquals(2, q.getSize());
        assertSame(a, q.getCombatantOnIndex(0));
        assertSame(c, q.getCombatantOnIndex(1));

        q = new CircularQueue();
        q.enqueue(a);
        q.enqueue(b);
        q.enqueue(c);
        assertTrue(q.remove(c));
        assertEquals(2, q.getSize());
        assertSame(a, q.getCombatantOnIndex(0));
        assertSame(b, q.getCombatantOnIndex(1));
    }

    @Test
    void removeLastElementEmptiesQueue() {
        CircularQueue q = new CircularQueue();
        Combatant only = combatant("only");
        q.enqueue(only);

        assertTrue(q.remove(only));
        assertTrue(q.isEmpty());
        assertNull(q.getCombatantOnIndex(0));
    }

    @Test
    void removeAbsentReturnsFalse() {
        CircularQueue q = new CircularQueue();
        q.enqueue(combatant("A"));

        assertFalse(q.remove(combatant("X")));
        assertEquals(1, q.getSize());
    }

    @Test
    void clearEmptiesQueue() {
        CircularQueue q = new CircularQueue();
        q.enqueue(combatant("A"));
        q.enqueue(combatant("B"));

        q.clear();

        assertTrue(q.isEmpty());
        assertNull(q.getCombatantOnIndex(0));
    }

    @Test
    void getCombatantOnIndexBounds() {
        CircularQueue q = new CircularQueue();
        Combatant a = combatant("A");
        q.enqueue(a);

        assertNull(q.getCombatantOnIndex(-1));
        assertNull(q.getCombatantOnIndex(1));
        assertSame(a, q.getCombatantOnIndex(0));
    }
}
