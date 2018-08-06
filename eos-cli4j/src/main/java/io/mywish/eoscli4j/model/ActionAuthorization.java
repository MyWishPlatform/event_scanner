package io.mywish.eoscli4j.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActionAuthorization {
    private final static String ACTIVE_PERMISSION = "active";
    private final static String OWNER_PERMISSION = "owner";
    private final String actor;
    private final String permission;
}
