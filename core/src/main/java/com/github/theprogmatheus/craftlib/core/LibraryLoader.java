package com.github.theprogmatheus.craftlib.core;

import java.io.File;
import java.util.Collection;

public interface LibraryLoader<T> {

    public abstract void addLibraries(T target, Collection<File> libraryFiles);

    public abstract boolean loadLibraries() throws Exception;
}
