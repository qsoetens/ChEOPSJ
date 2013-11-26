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
package be.ac.ua.ansymo.cheopsj.model.famix;

import hibernate.model.api.IModelProvider;

/**
 * The model provider for the change model classes.
 * 
 * @author qsoetens
 */
public class ModelProvider implements IModelProvider {

	/** 
	 * {@inheritDoc}
	 */
	public Class<?>[] getAnnotatedClasses() {
		Class<?>[] annotatedClasses = { 
				FamixAccess.class,
				FamixArgument.class,
				FamixAssociation.class,
				FamixAttribute.class,
				FamixBehaviouralEntity.class,
				FamixClass.class,
				FamixEntity.class,
				FamixFormalParameter.class,
				FamixFunction.class,
				FamixGlobalVariable.class,
				FamixImplicitVariable.class,
				FamixInheritanceDefinition.class,
				FamixInvocation.class,
				FamixLocalVariable.class,
				FamixMethod.class,
				FamixObject.class,
				FamixPackage.class,
				FamixStructuralEntity.class
		};

		return annotatedClasses;
	}

}
