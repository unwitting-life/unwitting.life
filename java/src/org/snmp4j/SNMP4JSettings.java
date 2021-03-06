/*_############################################################################
  _##
  _##  SNMP4J - SNMP4JSettings.java
  _##
  _##  Copyright 2003-2007  Frank Fock and Jochen Katz (SNMP4J.org)
  _##
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##
  _##########################################################################*/

package org.snmp4j;

/**
 * The <code>SNMP4JSettings</code> class implements a central configuration
 * class of the SNMP4J framework. As a rule of thumb, changes to the default
 * configuration should be made before any other classes of the SNMP4J API are
 * instantiated or referenced by the application code.
 *
 * @author Frank Fock
 * @version 1.8.1
 * @since 1.5
 */
public final class SNMP4JSettings {

    /**
     * Specifies whether SNMP4J can be extended by own implementation of
     * security protocols, transport mappings, address types, SMI syntaxes, etc.
     * through property files defined via System properties.
     * If set to <code>false</code> all classes SNMP4J is aware of will be
     * used hard coded which speeds up initialization and is required to use
     * SNMP4J in a secure environment where System properties are not available
     * (i.e. in an unsigned applet).
     *
     * @since 1.2.2
     */
    private static boolean extensibilityEnabled = false;

    /**
     * By default SNMP4J (and SNMP4J-Agent*) catch runtime exceptions at thread
     * boundaries of API controlled threads. In SNMP4J such a thread runs in each
     * {@link TransportMapping} and in each {@link Snmp} session object. To ensure
     * robust runtime behavior, unexpected runtime exceptions are caught and
     * logged. If you need to localize and debug such exceptions then set this
     * value to <code>true</code>.
     *
     * @since 1.8.1
     */
    private static volatile boolean forwardRuntimeExceptions = false;

    /**
     * Enables (or disables) the extensibility feature of SNMP4J. When enabled,
     * SNMP4J checks certain properties files that describe which transport
     * mappings, address types, SMI syntaxes, security protocols, etc. should be
     * supported by SNMP4J.
     * <p>
     * By default, the extensibility feature is disabled which provides a faster
     * startup and since no system properties are read, it ensures that SNMP4J
     * can be used also in secure environments like applets.
     *
     * @param enable if <code>true</code> activates extensibility or if <code>false</code>
     *               disables it. In the latter case, SNMP4J's default configuration will
     *               be used with all available features.
     * @since 1.2.2
     */
    public static void setExtensibilityEnabled(boolean enable) {
        extensibilityEnabled = enable;
    }

    /**
     * Tests if the extensibility feature is enabled.
     *
     * @return if <code>true</code> the extensibility is enabled otherwise it is
     * disabled. In the latter case, SNMP4J's default configuration will
     * be used with all available features.
     */
    public final static boolean isExtensibilityEnabled() {
        return extensibilityEnabled;
    }

    /**
     * Enables or disables runtime exception forwarding.
     *
     * @param forwardExceptions <code>true</code> runtime exceptions are thrown on thread boundaries
     *                          controlled by SNMP4J and related APIs. Default is <code>false</code>.
     * @see #forwardRuntimeExceptions
     * @since 1.8.1
     */
    public static void setForwardRuntimeExceptions(boolean forwardExceptions) {
        forwardRuntimeExceptions = forwardExceptions;
    }

    /**
     * Indicates whether runtime exceptions should be thrown on thread boundaries
     * controlled by SNMP4J and related APIs.
     *
     * @return <code>true</code> runtime exceptions are thrown on thread boundaries
     * controlled by SNMP4J and related APIs. Default is <code>false</code>.
     * @since 1.8.1
     */
    public final static boolean isFowardRuntimeExceptions() {
        return forwardRuntimeExceptions;
    }
}
