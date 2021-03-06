/*_############################################################################
  _## 
  _##  SNMP4J - EnumerationIterator.java  
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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * The <code>EnumerationIterator</code> provides an iterator from an
 * {@link Enumeration}.
 *
 * @author Frank Fock
 * @version 1.6.1
 * @since 1.6.1
 */
public class EnumerationIterator implements Iterator {

    private Enumeration e;

    public EnumerationIterator(Enumeration e) {
        this.e = e;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements.
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return e.hasMoreElements();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public Object next() {
        return e.nextElement();
    }

    /**
     * This method is not supported for enumerations.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
