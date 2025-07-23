package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.LibraryLoader;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.github.theprogmatheus.craftlib.bukkit.PluginFile.isValidJarFile;

@RequiredArgsConstructor
public class PluginLibraryTracker implements Runnable {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final JavaPlugin plugin;
    private final LibraryLoader loader;

    @Override
    public void run() {
        if (!(PLUGINS_FOLDER.exists() && PLUGINS_FOLDER.isDirectory()))
            return;

        File[] files = PLUGINS_FOLDER.listFiles();
        if (files == null)
            return;

        Set<File> libraryFiles = new HashSet<>();

        for (File file : files) {
            if (!isValidJarFile(file))
                continue;

            PluginFile pluginFile = new PluginFile(file);
            if (!pluginFile.isValidPlugin())
                continue;
            libraryFiles.addAll(new PluginLibraryResolver(this.plugin, pluginFile).resolve());
        }

        try {
            loader.loadLibraries(libraryFiles);
        } catch (Exception e) {
            throw new RuntimeException("Could not load library files", e);
        }
    }

}
