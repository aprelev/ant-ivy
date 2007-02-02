/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ivy.core.module.descriptor;

import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.plugins.namespace.Namespace;
import org.apache.ivy.util.extendable.ExtendableItem;


/**
 * @author x.hanin
 *
 */
public interface DependencyDescriptor extends ExtendableItem {
    ModuleId getDependencyId();
    /**
     * Used to indicate that this revision must be used in case of conflicts, independently
     * of conflicts manager. This only works for direct dependencies, and not transitive ones.
     * @return true if this dependency should be used, false if conflicts manager
     * can do its work.
     */
    boolean isForce();
    /**
     * Used to indicate that this dependency is a changing one.
     * A changing dependency in ivy means that the revision may have its artifacts modified
     * without revision change. When new artifacts are published a new ivy file should also
     * be published with a new publication date to indicate to ivy that artifacts have changed and that they 
     * should be downloaded again. 
     * @return true if this dependency is a changing one
     */
    boolean isChanging();
    boolean isTransitive();
    ModuleRevisionId getParentRevisionId();
    ModuleRevisionId getDependencyRevisionId();
    String[] getModuleConfigurations();
    String[] getDependencyConfigurations(String moduleConfiguration, String requestedConfiguration);
    String[] getDependencyConfigurations(String moduleConfiguration);
    String[] getDependencyConfigurations(String[] moduleConfigurations);
    Namespace getNamespace();
    DependencyArtifactDescriptor[] getAllDependencyArtifactsIncludes();
    DependencyArtifactDescriptor[] getDependencyArtifactsIncludes(String moduleConfigurations);
    DependencyArtifactDescriptor[] getDependencyArtifactsIncludes(String[] moduleConfigurations);
    DependencyArtifactDescriptor[] getAllDependencyArtifactsExcludes();
    DependencyArtifactDescriptor[] getDependencyArtifactsExcludes(String moduleConfigurations);
    DependencyArtifactDescriptor[] getDependencyArtifactsExcludes(String[] moduleConfigurations);
    boolean doesExclude(String[] moduleConfigurations, ArtifactId artifactId);
    /**
     * Returns true if this descriptor contains any exclusion rule
     * @return true if this descriptor contains any exclusion rule
     */
    public boolean canExclude();
	DependencyDescriptor asSystem();
}
