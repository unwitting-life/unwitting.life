/*_############################################################################
  _## 
  _##  SNMP4J - TcpAddress.java  
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


package org.snmp4j.smi;

import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;

import java.net.InetAddress;

/**
 * The <code>TcpAddress</code> represents TCP/IP transport addresses.
 *
 * @author Frank Fock
 * @version 1.0
 */

public class TcpAddress extends TransportIpAddress {

    static final long serialVersionUID = 1165319744164017388L;

    private static final LogAdapter logger = LogFactory.getLogger(TcpAddress.class);

    public TcpAddress() {
        super();
    }

    public TcpAddress(InetAddress inetAddress, int port) {
        setInetAddress(inetAddress);
        setPort(port);
    }

    public TcpAddress(int port) {
        super();
        setPort(port);
    }

    public TcpAddress(String address) {
        if (!parseAddress(address)) {
            throw new IllegalArgumentException(address);
        }
    }

    public static Address parse(String address) {
        try {
            TcpAddress a = new TcpAddress();
            if (a.parseAddress(address)) {
                return a;
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }

}
