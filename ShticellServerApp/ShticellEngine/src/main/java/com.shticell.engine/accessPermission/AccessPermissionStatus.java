package com.shticell.engine.accessPermission;

public enum AccessPermissionStatus {
    PENDING {
        @Override
        public String getPermissionStatus() {
            return "PENDING";
        }
    },
    APPROVED {
        @Override
        public String getPermissionStatus() {
            return "APPROVED";
        }
    },
    REJECTED {
        @Override
        public String getPermissionStatus() {
            return "REJECTED";
        }
    };
    abstract public String getPermissionStatus();
}
