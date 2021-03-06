/*_############################################################################
  _## 
  _##  SNMP4J - TreeListener.java  
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

import java.util.EventListener;

/**
 * The <code>TreeListener</code> interface is implemented by objects
 * listening for tree events.
 *
 * @author Frank Fock
 * @version 1.8
 * @see TreeUtils
 * @since 1.8
 */
public interface TreeListener extends EventListener {

    /**
     * Consumes the next table event, which is typically the next row in a
     * table retrieval operation.
     *
     * @param event a <code>TableEvent</code> instance.
     * @return <code>true</code> if this listener wants to receive more events,
     * otherwise return <code>false</code>. For example, a
     * <code>TreeListener</code> can return <code>false</code> to stop
     * tree retrieval.
     */
    boolean next(TreeEvent event);

    /**
     * Indicates in a series of tree events that no more events will follow.
     *
     * @param event a <code>TreeEvent</code> instance that will either indicate an error
     *              ({@link TreeEvent#isError()} returns <code>true</code>) or success
     *              of the tree retrieval operation.
     */
    void finished(TreeEvent event);

}
