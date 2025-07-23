package com.github.theprogmatheus.craftlib.bukkit.loaders.shade;

import com.github.theprogmatheus.craftlib.bukkit.loaders.LibraryLoaderImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;

public class ShadeLibraryLoader extends LibraryLoaderImpl {

    public ShadeLibraryLoader(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean loadLibraries(Collection<File> libraryFiles) throws Exception {
        plugin.getLogger().info("Trying to load a shaded dependency file");
        PluginLibraryShader libraryShader = new PluginLibraryShader(this.plugin, new File(plugin.getDataFolder(), "libraries.jar"), libraryFiles);
        File shadedJar = libraryShader.shade();
        Plugin shadedPlugin = Bukkit.getPluginManager().loadPlugin(shadedJar);
        plugin.getLogger().info(String.format("Shaded dependency file loaded successfully [%s].", shadedPlugin.getName()));
        return shadedPlugin.isEnabled();
    }
}
