package com.atomgdx.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Global registry for currently open LibGDX game projects in AtomGdx Studio.
 */
public class LibGdxProjectRegistry {
    private static final LibGdxProjectRegistry INSTANCE = new LibGdxProjectRegistry();

    private final List<LibGdxProject> openProjects = new CopyOnWriteArrayList<>();
    private LibGdxProject activeProject;

    private LibGdxProjectRegistry() {
    }

    public static LibGdxProjectRegistry getInstance() {
        return INSTANCE;
    }

    public void registerProject(LibGdxProject project) {
        if (!openProjects.contains(project)) {
            openProjects.add(project);
            if (activeProject == null) {
                activeProject = project;
            }
        }
    }

    public void unregisterProject(LibGdxProject project) {
        openProjects.remove(project);
        if (activeProject == project) {
            activeProject = openProjects.isEmpty() ? null : openProjects.get(0);
        }
    }

    public List<LibGdxProject> getOpenProjects() {
        return Collections.unmodifiableList(openProjects);
    }

    public LibGdxProject getActiveProject() {
        return activeProject;
    }

    public void setActiveProject(LibGdxProject project) {
        if (openProjects.contains(project)) {
            this.activeProject = project;
        }
    }

    public LibGdxProject findProjectByDirectory(File directory) {
        for (LibGdxProject p : openProjects) {
            if (p.getRootDirectory().equals(directory)) {
                return p;
            }
        }
        return null;
    }
}
