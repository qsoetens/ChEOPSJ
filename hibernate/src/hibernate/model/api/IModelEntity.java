/*
 * Copyright 2009 University of Zurich, Switzerland
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
package hibernate.model.api;

/**
 * All domain objects of Evolizer should implement this interface to ensure that future visualizations and tools are
 * able to represent Evolizer data in a meaningful way.
 * 
 * @author wuersch
 */
public interface IModelEntity {

    /**
     * Returns a Long that is unique for all instances of the class that implements {@link IModelEntity} .
     * 
     * @return an unique id
     */
    public Long getId();

    /**
     * Returns a String that uniquely identifies an entity among all the instances of all model classes.
     * 
     * @return a unique identifier
     */
    // TODO: unique naming scheme? Project information is probably important.
    public String getURI();

    /**
     * Returns a short (preferably a single word) descriptor of the entity that can be used for example in
     * visualizations or natural language processing.
     * 
     * @return a short but meaningful name
     */
    public String getLabel();
}
