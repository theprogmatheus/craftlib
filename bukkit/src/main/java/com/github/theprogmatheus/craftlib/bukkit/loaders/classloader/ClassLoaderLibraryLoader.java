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
                    URL url = file.toURI().toURL();
                    logger.fine(String.format("[%s] Preparing to inject: %s", plugin.getPluginName(), file.getName()));
                    return url;
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(URL[]::new);

            injectLibraries(plugin, urls);
        }
        return true;
    }


    public void injectLibraries(PluginFile pluginFile, URL[] urls) throws Exception {
        String pluginName = pluginFile.getPluginName();
        Plugin plugin = pluginFile.getPlugin();

        if (plugin == null) {
            logger.warning(String.format("[%s] Plugin reference is null.", pluginName));
            return;
        }

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (!(classLoader instanceof URLClassLoader)) {
            logger.warning(String.format("[%s] ClassLoader is not a URLClassLoader.", pluginName));
            return;
        }

        for (URL url : urls) {
            logger.info(String.format("[%s] Injecting library into classloader: %s", pluginName, new File(url.toURI()).getName()));
            addURLMethod.invoke(classLoader, url);
        }
    }

    public static boolean isAvailable() {
        return addURLMethod != null;
    }
}
