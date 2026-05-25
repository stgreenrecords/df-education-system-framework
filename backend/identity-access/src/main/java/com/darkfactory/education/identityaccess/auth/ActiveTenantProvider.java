package com.darkfactory.education.identityaccess.auth;

import java.util.UUID;

public interface ActiveTenantProvider {
    UUID getActiveTenantId();
}

