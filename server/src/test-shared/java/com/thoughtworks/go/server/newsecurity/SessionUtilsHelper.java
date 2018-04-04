/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.server.newsecurity;

import com.thoughtworks.go.server.newsecurity.models.AccessToken;
import com.thoughtworks.go.server.newsecurity.models.AnonymousCredential;
import com.thoughtworks.go.server.newsecurity.models.AuthenticationToken;
import com.thoughtworks.go.server.newsecurity.models.UsernamePassword;
import com.thoughtworks.go.server.newsecurity.utils.SessionUtils;
import com.thoughtworks.go.server.security.userdetail.GoUserPrinciple;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public class SessionUtilsHelper {

    public static void setCurrentUser(HttpServletRequest request, String username,
                                      GrantedAuthority... grantedAuthorities) {
        SessionUtils.setAuthenticationTokenAfterRecreatingSession(createUsernamePasswordAuthentication(username, "p@ssw0rd", 0L, grantedAuthorities), request);
        SessionUtils.setCurrentUser(SessionUtils.getAuthenticationToken(request).getUser());
    }

    public static void setAuthenticationToken(HttpServletRequest request, AuthenticationToken<?> authenticationToken) {
        SessionUtils.setAuthenticationTokenAfterRecreatingSession(authenticationToken, request);
    }

    public static AuthenticationToken<UsernamePassword> createUsernamePasswordAuthentication(String username,
                                                                                             String password,
                                                                                             long authenticatedAt,
                                                                                             GrantedAuthority... grantedAuthorities) {
        final GoUserPrinciple goUserPrinciple = new GoUserPrinciple(username, username, grantedAuthorities);
        return new AuthenticationToken<>(goUserPrinciple,
                new UsernamePassword(username, password),
                null,
                authenticatedAt,
                null);
    }

    public static AuthenticationToken<AccessToken> createWebAuthentication(Map<String, String> accessToken,
                                                                           long authenticatedAt,
                                                                           GrantedAuthority... grantedAuthorities) {
        final GoUserPrinciple goUserPrinciple = new GoUserPrinciple("bob", "Bob", grantedAuthorities);
        return new AuthenticationToken<>(goUserPrinciple,
                new AccessToken(accessToken),
                null,
                authenticatedAt,
                null);
    }

    public static GoUserPrinciple loginAsAnonymous() {
        SessionUtils.setCurrentUser(GoUserPrinciple.ANONYMOUS_WITH_SECURITY_ENABLED);
        return GoUserPrinciple.ANONYMOUS_WITH_SECURITY_ENABLED;
    }

    public static GoUserPrinciple loginAsAnonymous(HttpServletRequest request) {
        SessionUtils.setAuthenticationTokenAfterRecreatingSession(new AuthenticationToken<>(GoUserPrinciple.ANONYMOUS_WITH_SECURITY_ENABLED, AnonymousCredential.INSTANCE, null, 0L, null), request);
        return loginAsAnonymous();
    }

    public static GoUserPrinciple loginAs(String username, GrantedAuthority... grantedAuthorities) {
        final GoUserPrinciple goUserPrinciple = new GoUserPrinciple(username, username, grantedAuthorities);
        SessionUtils.setCurrentUser(goUserPrinciple);
        return goUserPrinciple;
    }


    public static GoUserPrinciple loginAsRandomUser(HttpServletRequest request,
                                                    GrantedAuthority... grantedAuthorities) {
        final GoUserPrinciple goUserPrinciple = loginAsRandomUser(grantedAuthorities);
        final AuthenticationToken<UsernamePassword> authenticationToken = new AuthenticationToken<>(goUserPrinciple,
                new UsernamePassword(goUserPrinciple.getUsername(), "p@ssw0rd"), null, 0L, null);
        SessionUtils.setAuthenticationTokenAfterRecreatingSession(authenticationToken, request);
        return goUserPrinciple;
    }

    public static GoUserPrinciple loginAsRandomUser(GrantedAuthority... grantedAuthorities) {
        final String username = "bob-" + UUID.randomUUID().toString();
        return loginAs(username, grantedAuthorities);
    }

    public static void setAuthenticationToken(HttpServletRequest request, String username,
                                              GrantedAuthority... grantedAuthorities) {
        SessionUtils.setAuthenticationTokenAfterRecreatingSession(createUsernamePasswordAuthentication(username, "p@ssw0rd", 0L, grantedAuthorities), request);
    }


}