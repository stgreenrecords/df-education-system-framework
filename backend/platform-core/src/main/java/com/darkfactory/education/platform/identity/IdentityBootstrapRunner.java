package com.darkfactory.education.platform.identity;

import com.darkfactory.education.identityaccess.auth.IdentityBootstrapService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class IdentityBootstrapRunner implements ApplicationRunner {

    private final IdentityBootstrapService identityBootstrapService;

    public IdentityBootstrapRunner(IdentityBootstrapService identityBootstrapService) {
        this.identityBootstrapService = identityBootstrapService;
    }

    @Override
    public void run(ApplicationArguments args) {
        identityBootstrapService.ensureBootstrapAdmin();
    }
}

