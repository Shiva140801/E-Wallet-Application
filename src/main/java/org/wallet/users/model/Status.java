package org.wallet.users.model;


public enum Status {
    /**
     * Success status.
     */
    SUCCESS("success"),
    /**
     * Failed status.
     */
    FAILED("failed"),
    /**
     * Error status.
     */
    ERROR("error");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
