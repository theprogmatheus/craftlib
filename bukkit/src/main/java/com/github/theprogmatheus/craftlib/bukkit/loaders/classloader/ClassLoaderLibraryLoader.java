package com.github.theprogmatheus.craftlib.bukkit.loaders.classloader;

import com.github.theprogmatheus.craftlib.bukkit.PluginFile;
import com.github.theprogmatheus.craftlib.bukkit.loaders.LibraryLoaderImpl;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
public class ClassLoaderLibraryLoader extends LibraryLoaderImpl {


    private static Method addURLMethod;

    static {
        try {
            addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
        } catch (Exception ignored) {
            addURLMethod = null;
        }
    }

    private final Logger logger;

    public ClassLoaderLibraryLoader(Plugin plugin) {
        super(plugin, new HashMap<>());
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean loadLibraries() throws Exception {
        for (Map.Entry<PluginFile, Collection<File>> entry : this.libraries.entrySet()) {
            PluginFile plugin = entry.getKey();
            Collection<File> librariesFiles = entry.getValue();

            URL[] urls = librariesFiles.stream().map(file -> {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(URL[]::new);

            injectLibraries(plugin, urls);
        }
        return true;
    }


    public void injectLibraries(PluginFile pluginFile, URL[] urls) throws Exception {

        Plugin plugin = pluginFile.getPlugin();
        if (plugin == null)
            return;

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (!(classLoader instanceof URLClassLoader))
            return;

        for (URL url : urls)
            addURLMethod.invoke(classLoader, url);
    }

    private void addURL(URLClassLoader classLoader, URL url) throws Exception {
        addURLMethod.invoke(classLoader, url);
    }

    public static boolean isAvailable() {
        return addURLMethod != null;
    }
}
