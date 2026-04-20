package com.openmanagement.common.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

public final class DataPermissionContext {
    private DataPermissionContext() {
    }

    private static final ThreadLocal<DataScope> HOLDER = new ThreadLocal<>();

    public static void set(DataScope dataScope) {
        HOLDER.set(dataScope);
    }

    public static DataScope get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataScope {
        private boolean allData;
        private Set<Long> deptIds;
        private Long userId;

        public Set<Long> safeDeptIds() {
            return deptIds == null ? Collections.emptySet() : deptIds;
        }
    }
}
