/*_############################################################################
  _## 
  _##  SNMP4J - NoLogger.java  
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

package org.snmp4j.log;

import java.util.Collections;
import java.util.Iterator;

/**
 * The <code>NoLogger</code> implements a <code>LogAdapter</code> that does
 * not perform any logging.
 *
 * @author Frank Fock
 * @version 1.6.1
 * @since 1.2.1
 */
public class NoLogger implements LogAdapter {

    static final NoLogger instance = new NoLogger();

    private NoLogger() {
    }

    public void debug(Object message) {
    }

    public void error(Object message) {
    }

    public void error(Object message, Throwable t) {
    }

    public void info(Object message) {
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public boolean isInfoEnabled() {
        return false;
    }

    public boolean isWarnEnabled() {
        return false;
    }

    public void warn(Object message) {
    }

    public void fatal(Object message) {
    }

    public void fatal(Object message, Throwable throwable) {
    }

    public void setLogLevel(LogLevel level) {
    }

    public String getName() {
        return "";
    }

    public LogLevel getLogLevel() {
        return LogLevel.OFF;
    }

    public LogLevel getEffectiveLogLevel() {
        return LogLevel.OFF;
    }

    public Iterator getLogHandler() {
        return Collections.EMPTY_LIST.iterator();
    }

}
