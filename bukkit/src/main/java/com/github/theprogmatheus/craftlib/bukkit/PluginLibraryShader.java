package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.utils.FileUtils;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
public class PluginLibraryShader {

    private static final String MAIN_CLASS_NAME = "com.github.theprogmatheus.craftlib.bukkit.shade.Main";
    private static final String PLUGIN_YML = "plugin.yml";


    private final Plugin plugin;
    private final File shadeJar;
    private final Collection<File> files;
    private final String shadeHash;


    public PluginLibraryShader(Plugin plugin, File shadeJar, Collection<File> files) throws Exception {
        this.plugin = plugin;
        this.shadeJar = shadeJar;
        this.files = files;
        this.shadeHash = FileUtils.hashFiles(files);
    }


    public File shade() throws Exception {
        if (alreadyExists(true))
            return this.shadeJar;

        Set<String> addedEntries = new HashSet<>();

        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(shadeJar), createManifest())) {

            byte[] buffer = new byte[8192];

            for (File file : files) {
                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        String entryName = entry.getName();

                        if (entry.isDirectory() || entryName.startsWith("META-INF"))
                            continue;

                        if (addedEntries.contains(entryName))
                            continue;

                        jarOutputStream.putNextEntry(new JarEntry(entry.getName()));
                        addedEntries.add(entryName);

                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            jarOutputStream.write(buffer, 0, bytesRead);
                        }

                        jarOutputStream.closeEntry();
                    }
                }
            }

            addDummyMain(jarOutputStream);
            addPluginYml(jarOutputStream);
        }
        return shadeJar;
    }

    public boolean alreadyExists(boolean checkHash) {
        if (!checkHash)
            return this.shadeJar.exists();

        if (PluginFile.isValidJarFile(this.shadeJar)) {
            PluginFile pluginFile = new PluginFile(this.shadeJar);
            if (pluginFile.isValidPlugin()) {
                String shadeHash = pluginFile.getPluginYaml().getString("shade-hash");
                return (shadeHash != null && !shadeHash.trim().isEmpty()) && (this.shadeHash.equals(shadeHash));
            }
        }
        return false;
    }

    private Manifest createManifest() {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, MAIN_CLASS_NAME);
        return manifest;
    }

    private void addDummyMain(JarOutputStream jos) throws Exception {
        String path = MAIN_CLASS_NAME.replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null)
                throw new IOException("Main dummy class not found in resources: " + path);
            jos.putNextEntry(new JarEntry(path));
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                jos.write(buffer, 0, bytesRead);
            }
            jos.closeEntry();
        }
    }

    private void addPluginYml(JarOutputStream jarOutputStream) throws Exception {
        InputStream inputStream = this.plugin.getResource(PLUGIN_YML);
        if (inputStream == null) throw new FileNotFoundException("plugin.yml não encontrado!");

        YamlConfiguration pluginYaml = new YamlConfiguration();
        pluginYaml.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        pluginYaml.set("main", MAIN_CLASS_NAME);
        pluginYaml.set("name", "CraftLibs");
        pluginYaml.set("description", "A shade plugin with all the necessary dependencies");
        pluginYaml.set("shade-hash", this.shadeHash);

        jarOutputStream.putNextEntry(new JarEntry(PLUGIN_YML));
        jarOutputStream.write(pluginYaml.saveToString().getBytes());
        jarOutputStream.closeEntry();
    }
}