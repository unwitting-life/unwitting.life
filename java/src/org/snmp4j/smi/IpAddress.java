/*_############################################################################
  _##
  _##  SNMP4J - IpAddress.java
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

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The <code>IpAddress</code> class represents an IPv4 address SNMP variable.
 *
 * @author Frank Fock
 * @version 1.7
 * @since 1.0
 */
public class IpAddress extends SMIAddress {

    private static final long serialVersionUID = -146846354059565449L;

    private static final LogAdapter logger =
            LogFactory.getLogger(AbstractVariable.class);

    private static final byte[] IPANYADDRESS = {0, 0, 0, 0};

    public static final InetAddress ANY_IPADDRESS = createAnyAddress();

    private java.net.InetAddress inetAddress;

    /**
     * Creates a <code>0.0.0.0</code> IP address.
     */
    public IpAddress() {
        this.inetAddress = ANY_IPADDRESS;
    }

    /**
     * Creates an IP address from an <code>InetAddress</code>
     *
     * @param address an <code>InetAddress</code> instance (must not necessarily be a IPv4
     *                address).
     */
    public IpAddress(InetAddress address) {
        if (address == null) {
            throw new NullPointerException();
        }
        this.inetAddress = address;
    }

    /**
     * Create an IP address from an address string.
     *
     * @param address an IP address String.
     * @see Address#parseAddress(String address)
     */
    public IpAddress(String address) {
        if (!parseAddress(address)) {
            throw new IllegalArgumentException(address);
        }
    }

    public int getSyntax() {
        return SMIConstants.SYNTAX_IPADDRESS;
    }

    public boolean isValid() {
        return (inetAddress != null);
    }

    public String toString() {
        String addressString = inetAddress.toString();
        return addressString.substring(addressString.indexOf('/') + 1);
    }

    public int hashCode() {
        if (inetAddress != null) {
            return inetAddress.hashCode();
        }
        return 0;
    }

    /**
     * Parses an IP address string and returns the corresponding
     * <code>IpAddress</code> instance.
     *
     * @param address an IP address string which may be a host name or a numerical IP address.
     * @return an <code>IpAddress</code> instance or <code>null</code> if
     * <code>address</code> cannot not be parsed.
     * @see Address#parseAddress(String address)
     */
    public static Address parse(String address) {
        try {
            InetAddress addr = InetAddress.getByName(address);
            return new IpAddress(addr);
        } catch (Exception ex) {
            logger.error("Unable to parse IpAddress from: " + address, ex);
            return null;
        }
    }

    public boolean parseAddress(String address) {
        try {
            inetAddress = InetAddress.getByName(address);
            return true;
        } catch (UnknownHostException uhex) {
            return false;
        }
    }

    public int compareTo(Object o) {
        OctetString a = new OctetString(inetAddress.getAddress());
        return a.compareTo(new OctetString(((IpAddress) o).getInetAddress().getAddress()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof IpAddress)) {
            return false;
        }
        return (compareTo(o) == 0);
    }

    public void decodeBER(BERInputStream inputStream) throws java.io.IOException {
        BER.MutableByte type = new BER.MutableByte();
        byte[] value = BER.decodeString(inputStream, type);
        if (type.getValue() != BER.IPADDRESS) {
            throw new IOException("Wrong type encountered when decoding Counter: " +
                    type.getValue());
        }
        if (value.length != 4) {
            throw new IOException("IpAddress encoding error, wrong length: " +
                    value.length);
        }
        inetAddress = InetAddress.getByAddress(value);
    }

    public void encodeBER(OutputStream outputStream) throws java.io.IOException {
        byte[] address = new byte[4];
        System.arraycopy(inetAddress.getAddress(), 0, address, 0, 4);
        BER.encodeString(outputStream, BER.IPADDRESS, inetAddress.getAddress());
    }

    public int getBERLength() {
        return 6;
    }

    public void setAddress(byte[] rawValue) throws UnknownHostException {
        this.inetAddress = InetAddress.getByAddress(rawValue);
    }

    public void setInetAddress(java.net.InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    private static InetAddress createAnyAddress() {
        try {
            return InetAddress.getByAddress(IPANYADDRESS);
        } catch (Exception ex) {
            logger.error("Unable to create any IpAddress: " + ex.getMessage(), ex);
        }
        return null;
    }

    public Object clone() {
        return new IpAddress(inetAddress);
    }

    public int toInt() {
        throw new UnsupportedOperationException();
    }

    public long toLong() {
        throw new UnsupportedOperationException();
    }

    public OID toSubIndex(boolean impliedLength) {
        byte[] address = new byte[4];
        System.arraycopy(inetAddress.getAddress(), 0, address, 0, 4);
        OID subIndex = new OID(new int[4]);
        for (int i = 0; i < address.length; i++) {
            subIndex.set(i, address[i] & 0xFF);
        }
        return subIndex;
    }

    public void fromSubIndex(OID subIndex, boolean impliedLength) {
        byte[] rawValue = new byte[4];
        for (int i = 0; i < rawValue.length; i++) {
            rawValue[i] = (byte) (subIndex.get(i) & 0xFF);
        }
        try {
            setAddress(rawValue);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    public void setValue(String value) {
        if (!parseAddress(value)) {
            throw new IllegalArgumentException(value + " cannot be parsed by " +
                    getClass().getName());
        }
    }

}

