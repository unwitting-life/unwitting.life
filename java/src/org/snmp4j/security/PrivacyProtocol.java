/*_############################################################################
  _## 
  _##  SNMP4J - PrivacyProtocol.java  
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


package org.snmp4j.security;

import org.snmp4j.smi.OID;

/**
 * The <code>PrivacyProtocol</code> interface defines a common
 * interface for all SNMP privacy protocols.
 *
 * @author Jochen Katz & Frank Fock
 * @version 1.0
 */
public interface PrivacyProtocol extends SecurityProtocol {

    /**
     * Encrypts a message using a given encryption key, engine boots count, and
     * engine ID.
     *
     * @param unencryptedData the unencrypted data. This byte array may contain leading and trailing
     *                        bytes that will not be encrypted.
     * @param offset          the offset into the <code>unencryptedData</code> where to start
     *                        encryption.
     * @param length          the length of the substring starting at <code>offset</code> to encrypt.
     * @param encryptionKey   the key to be used for encryption.
     * @param engineBoots     the engine boots counter to use.
     * @param engineTime      the engine time to use.
     * @param decryptParams   returns the decryption parameters needed to decrypt the data that
     *                        has been encrypted by this method.
     * @return the encrypted copy of <code>unencryptedData</code>.
     */
    byte[] encrypt(byte[] unencryptedData,
                   int offset,
                   int length,
                   byte[] encryptionKey,
                   long engineBoots,
                   long engineTime,
                   DecryptParams decryptParams);

    /**
     * Decrypts a message using a given decryption key, engine boots count, and
     * engine ID.
     *
     * @param cryptedData   the crypted data. This byte array may contain leading and trailing
     *                      bytes that will not be decrypted.
     * @param offset        the offset into the <code>cryptedData</code> where to start
     *                      encryption.
     * @param length        the length of the substring starting at <code>offset</code> to decrypt.
     * @param decryptionKey the key to be used for decryption.
     * @param engineBoots   the engine boots counter to use.
     * @param engineTime    the engine time to use.
     * @param decryptParams contains the decryption parameters.
     * @return the decrypted data, or <code>null</code> if decryption failed.
     */
    byte[] decrypt(byte[] cryptedData,
                   int offset,
                   int length,
                   byte[] decryptionKey,
                   long engineBoots,
                   long engineTime,
                   DecryptParams decryptParams);

    /**
     * Gets the OID uniquely identifying the privacy protocol.
     *
     * @return an <code>OID</code> instance.
     */
    OID getID();

    /**
     * Gets the length of a scoped PDU when encrypted with this security protocol.
     *
     * @param scopedPDULength the length of the (unencrypted) scoped PDU.
     * @return the length of the encrypted scoped PDU.
     */
    int getEncryptedLength(int scopedPDULength);

    /**
     * Gets the minimum key size for this privacy protcol.
     *
     * @return the minimum key size for this privacy protcol.
     */
    int getMinKeyLength();

    /**
     * Gets the maximum key size for this privacy protcol.
     *
     * @return the minimum key size for this privacy protcol.
     */
    int getMaxKeyLength();

    /**
     * Gets the length of the decryption parameters used by this security
     * protocol.
     *
     * @return a positive integer denoting the length of decryption parameters returned
     * by this security protocol.
     */
    int getDecryptParamsLength();
}

