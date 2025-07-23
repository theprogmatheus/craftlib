package com.github.theprogmatheus.craftlib.bukkit.loaders;

import com.github.theprogmatheus.craftlib.bukkit.PluginFile;
import com.github.theprogmatheus.craftlib.core.LibraryLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
public abstract class LibraryLoaderImpl implements LibraryLoader<PluginFile> {

    protected final Plugin plugin;
    protected final HashMap<PluginFile, Collection<File>> libraries;

    @Override
    public void addLibraries(PluginFile target, Collection<File> libraryFiles) {
        this.libraries.put(target, libraryFiles);
    }
}
