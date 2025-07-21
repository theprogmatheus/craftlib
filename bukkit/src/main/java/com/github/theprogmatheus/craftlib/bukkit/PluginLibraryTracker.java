package com.github.theprogmatheus.craftlib.bukkit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PluginLibraryTracker implements Runnable {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final JavaPlugin plugin;

    @Override
    @SneakyThrows
    public void run() {
        if (!(PLUGINS_FOLDER.exists() && PLUGINS_FOLDER.isDirectory()))
            return;

        File[] files = PLUGINS_FOLDER.listFiles();
        if (files == null)
            return;

        List<File> filesToShade = new ArrayList<>();

        for (File file : files) {
            if (!isValidJarFile(file))
                continue;

            PluginFile pluginFile = new PluginFile(file);
            if (!pluginFile.isValidPlugin())
                continue;

            filesToShade.addAll(new PluginLibraryResolver(this.plugin, pluginFile).resolve());
        }

        PluginLibraryShader libraryShader = new PluginLibraryShader(new File(this.plugin.getDataFolder(), "craftlib-shade.jar"), filesToShade);
        Bukkit.getPluginManager().loadPlugin(libraryShader.createShadedPluginJar());
    }

    private boolean isValidJarFile(File file) {
        if (file == null)
            return false;

        if (file.isDirectory())
            return false;

        String fileName = file.getName();
        return fileName.endsWith(".jar");
    }

}
