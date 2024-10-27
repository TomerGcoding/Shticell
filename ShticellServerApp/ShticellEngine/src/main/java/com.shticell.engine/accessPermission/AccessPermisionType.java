package com.shticell.engine.accessPermission;

public enum AccessPermisionType {
    NONE {
        @Override
        public String getPermissionName() {
            return "NONE";
        }
    },
    READER {
        @Override
        public String getPermissionName() {
            return "READ";
        }
    },
    WRITER {
        @Override
        public String getPermissionName() {
            return "WRITE";
        }
    },
    OWNER {
        @Override
        public String getPermissionName() {
            return "OWNER";
        }
    };
    abstract public String getPermissionName();

}
