package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class AbstractMessageQueue<M> {
    protected final ConcurrentLinkedQueue<M> innerQueue;
    protected final AtomicReference<List<SettableFuture<Void>>> queueListeners;

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
            List<SettableFuture<Void>> oldList = queueListeners.get();
            List<SettableFuture<Void>> newList = new ArrayList<>();
            if (oldList != null && !oldList.isEmpty()) {
                newList.addAll(oldList);
            }
            newList.add(notificationFuture);
            if (queueListeners.compareAndSet(oldList, newList)) {
                break;
            }
        }
        // To mitigate race conditions, check if we can notify listeners immediately
        notifyMessageListeners(false);
        return notificationFuture;
    }

    /**
     * Check if the queue is not empty and notify all listeners
     * @param force False - won't notify if the queue is empty; True - will force notification even if there are no more messages in the queue
     */
    public void notifyMessageListeners(boolean force) {
        while (true) {
            if (!force && innerQueue.isEmpty()) {
                return;
            }
            List<SettableFuture<Void>> oldList = queueListeners.get();
            if (oldList == null) {
                return;
            }
            if (queueListeners.compareAndSet(oldList, null)) {
                for (SettableFuture<Void> listener : oldList) {
                    listener.set(null);
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
