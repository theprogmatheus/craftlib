package com.github.theprogmatheus.craftlib.core;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Resolves and downloads library dependencies into a local folder.
 *
 * This class is responsible for finding the correct download URL
 * for a given dependency using the list of repositories and saving
 * the file under the plugin's data folder, typically in:
 * {dataFolder}/libraries/group/artifact/version/
 */
public class LibraryResolver {

    private final File librariesFolder;
    private final List<LibraryRepository> repositories;

    /**
     * Constructs a new LibraryResolver.
     *
     * @param dataFolder   The plugin's base data folder.
     * @param repositories A list of repositories to search in order.
     */
    public LibraryResolver(File dataFolder, List<LibraryRepository> repositories) {
        this.librariesFolder = new File(dataFolder, "libraries");
        this.repositories = repositories;
    }

    /**
     * Resolves and downloads the given dependency if it's not already cached locally.
     *
     * @param dependency The dependency to resolve and download.
     * @return The file pointing to the downloaded jar.
     * @throws IOException If the dependency could not be downloaded.
     */
    public File resolve(LibraryDependency dependency) throws IOException {
        File outputFile = new File(
                librariesFolder,
                dependency.getGroupId().replace('.', File.separatorChar)
                        + File.separator + dependency.getArtifactId()
                        + File.separator + dependency.getVersion()
                        + File.separator + dependency.getJarFileName()
        );

        if (outputFile.exists()) {
            return outputFile;
        }

        outputFile.getParentFile().mkdirs();

        IOException lastError = null;
        for (LibraryRepository repository : repositories) {
            try {
                URL url = dependency.getDownloadURI(repository).toURL();
                download(url, outputFile);
                return outputFile;
            } catch (IOException e) {
                lastError = e; // Try the next repo
            }
        }

        throw new IOException("Failed to resolve dependency: " + dependency, lastError);
    }

    private void download(URL url, File destination) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "CraftLib Resolver");
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download " + url + ": HTTP " + connection.getResponseCode());
        }

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}