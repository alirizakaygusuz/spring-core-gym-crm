package com.alirizakaygusuz.gymcrm.dao.util;

import java.util.Map;

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
