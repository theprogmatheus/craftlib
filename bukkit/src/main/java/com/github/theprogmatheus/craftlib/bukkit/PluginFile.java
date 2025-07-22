package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.LibraryDependency;
import com.github.theprogmatheus.craftlib.core.LibraryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Data
@RequiredArgsConstructor
public class PluginFile {

    private final File file;
    private YamlConfiguration pluginYaml;
    private String pluginName;
    private Set<LibraryRepository> repositories;
    private Set<LibraryDependency> dependencies;

    public String getPluginName() {
        if (this.pluginName != null)
            return this.pluginName;

        YamlConfiguration yaml = getPluginYaml();
        if (yaml != null)
            return this.pluginName = yaml.getString("name");
        return this.pluginName;
    }

    public boolean isValidPlugin() {
        String pluginName = getPluginName();
        return pluginName != null && !pluginName.trim().isEmpty();
    }

    public Plugin getPlugin() {
        if (!isValidPlugin())
            return null;
        return Bukkit.getPluginManager().getPlugin(getPluginName());
    }

    public Set<LibraryRepository> getRepositories() {
        if (this.repositories != null)
            return this.repositories;

        YamlConfiguration yaml = getPluginYaml();

        if (yaml == null)
            return new HashSet<>();

        List<String> repositories = yaml.getStringList("craftlib.repositories");
        if (repositories == null)
            return new HashSet<>();

        Set<LibraryRepository> list = new HashSet<>();

        repositories.forEach(repositoryUrl -> {
            try {
                URI uri = URI.create(repositoryUrl);
                String host = uri.getHost();
                String name = (host == null || host.trim().isEmpty()) ? repositoryUrl : host;
                list.add(new LibraryRepository(name, uri));
            } catch (Exception ignored) {
            }
        });
        return this.repositories = list;
    }

    public Set<LibraryDependency> getDependencies() {
        if (this.dependencies != null)
            return this.dependencies;

        YamlConfiguration yaml = getPluginYaml();

        if (yaml == null)
            return new HashSet<>();

        List<String> libraries = yaml.getStringList("craftlib.libraries");
        if (libraries == null)
            libraries = yaml.getStringList("libraries");

        if (libraries == null)
            return new HashSet<>();

        Set<LibraryDependency> list = new HashSet<>();

        libraries.forEach(dependencyUrl -> {
            try {
                list.add(LibraryDependency.fromCoordinates(dependencyUrl));
            } catch (Exception ignored) {
            }
        });
        return this.dependencies = list;
    }

    public YamlConfiguration getPluginYaml() {
        if (this.pluginYaml != null)
            return this.pluginYaml;

        try (JarFile jar = new JarFile(this.file)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if (entry == null)
                return null;

            try (InputStream input = jar.getInputStream(entry)) {
                this.pluginYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        return this.pluginYaml;
    }


    public static boolean isValidJarFile(File file) {
        if (file == null || !file.exists())
            return false;

        if (file.isDirectory())
            return false;

        String fileName = file.getName();
        return fileName.endsWith(".jar");
    }
}
