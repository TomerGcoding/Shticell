package com.shticell.engine.users.accessPermission;

public enum AccessPermisionType {
    NONE {
        @Override
        public String toString() {
            return "NONE";
        }
    },
    READER {
        @Override
        public String toString() {
            return "READ";
        }
    },
    WRITER {
        @Override
        public String toString() {
            return "WRITE";
        }
    },
    OWNER {
        @Override
        public String toString() {
            return "OWNER";
        }
    };

}
