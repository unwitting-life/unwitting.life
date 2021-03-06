/*_############################################################################
  _## 
  _##  SNMP4J - AbstractTransportMapping.java  
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


package org.snmp4j.transport;

import org.snmp4j.MessageDispatcher;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * The <code>AbstractTransportMapping</code> provides an abstract
 * implementation for the message dispatcher list and the maximum inbound
 * message size.
 *
 * @author Frank Fock
 * @version 1.6
 */
public abstract class AbstractTransportMapping implements TransportMapping {

    protected Vector transportListener = new Vector(1);
    protected int maxInboundMessageSize = (1 << 16) - 1;
    protected boolean asyncMsgProcessingSupported = true;

    public abstract Class getSupportedAddressClass();

    public abstract void sendMessage(Address address, byte[] message)
            throws IOException;

    public void addMessageDispatcher(MessageDispatcher dispatcher) {
        addTransportListener(dispatcher);
    }

    public void removeMessageDispatcher(MessageDispatcher dispatcher) {
        removeTransportListener(dispatcher);
    }

    public synchronized void addTransportListener(TransportListener l) {
        Vector v = (transportListener == null) ?
                new Vector(2) : (Vector) transportListener.clone();
        if (!v.contains(l)) {
            v.addElement(l);
            transportListener = v;
        }
    }

    public synchronized void removeTransportListener(TransportListener l) {
        if (transportListener != null && transportListener.contains(l)) {
            Vector v = (Vector) transportListener.clone();
            v.removeElement(l);
            transportListener = v;
        }
    }

    protected void fireProcessMessage(Address address, ByteBuffer buf) {
        if (transportListener != null) {
            for (int i = 0; i < transportListener.size(); i++) {
                TransportListener l;
                synchronized (this) {
                    l = (TransportListener) transportListener.get(i);
                }
                l.processMessage(this, address, buf);
            }
        }
    }


    public abstract void close() throws IOException;

    public abstract void listen() throws IOException;

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    /**
     * Returns <code>true</code> if asynchronous (multi-threaded) message
     * processing may be implemented. The default is <code>true</code>.
     *
     * @return if <code>false</code> is returned the
     * {@link MessageDispatcher#processMessage}
     * method must not return before the message has been entirely processed.
     */
    public boolean isAsyncMsgProcessingSupported() {
        return asyncMsgProcessingSupported;
    }

    /**
     * Specifies whether this transport mapping has to support asynchronous
     * messages processing or not.
     *
     * @param asyncMsgProcessingSupported if <code>false</code> the {@link MessageDispatcher#processMessage}
     *                                    method must not return before the message has been entirely processed,
     *                                    because the incoming message buffer is not copied before the message
     *                                    is being processed. If <code>true</code> the message buffer is copied
     *                                    for each call, so that the message processing can be implemented
     *                                    asynchronously.
     */
    public void setAsyncMsgProcessingSupported(
            boolean asyncMsgProcessingSupported) {
        this.asyncMsgProcessingSupported = asyncMsgProcessingSupported;
    }

}
