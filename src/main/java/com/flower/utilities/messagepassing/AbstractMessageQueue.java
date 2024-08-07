package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractMessageQueue<M> {
    protected final ConcurrentLinkedQueue<M> innerQueue;
    protected final AtomicReference<LinkedListenerNode> queueListeners;

    static class LinkedListenerNode {
        private final SettableFuture<Void> future;
        @Nullable private final LinkedListenerNode next;

        LinkedListenerNode(SettableFuture<Void> future, @Nullable LinkedListenerNode next) {
            this.future = future;
            this.next = next;
        }
    }

    public AbstractMessageQueue() {
        this.innerQueue = new ConcurrentLinkedQueue<>();
        queueListeners = new AtomicReference<>(null);
    }

    public int size() {
        return innerQueue.size();
    }

    public boolean isEmpty() {
        return innerQueue.isEmpty();
    }

    /**
     * Set a listener to be notified when new messages are added
     */
    public ListenableFuture<Void> getMessageListener() {
        SettableFuture<Void> notificationFuture = SettableFuture.create();
        while (true) {
            LinkedListenerNode oldList = queueListeners.get();
            LinkedListenerNode newList = new LinkedListenerNode(notificationFuture, oldList);
            if (queueListeners.compareAndSet(oldList, newList)) {
                break;
            }
        }
        // To mitigate race conditions, check if we can notify listeners immediately
        notifyMessageListeners(false);
        return notificationFuture;
    }

    /**
     * Wake up all waiting consumers without sending a message
     */
    public void notifyMessageListeners() {
        this.notifyMessageListeners(true);
    }

    /**
     * Wake up all waiting consumers without sending a message, optionally checking if the queue is not empty
     * @param force False - won't notify if the queue is empty; True - will force notification even if there are no more messages in the queue
     */
    public void notifyMessageListeners(boolean force) {
        while (true) {
            if (!force && innerQueue.isEmpty()) {
                return;
            }
            LinkedListenerNode oldList = queueListeners.get();
            if (oldList == null) {
                return;
            }
            if (queueListeners.compareAndSet(oldList, null)) {
                LinkedListenerNode cursor = oldList;
                while (cursor != null) {
                    cursor.future.set(null);
                    cursor = cursor.next;
                }
                return;
            }
        }
    }

    // =================================================

    protected void innerAdd(M message) {
        innerQueue.add(message);
        notifyMessageListeners(false);
    }

    protected void innerAddAll(Collection<? extends M> messages) {
        innerQueue.addAll(messages);
        notifyMessageListeners(false);
    }

    @Nullable
    protected M innerPoll() {
        return innerQueue.poll();
    }

    @Nullable
    protected M innerPeek() {
        return innerQueue.peek();
    }
}
