package com.github.theprogmatheus.craftlib.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onLoad() {
        new PluginLibraryTracker(this).run();
    }
}
