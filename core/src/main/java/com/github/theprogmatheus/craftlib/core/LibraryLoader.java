package com.github.theprogmatheus.craftlib.core;

import java.io.File;
import java.util.Collection;

public interface LibraryLoader {

    public abstract boolean loadLibraries(Collection<File> libraryFiles) throws Exception;
}
