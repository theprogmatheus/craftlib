package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.bukkit.loaders.classloader.ClassLoaderLibraryLoader;
import com.github.theprogmatheus.craftlib.bukkit.loaders.shade.ShadeLibraryLoader;
import com.github.theprogmatheus.craftlib.core.LibraryLoader;
import com.github.theprogmatheus.util.JGRUChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private JGRUChecker updateChecker;


    @Override
    public void onLoad() {
        checkLibraries();
    }

    @Override
    public void onEnable() {
        checkNewUpdates();
    }

    private void checkLibraries() {
        LibraryLoader<PluginFile> libraryLoader;

        if (ClassLoaderLibraryLoader.isAvailable())
            libraryLoader = new ClassLoaderLibraryLoader(this);
        else {
            libraryLoader = new ShadeLibraryLoader(this);

            getLogger().warning("------------------------------------------------------------");
            getLogger().warning("Your Java version has restricted access to the ClassLoader.addURL method.");
            getLogger().warning("CraftLib will fallback to the shaded plugin loader.");
            getLogger().warning("This means all libraries will be merged into the plugin jar.");
            getLogger().warning("");
            getLogger().warning("[!] WARNING: This fallback is less stable and may cause dependency conflicts.");
            getLogger().warning("CraftLib cannot isolate dependencies and will use the first version it finds.");
            getLogger().warning("");
            getLogger().warning("[!] For best compatibility, start your server with the following JVM flag:");
            getLogger().warning("   --add-opens java.base/java.net=ALL-UNNAMED");
            getLogger().warning("This will allow CraftLib to dynamically inject libraries at runtime.");
            getLogger().warning("------------------------------------------------------------");
        }
        new PluginLibraryTracker(this, libraryLoader).run();
    }

    private void checkNewUpdates() {
        this.updateChecker = new JGRUChecker("theprogmatheus", "craftlib", getDescription().getVersion());
        this.updateChecker.checkAsync().thenAcceptAsync(release -> {
            if (release == null || this.updateChecker.getCurrentVersion().equals(release.getVersion())) return;

            Bukkit.getScheduler().runTask(this, () -> {
                Logger log = getLogger();

                // ANSI escape code
                String ANSI_RESET = "\u001B[0m";
                String ANSI_YELLOW = "\u001B[93m";
                String ANSI_CYAN = "\u001B[36m";
                String ANSI_WHITE = "\u001B[37m";
                String ANSI_GOLD = "\u001B[33m";


                log.info(" ");
                log.info(ANSI_YELLOW + "===============================================" + ANSI_RESET);
                log.info(ANSI_GOLD + "[!] A new update is available! [!]" + ANSI_RESET);
                log.info(ANSI_YELLOW + "===============================================" + ANSI_RESET);
                log.info(ANSI_CYAN + " * Plugin: " + ANSI_WHITE + release.getName() + ANSI_RESET);
                log.info(ANSI_CYAN + " * Current Version: " + ANSI_WHITE + this.updateChecker.getCurrentVersion() + ANSI_RESET);
                log.info(ANSI_CYAN + " * New Version: " + ANSI_WHITE + release.getVersion() + ANSI_RESET);
                log.info(ANSI_CYAN + " * Download here: " + ANSI_WHITE + release.getDownloadPage() + ANSI_RESET);
                log.info(ANSI_YELLOW + "===============================================" + ANSI_RESET);
                log.info(ANSI_GOLD + "[!] Upgrade now to enjoy the latest features, improvements, and bug fixes!" + ANSI_RESET);
                log.info(ANSI_GOLD + "[!] Staying updated ensures better performance and security." + ANSI_RESET);
                log.info(ANSI_YELLOW + "===============================================" + ANSI_RESET);
                log.info(" ");
            });
        });
    }

}
