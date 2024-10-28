package com.shticell.engine.users.accessPermission;

public enum AccessPermissionStatus {
    PENDING {
        @Override
        public String toString() {
            return "PENDING";
        }
    },
    APPROVED {
        @Override
        public String toString() {
            return "APPROVED";
        }
    },
    REJECTED {
        @Override
        public String toString() {
            return "REJECTED";
        }
    };
}
