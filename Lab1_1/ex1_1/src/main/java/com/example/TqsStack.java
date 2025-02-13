package com.example;

import java.util.NoSuchElementException;
import java.util.Stack;

public class TqsStack<T> {
    private Stack<T> stack;
    private int capacity;

    public TqsStack() {
        this.stack = new Stack<>();
        this.capacity = -1;
    }

    public TqsStack(int capacity) {
        this.stack = new Stack<>();
        this.capacity = capacity;
    }

    public void push(T x) {
        if (capacity > 0 && stack.size() >= capacity) {
            throw new IllegalStateException("Stack is full");
        }
        stack.push(x);
    }

    public T pop() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return stack.pop();
    }

    public T peek() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return stack.peek();
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public T popTopN(int n) {
        T top = null;
        for (int i = 0; i < n; i++) {
            top = stack.removeFirst();
        }
        return top;
    }
}
