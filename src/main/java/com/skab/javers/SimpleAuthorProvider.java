package com.skab.javers;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SimpleAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        String providerName = "system";
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (!auth.getName().equals("anonymousUser")) {
                providerName = auth.getName();
            }
        }
        return providerName;
    }
}
