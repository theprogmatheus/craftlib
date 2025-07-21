package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.LibraryDependency;
import com.github.theprogmatheus.craftlib.core.LibraryRepository;
import com.github.theprogmatheus.craftlib.core.LibraryResolver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

@Getter
public class PluginLibraryResolver extends LibraryResolver {

    private final Plugin plugin;
    private final PluginFile pluginFile;
    private final URLClassLoader classLoader;

    public PluginLibraryResolver(Plugin plugin, PluginFile pluginFile) {
        super(plugin.getLogger(), plugin.getDataFolder(), pluginFile.getRepositories());
        this.plugin = plugin;
        this.pluginFile = pluginFile;
        this.classLoader = (URLClassLoader) pluginFile.getPlugin().getClass().getClassLoader();

        // set default repositories
        getRepositories().addAll(LibraryRepository.ALL);
    }


    @Override
    public List<File> resolve() {
        Set<LibraryDependency> dependencies = this.pluginFile.getDependencies();
        if (dependencies.isEmpty())
            return List.of();

        return super.resolve(dependencies);
    }
}
