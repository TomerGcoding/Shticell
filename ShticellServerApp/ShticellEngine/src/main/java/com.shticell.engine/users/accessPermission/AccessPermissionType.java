package com.shticell.engine.users.accessPermission;

import java.io.Serializable;

public enum AccessPermissionType implements Serializable {
    NONE {
        @Override
        public String toString() {
            return "None";
        }
    },
    READER {
        @Override
        public String toString() {
            return "Reader";
        }
    },
    WRITER {
        @Override
        public String toString() {
            return "Writer";
        }
    },
    OWNER {
        @Override
        public String toString() {
            return "Owner";
        }
    };
@Override
    public String toString() {
        return "";
    }
}
