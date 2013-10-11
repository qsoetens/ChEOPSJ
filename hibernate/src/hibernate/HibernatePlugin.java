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
package hibernate;

import hibernate.model.api.IModelProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author wuersch
 */
public class HibernatePlugin extends Plugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.evolizer.core.hibernate";

    // The shared instance
    private static HibernatePlugin sPlugin;

    /**
     * The constructor.
     */
    public HibernatePlugin() {
        HibernatePlugin.sPlugin = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        HibernatePlugin.sPlugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static HibernatePlugin getDefault() {
        return HibernatePlugin.sPlugin;
    }

    /**
     * Queries all model providers and returns ejb3-annotated classes.
     * 
     * @return A list containing classes that are annotated with ejb3-tags for Hibernate mapping.
     * @throws Exception 
     */
    public List<Class<?>> gatherModels() throws Exception {
        List<Class<?>> annotatedClasses = new ArrayList<Class<?>>();

        // Iterate over all extensions and gather classes that are hibernate annotated
        IExtension[] extensions =
                Platform.getExtensionRegistry().getExtensionPoint(HibernatePlugin.PLUGIN_ID, "modelProvider")
                        .getExtensions();
        for (IExtension element : extensions) {
            IConfigurationElement[] configElements = element.getConfigurationElements();
            for (IConfigurationElement configElement : configElements) {
                try {
                    IModelProvider provider =
                            (IModelProvider) configElement.createExecutableExtension("class");
                    // Throws CoreException if executable could not be created
                    Class<?>[] classes = provider.getAnnotatedClasses();

                    for (Class<?> element1 : classes) {
                        annotatedClasses.add(element1);
                    }


                } catch (CoreException exception) {
                    String message =
                            "Could not create executable extension from " + configElement.getContributor() + ". "
                                    + exception.getMessage();


                    throw new Exception(message);
                }
            }
        }

        return annotatedClasses;
    }

    /**
     * Opens a file located within the plugin-bundle.
     * 
     * @param filePath
     *            relative path of the file starting at the root of this plugin
     * @return an InputStream reading the specified file
     * @throws IOException
     *             if file could not be opened
     */
    public static InputStream openBundledFile(String filePath) throws IOException {
        return HibernatePlugin.getDefault().getBundle().getEntry(filePath).openStream();
    }

}
