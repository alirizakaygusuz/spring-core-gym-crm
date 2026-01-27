package com.alirizakaygusuz.gymcrm.dao.util;

import java.util.Map;

/**
 * Utility class responsible for generating sequential identifier values.
 *
 * <p>This class is used to generate unique IDs for in-memory persistence
 * and can synchronize its state based on existing storage content.</p>
 */
public final class IdSequence {
    private long nextId = 1L;

    public long next() {
        return nextId++;
    }

    public void syncFrom(Map<Long, ?> storage) {
        long maxId = storage.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        nextId = maxId + 1;
    }


}
