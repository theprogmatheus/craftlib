package com.github.theprogmatheus.craftlib.bukkit.loaders.shade;

import com.github.theprogmatheus.craftlib.bukkit.loaders.LibraryLoaderImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ShadeLibraryLoader extends LibraryLoaderImpl {

    public ShadeLibraryLoader(Plugin plugin) {
        super(plugin, new HashMap<>());
    }

    @Override
    public boolean loadLibraries() throws Exception {
        Set<File> libraryFiles = new HashSet<>();
        this.libraries.values().forEach(libraryFiles::addAll);

        if (libraryFiles.isEmpty())
            return false;

        plugin.getLogger().info("Trying to load a shaded dependency file");

        PluginLibraryShader libraryShader = new PluginLibraryShader(this.plugin, new File(plugin.getDataFolder(), "libraries.jar"), libraryFiles);
        File shadedJar = libraryShader.shade();
        Plugin shadedPlugin = Bukkit.getPluginManager().loadPlugin(shadedJar);

        plugin.getLogger().info(String.format("Shaded dependency file loaded successfully [%s].", shadedPlugin.getName()));
        return shadedPlugin.isEnabled();
    }
}
