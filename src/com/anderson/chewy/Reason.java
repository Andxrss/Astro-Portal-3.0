package com.anderson.chewy;

public enum Reason {
    APPOINTMENT,
    PICKUP,
    RETURN,
    SUPPORT;

    public static Reason[] valuesPreferredOrder() {
        return new Reason[]{ SUPPORT, PICKUP, APPOINTMENT, RETURN };
    }
}
