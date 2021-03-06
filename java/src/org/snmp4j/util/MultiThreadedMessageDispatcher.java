/*_############################################################################
  _## 
  _##  SNMP4J - MultiThreadedMessageDispatcher.java  
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


package org.snmp4j.util;

import org.snmp4j.*;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.mp.*;
import org.snmp4j.smi.Address;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The <code>MultiThreadedMessageDispatcher</code> class is a decorator
 * for any <code>MessageDispatcher</code> instances that processes incoming
 * message with a supplied <code>ThreadPool</code>. The processing is thus
 * parallelized on up to the size of the supplied thread pool threads.
 *
 * @author Frank Fock
 * @version 1.8
 * @since 1.0.2
 */
public class MultiThreadedMessageDispatcher implements MessageDispatcher {

    private MessageDispatcher dispatcher;
    private ThreadPool threadPool;

    /**
     * Creates a multi thread message dispatcher using the provided
     * <code>ThreadPool</code> to concurrently process incoming messages
     * that are forwarded to the supplied decorated
     * <code>MessageDispatcher</code>.
     *
     * @param threadPool          a <code>ThreadPool</code> instance (that can be shared). <em>The thread
     *                            pool has to be stopped externally.</em>
     * @param decoratedDispatcher the decorated <code>MessageDispatcher</code> that must be
     *                            multi-threading safe.
     */
    public MultiThreadedMessageDispatcher(ThreadPool threadPool,
                                          MessageDispatcher decoratedDispatcher) {
        this.threadPool = threadPool;
        this.dispatcher = decoratedDispatcher;
    }

    public int getNextRequestID() {
        return dispatcher.getNextRequestID();
    }

    public void addMessageProcessingModel(MessageProcessingModel model) {
        dispatcher.addMessageProcessingModel(model);
    }

    public void removeMessageProcessingModel(MessageProcessingModel model) {
        dispatcher.removeMessageProcessingModel(model);
    }

    public MessageProcessingModel getMessageProcessingModel(int messageProcessingModel) {
        return dispatcher.getMessageProcessingModel(messageProcessingModel);
    }

    public void addTransportMapping(TransportMapping transport) {
        dispatcher.addTransportMapping(transport);
    }

    public TransportMapping removeTransportMapping(TransportMapping transport) {
        return dispatcher.removeTransportMapping(transport);
    }

    public Collection getTransportMappings() {
        return dispatcher.getTransportMappings();
    }

    public void addCommandResponder(CommandResponder listener) {
        dispatcher.addCommandResponder(listener);
    }

    public void removeCommandResponder(CommandResponder listener) {
        dispatcher.removeCommandResponder(listener);
    }

    public PduHandle sendPdu(Address transportAddress,
                             int messageProcessingModel,
                             int securityModel,
                             byte[] securityName,
                             int securityLevel,
                             PDU pdu,
                             boolean expectResponse) throws MessageException {
        return dispatcher.sendPdu(transportAddress, messageProcessingModel,
                securityModel, securityName, securityLevel,
                pdu, expectResponse);
    }

    public PduHandle sendPdu(TransportMapping transportMapping,
                             Address transportAddress,
                             int messageProcessingModel,
                             int securityModel,
                             byte[] securityName,
                             int securityLevel,
                             PDU pdu,
                             boolean expectResponse) throws MessageException {
        return dispatcher.sendPdu(transportMapping, transportAddress,
                messageProcessingModel,
                securityModel, securityName,
                securityLevel, pdu, expectResponse);
    }

    public PduHandle sendPdu(TransportMapping transportMapping,
                             Address transportAddress,
                             int messageProcessingModel,
                             int securityModel, byte[] securityName,
                             int securityLevel, PDU pdu, boolean expectResponse,
                             PduHandleCallback callback) throws MessageException {
        return dispatcher.sendPdu(transportMapping, transportAddress,
                messageProcessingModel,
                securityModel, securityName,
                securityLevel, pdu, expectResponse, callback);
    }

    public int returnResponsePdu(int messageProcessingModel,
                                 int securityModel,
                                 byte[] securityName,
                                 int securityLevel,
                                 PDU pdu,
                                 int maxSizeResponseScopedPDU,
                                 StateReference stateReference,
                                 StatusInformation statusInformation)
            throws MessageException {
        return dispatcher.returnResponsePdu(messageProcessingModel,
                securityModel, securityName,
                securityLevel, pdu,
                maxSizeResponseScopedPDU,
                stateReference,
                statusInformation);
    }

    public void processMessage(TransportMapping sourceTransport,
                               Address incomingAddress,
                               BERInputStream wholeMessage) {
        // OK, here wo do all that what this class is all about!
        MessageTask task = new MessageTask(sourceTransport,
                incomingAddress,
                wholeMessage);
        threadPool.execute(task);
    }

    public void processMessage(TransportMapping sourceTransport,
                               Address incomingAddress, ByteBuffer wholeMessage) {
        processMessage(sourceTransport, incomingAddress,
                new BERInputStream(wholeMessage));
    }

    public void releaseStateReference(int messageProcessingModel,
                                      PduHandle pduHandle) {
        dispatcher.releaseStateReference(messageProcessingModel, pduHandle);
    }

    public TransportMapping getTransport(Address destAddress) {
        return dispatcher.getTransport(destAddress);
    }

    class MessageTask implements Runnable {
        private TransportMapping sourceTransport;
        private Address incomingAddress;
        private BERInputStream wholeMessage;

        public MessageTask(TransportMapping sourceTransport,
                           Address incomingAddress,
                           BERInputStream wholeMessage) {
            this.sourceTransport = sourceTransport;
            this.incomingAddress = incomingAddress;
            this.wholeMessage = wholeMessage;
        }

        public void run() {
            dispatcher.processMessage(sourceTransport, incomingAddress, wholeMessage);
        }

    }
}
