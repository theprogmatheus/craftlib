package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.LibraryDependency;
import com.github.theprogmatheus.craftlib.core.LibraryResolver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Getter
public class PluginLibraryResolver extends LibraryResolver {

    private final Plugin plugin;
    private final PluginFile pluginFile;

    public PluginLibraryResolver(Plugin plugin, PluginFile pluginFile) {
        super(pluginFile.getPluginName(), plugin.getLogger(), plugin.getDataFolder(), pluginFile.getRepositories());
        this.plugin = plugin;
        this.pluginFile = pluginFile;
    }


    @Override
    public Collection<File> resolve() {
        Set<LibraryDependency> dependencies = this.pluginFile.getDependencies();
        if (dependencies.isEmpty())
            return new ArrayList<>();
        return super.resolveMaven(dependencies);
    }
}
