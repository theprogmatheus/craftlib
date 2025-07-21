package com.github.theprogmatheus.craftlib.bukkit;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequiredArgsConstructor
public class PluginLibraryShader {

    private final File shadeJar;
    private final Collection<File> files;


    private static final String MAIN_CLASS_NAME = "com.github.theprogmatheus.craftlib.bukkit.shade.Main";
    private static final String PLUGIN_YML = "plugin.yml";

    public File createShadedPluginJar() throws IOException {
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

    private Manifest createManifest() {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, MAIN_CLASS_NAME);
        return manifest;
    }

    private void addDummyMain(JarOutputStream jos) throws IOException {
        String path = MAIN_CLASS_NAME.replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null)
                throw new IOException("Main dummy class not found in resources: " + path);
            jos.putNextEntry(new JarEntry(path));
            is.transferTo(jos);
            jos.closeEntry();
        }
    }

    private void addPluginYml(JarOutputStream jarOutputStream) throws IOException {
        jarOutputStream.putNextEntry(new JarEntry(PLUGIN_YML));
        jarOutputStream.write(("""
                load: STARTUP
                depend: ["craftlib"]
                name: craftlib-shade
                main: %s
                version: 1.0.0
                api-version: 1.8
                """.formatted(MAIN_CLASS_NAME)).getBytes());
        jarOutputStream.closeEntry();
    }

}