package com.saisaiwa.tspi.nas.common.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @date: 2024/03/24 12:33
 * @author: saisiawa
 **/
public class FileLockUtil {

    private static final Map<Long, ReentrantLock> REENTRANT_LOCK_MAP = new ConcurrentHashMap<>();


    public static void addLock(Long bucketsId) {
        if (!REENTRANT_LOCK_MAP.containsKey(bucketsId)) {
            REENTRANT_LOCK_MAP.put(bucketsId, new ReentrantLock());
        }
    }

    public static ReentrantLock getLock(Long bucketsId) {
        addLock(bucketsId);
        return REENTRANT_LOCK_MAP.get(bucketsId);
    }

    public static void removeLock(Long bucketsId) {
        if (REENTRANT_LOCK_MAP.containsKey(bucketsId)) {
            ReentrantLock lock = REENTRANT_LOCK_MAP.get(bucketsId);
            try {
                if (lock.tryLock(30, TimeUnit.SECONDS)) {
                    REENTRANT_LOCK_MAP.remove(bucketsId);
                    lock.unlock();
                } else {
                    if (lock.isLocked()) {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
