/*
 * Copyright 2025 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.idp.internal.config;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;

import java.util.Objects;

public class IDPProxyConfiguration implements ProxyConfig {

    /**
     * The hostname of the HTTP proxy e.g. localhost This field also requires the port to be set.
     */
    @Parameter
    @Optional
    @Summary("The host name of the HTTP proxy which also requires the port number to be set.")
    @DisplayName("Host")
    @Placement(tab = "Proxy", order = 1)
    private String host;

    /**
     * The port number of the HTTP proxy e.g. 3128. The port number must be specified if the hostname is also specified.
     */
    @Parameter
    @Optional
    @Summary("The port number of the HTTP proxy which also requires the host name to be set.")
    @DisplayName("Port")
    @Placement(tab = "Proxy", order = 2)
    private int port;

    /**
     * The username which should be supplied to the HTTP proxy on every request to NetSuite. This field is optional, since a user might want to pass through an unauthenticated HTTP
     * proxy.
     */
    @Parameter
    @Optional
    @Summary("User name is optional for HTTP proxies without authentication.")
    @DisplayName("Username")
    @Placement(tab = "Proxy", order = 3)
    private String username;

    /**
     * The password which would be supplied to the HTTP proxy on every request to NetSuite. This field is optional, since a user might want to pass through an unauthenticated HTTP
     * proxy.
     */
    @Parameter
    @Optional
    @Summary("Password is optional for HTTP proxies without authentication.")
    @DisplayName("Password")
    @Password
    @Placement(tab = "Proxy", order = 4)
    private String password;

    /**
     * A list of comma separated hosts against which the proxy should not be used
     */
    @Parameter
    @Optional
    @DisplayName("Non proxied hosts")
    @Placement(tab = "Proxy", order = 5)
    @Summary("A list of comma separated hosts against which the proxy should not be used")
    private String nonProxyHosts;


    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getNonProxyHosts() {
        return nonProxyHosts;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IDPProxyConfiguration)) return false;
        IDPProxyConfiguration that = (IDPProxyConfiguration) o;
        return port == that.port && Objects.equals(host, that.host) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(nonProxyHosts, that.nonProxyHosts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password, nonProxyHosts);
    }
}