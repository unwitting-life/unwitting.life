/*_############################################################################
  _## 
  _##  SNMP4J - PDU.java  
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

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BERSerializable;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * The <code>PDU</code> class represents a SNMP protocol data unit. The PDU
 * version supported by the BER decoding and encoding methods of this class
 * is v2.
 * <p>
 * The default PDU type is GET.
 *
 * @author Frank Fock
 * @version 1.1
 * @see PDUv1
 * @see ScopedPDU
 */
public class PDU implements BERSerializable, Serializable {

    private static final long serialVersionUID = 7607672475629607472L;

    /**
     * Denotes a get PDU.
     */
    public static final int GET = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x0);
    /**
     * Denotes a getnext (search) PDU.
     */
    public static final int GETNEXT = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x1);
    /**
     * Denotes a response PDU.
     */
    public static final int RESPONSE = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x2);
    /**
     * Denotes a set PDU.
     */
    public static final int SET = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x3);
    /**
     * Denotes a SNMPv1 trap PDU. This type can only be used with instances of the
     * {@link PDUv1} class.
     */
    public static final int V1TRAP = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x4);
    /**
     * Denotes a SNMPv2c/v3 getbulk PDU.
     */
    public static final int GETBULK = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x5);
    /**
     * Denotes a SNMPv2c/v3 inform PDU (unprecisely also known as a confirmed
     * notification).
     */
    public static final int INFORM = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x6);
    /**
     * Denotes a SNMPv2c/v3 notification PDU (undistinguishable from
     * {@link #TRAP}).
     */
    public static final int TRAP = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x7);
    /**
     * Denotes a SNMPv2c/v3 notification PDU (undistinguishable from
     * {@link #NOTIFICATION}).
     */
    public static final int NOTIFICATION = TRAP;
    /**
     * Denotes a SNMPv3 report PDU.
     */
    public static final int REPORT = (BER.ASN_CONTEXT | BER.ASN_CONSTRUCTOR | 0x8);


    // Error status constants

    /**
     * Operation success (no error).
     */
    public static final int noError = SnmpConstants.SNMP_ERROR_SUCCESS;

    /**
     * PDU encoding is too big for the transport used.
     */
    public static final int tooBig = SnmpConstants.SNMP_ERROR_TOO_BIG;

    /**
     * No such variable binding name, see error index.
     */
    public static final int noSuchName = SnmpConstants.SNMP_ERROR_NO_SUCH_NAME;

    /**
     * Bad value in variable binding, see error index.
     */
    public static final int badValue = SnmpConstants.SNMP_ERROR_BAD_VALUE;

    /**
     * The variable binding is read-only, see error index.
     */
    public static final int readOnly = SnmpConstants.SNMP_ERROR_READ_ONLY;

    /**
     * An unspecific error caused by a variable binding, see error index.
     */
    public static final int genErr = SnmpConstants.SNMP_ERROR_GENERAL_ERROR;

    /**
     * The variable binding is not accessible by the current MIB view, see error
     * index.
     */
    public static final int noAccess = SnmpConstants.SNMP_ERROR_NO_ACCESS;

    /**
     * The variable binding's value has the wrong type, see error index.
     */
    public static final int wrongType = SnmpConstants.SNMP_ERROR_WRONG_TYPE;

    /**
     * The variable binding's value has the wrong length, see error index.
     */
    public static final int wrongLength = SnmpConstants.SNMP_ERROR_WRONG_LENGTH;

    /**
     * The variable binding's value has a value that could under no circumstances
     * be assigned, see error index.
     */
    public static final int wrongValue = SnmpConstants.SNMP_ERROR_WRONG_VALUE;

    /**
     * The variable binding's value has the wrong encoding, see error index.
     */
    public static final int wrongEncoding =
            SnmpConstants.SNMP_ERROR_WRONG_ENCODING;

    /**
     * The specified object does not exists and cannot be created,
     * see error index.
     */
    public static final int noCreation = SnmpConstants.SNMP_ERROR_NO_CREATION;

    /**
     * The variable binding's value is presently inconsistent with the current
     * state of the target object, see error index.
     */
    public static final int inconsistentValue =
            SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE;

    /**
     * The resource needed to assign a variable binding's value is presently
     * unavailable, see error index.
     */
    public static final int resourceUnavailable =
            SnmpConstants.SNMP_ERROR_RESOURCE_UNAVAILABLE;

    /**
     * Unable to commit a value, see error index.
     */
    public static final int commitFailed = SnmpConstants.SNMP_ERROR_COMMIT_FAILED;

    /**
     * Unable to undo a committed value, see error index.
     */
    public static final int undoFailed = SnmpConstants.SNMP_ERROR_UNDO_FAILED;

    /**
     * Unauthorized access, see error index.
     */
    public static final int authorizationError =
            SnmpConstants.SNMP_ERROR_AUTHORIZATION_ERROR;

    /**
     * The variable's value cannot be modified, see error index.
     */
    public static final int notWritable = SnmpConstants.SNMP_ERROR_NOT_WRITEABLE;

    /**
     * The specified object does not exists and presently it cannot be created,
     * see error index.
     */
    public static final int inconsistentName =
            SnmpConstants.SNMP_ERROR_INCONSISTENT_NAME;

    protected Vector variableBindings = new Vector();
    protected Integer32 errorStatus = new Integer32();
    protected Integer32 errorIndex = new Integer32();
    protected Integer32 requestID = new Integer32();
    protected int type = GET;

    /**
     * Default constructor.
     */
    public PDU() {
    }

    /**
     * Copy constructor.
     *
     * @param other the <code>PDU</code> to copy from.
     */
    public PDU(PDU other) {
        variableBindings = (Vector) other.variableBindings.clone();
        errorIndex = (Integer32) other.errorIndex.clone();
        errorStatus = (Integer32) other.errorStatus.clone();
        type = other.type;
        requestID = (Integer32) other.requestID.clone();
    }

    /**
     * Adds a variable binding to this PDU. A <code>NullPointerException</code>
     * is thrown if <code>VariableBinding</code> or its <code>Variable</code> is
     * <code>null</code>.
     * <p>
     * The added <code>VariableBinding</code> is cloned before the clone is added
     * when the {@link Variable} is a dynamic one (see
     * {@link Variable#isDynamic()}).
     *
     * @param vb a <code>VariableBinding</code> instance.
     */
    public void add(VariableBinding vb) {
        if (vb.getVariable().isDynamic()) {
            VariableBinding cvb = (VariableBinding) vb.clone();
            variableBindings.add(cvb);
        } else {
            variableBindings.add(vb);
        }
    }

    /**
     * Adds a new variable binding to this PDU by using the OID of the supplied
     * <code>VariableBinding</code>. The value portion is thus set to
     * <code>null</code>.
     * <p>
     * This method should be used for GET type requests. For SET, TRAP and INFORM
     * requests, the {@link #add} method should be used instead.
     *
     * @param vb a <code>VariableBinding</code> instance.
     * @since 1.8
     */
    public void addOID(VariableBinding vb) {
        VariableBinding cvb = new VariableBinding(vb.getOid());
        variableBindings.add(cvb);
    }

    /**
     * Adds an array of variable bindings to this PDU (see
     * {@link #add(VariableBinding vb)}).
     *
     * @param vbs an array of <code>VariableBinding</code> instances. The instances in the
     *            array will be appended to the current list of variable bindings in the
     *            PDU.
     */
    public void addAll(VariableBinding[] vbs) {
        variableBindings.ensureCapacity(variableBindings.size() + vbs.length);
        for (int i = 0; i < vbs.length; i++) {
            add(vbs[i]);
        }
    }

    /**
     * Adds new <code>VariableBindings</code> each with the OID of the
     * corresponding variable binding of the supplied array to this PDU (see
     * {@link #addOID(VariableBinding vb)}).
     *
     * @param vbs an array of <code>VariableBinding</code> instances. For each instance
     *            in the supplied array, a new VariableBinding created by
     *            <code>new VariableBinding(OID)</code> will be appended to the current
     *            list of variable bindings in the PDU.
     * @since 1.8
     */
    public void addAllOIDs(VariableBinding[] vbs) {
        variableBindings.ensureCapacity(variableBindings.size() + vbs.length);
        for (int i = 0; i < vbs.length; i++) {
            addOID(vbs[i]);
        }
    }


    /**
     * Gets the variable binding at the specified position.
     *
     * @param index a zero based positive integer (<code>0 <= index < {@link #size()}</code>)
     * @return a VariableBinding instance. If <code>index</code> is out of bounds
     * an exception is thrown.
     */
    public VariableBinding get(int index) {
        return (VariableBinding) variableBindings.get(index);
    }

    /**
     * Sets the variable binding at the specified position.
     *
     * @param index a zero based positive integer (<code>0 <= index < {@link #size()}</code>)
     *              If <code>index</code> is out of bounds
     *              an exception is thrown.
     * @param vb    a VariableBinding instance (<code>null</code> is not allowed).
     * @return the variable binding that has been replaced.
     */
    public VariableBinding set(int index, VariableBinding vb) {
        if (vb == null) {
            throw new NullPointerException("Variable binding must not be null");
        }
        return (VariableBinding) variableBindings.set(index, vb);
    }

    /**
     * Removes the variable binding at the supplied position.
     *
     * @param index a position >= 0 and < {@link #size()}.
     */
    public void remove(int index) {
        variableBindings.remove(index);
    }

    /**
     * Gets the number of variable bindings in the PDU.
     *
     * @return the size of the PDU.
     */
    public int size() {
        return variableBindings.size();
    }

    /**
     * Gets the variable binding vector.
     *
     * @return the internal <code>Vector</code> containing the PDU's variable bindings.
     */
    public Vector getVariableBindings() {
        return variableBindings;
    }

    /**
     * Remove the last variable binding from the PDU, if such an element exists.
     */
    public void trim() {
        if (variableBindings.size() > 0) {
            variableBindings.remove(variableBindings.size() - 1);
        }
    }

    /**
     * Sets the error status of the PDU.
     *
     * @param errorStatus a SNMP error status.
     * @see SnmpConstants
     */
    public void setErrorStatus(int errorStatus) {
        this.errorStatus.setValue(errorStatus);
    }

    /**
     * Gets the error status of the PDU.
     *
     * @return a SNMP error status.
     * @see SnmpConstants
     */
    public int getErrorStatus() {
        return errorStatus.getValue();
    }

    /**
     * Gets a textual description of the error status.
     *
     * @return a String containing an element of the
     * {@link SnmpConstants#SNMP_ERROR_MESSAGES} array for a valid error status.
     * "Unknown error: <errorStatusNumber>" is returned for any other value.
     */
    public String getErrorStatusText() {
        return toErrorStatusText(errorStatus.getValue());
    }

    /**
     * Returns textual description for the supplied error status value.
     *
     * @param errorStatus an error status.
     * @return a String containing an element of the
     * {@link SnmpConstants#SNMP_ERROR_MESSAGES} array for a valid error status.
     * "Unknown error: <errorStatusNumber>" is returned for any other value.
     * @since 1.7
     */
    public static final String toErrorStatusText(int errorStatus) {
        try {
            return SnmpConstants.SNMP_ERROR_MESSAGES[errorStatus];
        } catch (ArrayIndexOutOfBoundsException iobex) {
            return "Unknown error: " + errorStatus;
        }
    }

    /**
     * Sets the error index.
     *
     * @param errorIndex an integer value >= 0 where 1 denotes the first variable binding.
     */
    public void setErrorIndex(int errorIndex) {
        this.errorIndex.setValue(errorIndex);
    }

    /**
     * Gets the error index.
     *
     * @return an integer value >= 0 where 1 denotes the first variable binding.
     */
    public int getErrorIndex() {
        return errorIndex.getValue();
    }

    /**
     * Checks whether this PDU is a confirmed class PDU.
     *
     * @return boolean
     */
    public boolean isConfirmedPdu() {
        return ((type != PDU.REPORT) && (type != PDU.RESPONSE) &&
                (type != PDU.TRAP) && (type != PDU.V1TRAP));
    }

    public int getBERLength() {
        // header for data_pdu
        int length = getBERPayloadLengthPDU();
        length += BER.getBERLengthOfLength(length) + 1;
        // assume maxmimum length here
        return length;
    }

    public int getBERPayloadLength() {
        return getBERPayloadLengthPDU();
    }

    public void decodeBER(BERInputStream inputStream) throws IOException {
        BER.MutableByte pduType = new BER.MutableByte();
        int length = BER.decodeHeader(inputStream, pduType);
        int pduStartPos = (int) inputStream.getPosition();
        switch (pduType.getValue()) {
            case PDU.SET:
            case PDU.GET:
            case PDU.GETNEXT:
            case PDU.GETBULK:
            case PDU.INFORM:
            case PDU.REPORT:
            case PDU.TRAP:
            case PDU.RESPONSE:
                break;
            default:
                throw new IOException("Unsupported PDU type: " + pduType.getValue());
        }
        this.type = pduType.getValue();
        requestID.decodeBER(inputStream);
        errorStatus.decodeBER(inputStream);
        errorIndex.decodeBER(inputStream);

        pduType = new BER.MutableByte();
        int vbLength = BER.decodeHeader(inputStream, pduType);
        if (pduType.getValue() != BER.SEQUENCE) {
            throw new IOException("Encountered invalid tag, SEQUENCE expected: " +
                    pduType.getValue());
        }
        // rest read count
        int startPos = (int) inputStream.getPosition();
        variableBindings = new Vector();
        while (inputStream.getPosition() - startPos < vbLength) {
            VariableBinding vb = new VariableBinding();
            vb.decodeBER(inputStream);
            variableBindings.add(vb);
        }
        if (inputStream.getPosition() - startPos != vbLength) {
            throw new IOException("Length of VB sequence (" + vbLength +
                    ") does not match real length: " +
                    ((int) inputStream.getPosition() - startPos));
        }
        if (BER.isCheckSequenceLength()) {
            BER.checkSequenceLength(length,
                    (int) inputStream.getPosition() - pduStartPos,
                    this);
        }
    }

    protected int getBERPayloadLengthPDU() {
        int length = 0;

        // length for all vbs
        for (int i = 0; i < variableBindings.size(); i++) {
            length += ((VariableBinding) variableBindings.get(i)).getBERLength();
        }

        length += BER.getBERLengthOfLength(length) + 1;

        // req id, error status, error index
        Integer32 i32 = new Integer32(requestID.getValue());
        length += i32.getBERLength();
        i32 = errorStatus;
        length += i32.getBERLength();
        i32 = errorIndex;
        length += i32.getBERLength();
        i32 = null;
        return length;
    }

    public void encodeBER(OutputStream outputStream) throws IOException {
        BER.encodeHeader(outputStream, type, getBERPayloadLengthPDU());

        requestID.encodeBER(outputStream);
        errorStatus.encodeBER(outputStream);
        errorIndex.encodeBER(outputStream);

        int vbLength = 0;
        for (int i = 0; i < variableBindings.size(); i++) {
            vbLength += ((VariableBinding) variableBindings.get(i)).getBERLength();
        }
        BER.encodeHeader(outputStream, BER.SEQUENCE, vbLength);
        for (int i = 0; i < variableBindings.size(); i++) {
            ((VariableBinding) variableBindings.get(i)).encodeBER(outputStream);
        }
    }

    /**
     * Removes all variable bindings from the PDU and sets the request ID to zero.
     * This can be used to reuse a PDU for another request.
     */
    public void clear() {
        variableBindings.clear();
        setRequestID(new Integer32(0));
    }

    /**
     * Sets the PDU type.
     *
     * @param type the type of the PDU (e.g. GETNEXT, SET, etc.)
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the PDU type. The default is {@link PDU#GETNEXT}.
     *
     * @return the PDU's type.
     */
    public int getType() {
        return type;
    }

    public Object clone() {
        return new PDU(this);
    }

    /**
     * Gets the request ID associated with this PDU.
     *
     * @return an <code>Integer32</code> instance.
     */
    public Integer32 getRequestID() {
        return requestID;
    }

    /**
     * Sets the request ID for this PDU. When the request ID is not set or set to
     * zero, the message processing model will generate a unique request ID for
     * the <code>PDU</code> when sent.
     *
     * @param requestID a unique request ID.
     */
    public void setRequestID(Integer32 requestID) {
        this.requestID = requestID;
    }

    /**
     * Gets a string representation of the supplied PDU type.
     *
     * @param type a PDU type.
     * @return a string representation of <code>type</code>, for example "GET".
     */
    public static String getTypeString(int type) {
        switch (type) {
            case PDU.GET:
                return "GET";
            case PDU.SET:
                return "SET";
            case PDU.GETNEXT:
                return "GETNEXT";
            case PDU.GETBULK:
                return "GETBULK";
            case PDU.INFORM:
                return "INFORM";
            case PDU.RESPONSE:
                return "RESPONSE";
            case PDU.REPORT:
                return "REPORT";
            case PDU.TRAP:
                return "TRAP";
            case PDU.V1TRAP:
                return "V1TRAP";
        }
        return "unknown";
    }

    /**
     * Gets the PDU type identifier for a string representation of the type.
     *
     * @param type the string representation of a PDU type: <code>GET, GETNEXT, GETBULK,
     *             SET, INFORM, RESPONSE, REPORT, TRAP, V1TRAP)</code>.
     * @return the corresponding PDU type constant, or <code>Integer.MIN_VALUE</code>
     * of the supplied type is unknown.
     */
    public static int getTypeFromString(String type) {
        if (type.equals("GET")) {
            return PDU.GET;
        } else if (type.equals("SET")) {
            return PDU.SET;
        } else if (type.equals("GETNEXT")) {
            return PDU.GETNEXT;
        } else if (type.equals("GETBULK")) {
            return PDU.GETBULK;
        } else if (type.equals("INFORM")) {
            return PDU.INFORM;
        } else if (type.equals("RESPONSE")) {
            return PDU.RESPONSE;
        } else if (type.equals("TRAP")) {
            return PDU.TRAP;
        } else if (type.equals("V1TRAP")) {
            return PDU.V1TRAP;
        } else if (type.equals("REPORT")) {
            return PDU.REPORT;
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getTypeString(type));
        buf.append("[requestID=");
        buf.append(requestID);
        buf.append(", errorStatus=");
        buf.append(getErrorStatusText() + "(" + errorStatus + ")");
        buf.append(", errorIndex=");
        buf.append(errorIndex);
        buf.append(", VBS[");
        for (int i = 0; i < variableBindings.size(); i++) {
            buf.append(variableBindings.get(i));
            if (i + 1 < variableBindings.size()) {
                buf.append("; ");
            }
        }
        buf.append("]]");
        return buf.toString();
    }

    /**
     * Gets the maximum repetitions of repeatable variable bindings in GETBULK
     * requests.
     *
     * @return an integer value >= 0.
     */
    public int getMaxRepetitions() {
        return errorIndex.getValue();
    }

    /**
     * Sets the maximum repetitions of repeatable variable bindings in GETBULK
     * requests.
     *
     * @param maxRepetitions an integer value >= 0.
     */
    public void setMaxRepetitions(int maxRepetitions) {
        this.errorIndex.setValue(maxRepetitions);
    }

    /**
     * Gets the number of non repeater variable bindings in a GETBULK PDU.
     *
     * @return an integer value >= 0 and <= {@link #size()}
     */
    public int getNonRepeaters() {
        return errorStatus.getValue();
    }

    /**
     * Sets the number of non repeater variable bindings in a GETBULK PDU.
     *
     * @param nonRepeaters an integer value >= 0 and <= {@link #size()}
     */
    public void setNonRepeaters(int nonRepeaters) {
        this.errorStatus.setValue(nonRepeaters);
    }

    /**
     * Returns an array with the variable bindings of this PDU.
     *
     * @return an array of <code>VariableBinding</code> instances of this PDU in the
     * same order as in the PDU.
     */
    public VariableBinding[] toArray() {
        VariableBinding[] vbs = new VariableBinding[this.variableBindings.size()];
        this.variableBindings.toArray(vbs);
        return vbs;
    }
}

