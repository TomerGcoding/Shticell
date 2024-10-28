package com.shticell.engine.users.accessPermission;

import java.io.Serializable;

public enum AccessPermissionStatus implements Serializable {
    PENDING {
        @Override
        public String toString() {
            return "Pending";
        }
    },
    APPROVED {
        @Override
        public String toString() {
            return "Approved";
        }
    },
    REJECTED {
        @Override
        public String toString() {
            return "Rejected";
        }
    };
}
