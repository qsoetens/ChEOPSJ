/*
 * Copyright 2009 University of Zurich, Switzerland
 * Copyright 2013 Quinten Soetens - Adapted from org.evolizer.core
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hibernate;

public class HibernateException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3516052023706918053L;

	/**
     * Constructor.
     * 
     * @param message
     *            the error message
     */
    public HibernateException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            the exception that was caught and re-thrown
     */
    public HibernateException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     *            an error message.
     * @param cause
     *            the exception that was caught and re-thrown
     */
    public HibernateException(String message, Throwable cause) {
        super(message, cause);
    }
}
