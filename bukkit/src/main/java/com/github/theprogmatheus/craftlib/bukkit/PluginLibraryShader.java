package com.github.theprogmatheus.craftlib.bukkit;

import com.github.theprogmatheus.craftlib.core.utils.FileUtils;
import lombok.Getter;

import java.io.*;
import java.util.Collection;
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


    private final File shadeJar;
    private final Collection<File> files;
    private final String shadeHash;


    public PluginLibraryShader(File shadeJar, Collection<File> files) throws Exception {
        this.shadeJar = shadeJar;
        this.files = files;
        this.shadeHash = FileUtils.hashFiles(files);
    }


    public File shade() throws Exception {
        if (alreadyExists(true))
            return this.shadeJar;

        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(shadeJar), createManifest())) {

            for (File file : files) {
                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.isDirectory() || entry.getName().startsWith("META-INF"))
                            continue;
                        jarOutputStream.putNextEntry(new JarEntry(entry.getName()));
                        zis.transferTo(jarOutputStream);
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
                return (shadeHash != null && !shadeHash.isBlank()) && (this.shadeHash.equals(shadeHash));
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
            is.transferTo(jos);
            jos.closeEntry();
        }
    }

    private void addPluginYml(JarOutputStream jarOutputStream) throws Exception {
        jarOutputStream.putNextEntry(new JarEntry(PLUGIN_YML));
        jarOutputStream.write(("""
                load: STARTUP
                depend: ["CraftLib"]
                name: CraftLibs
                main: %s
                version: 1.0.0
                api-version: 1.8
                shade-hash: %s
                """.formatted(MAIN_CLASS_NAME, this.shadeHash)).getBytes());
        jarOutputStream.closeEntry();
    }

}