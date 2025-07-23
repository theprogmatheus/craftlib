package com.github.theprogmatheus.craftlib.bukkit.loaders;

import com.github.theprogmatheus.craftlib.core.LibraryLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public abstract class LibraryLoaderImpl implements LibraryLoader {

    protected final Plugin plugin;


}
