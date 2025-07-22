package com.github.theprogmatheus.craftlib.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.github.theprogmatheus.craftlib.bukkit.PluginFile.isValidJarFile;

@RequiredArgsConstructor
public class PluginLibraryTracker implements Runnable {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final JavaPlugin plugin;

    @Override
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
        try {
            plugin.getLogger().info("Trying to load a shaded dependency file");
            var libraryShader = new PluginLibraryShader(new File(plugin.getDataFolder(), "libraries.jar"), filesToShade);
            Plugin shadedPlugin = Bukkit.getPluginManager().loadPlugin(libraryShader.shade());
            plugin.getLogger().info("Shaded dependency file loaded successfully [%s].".formatted(shadedPlugin.getName()));
        } catch (Exception e) {
            plugin.getLogger().severe("Unable to create a shaded dependency file: " + e.getMessage());
        }

    }

}
