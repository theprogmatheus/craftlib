package com.github.theprogmatheus.craftlib.core;


import com.github.theprogmatheus.craftlib.core.maven.MavenDependencyResolver;
import lombok.Data;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Resolves and downloads library dependencies into a local folder.
 * <p>
 * This class is responsible for finding the correct download URL
 * for a given dependency using the list of repositories and saving
 * the file under the plugin's data folder, typically in:
 * {dataFolder}/libraries/group/artifact/version/
 */
@Data
public abstract class LibraryResolver {

    private final Logger logger;
    private final File librariesFolder;
    private final Set<LibraryRepository> repositories;
    private final MavenDependencyResolver mavenDependencyResolver;

    public abstract Collection<File> resolve();

    /**
     * Constructs a new LibraryResolver.
     *
     * @param dataFolder   The plugin's base data folder.
     * @param repositories A list of repositories to search in order.
     */
    public LibraryResolver(Logger logger, File dataFolder, Set<LibraryRepository> repositories) {
        this.logger = logger;
        this.librariesFolder = new File(dataFolder, "libraries");
        this.repositories = repositories;

        List<RemoteRepository> mavenRepositories = this.repositories.stream()
                .map(LibraryRepository::toMavenRepository)
                .collect(Collectors.toList());
        mavenRepositories.addAll(LibraryRepository.ALL.stream().map(LibraryRepository::toMavenRepository).collect(Collectors.toList()));

        this.mavenDependencyResolver = new MavenDependencyResolver(this.librariesFolder, mavenRepositories);
    }


    /**
     * Resolves and downloads the provided dependencies, if they are not already cached locally.
     *
     * @param dependencies The list of dependencies to resolve and download.
     * @return The list of files pointing to the downloaded jars.
     * @throws IOException If any dependency could not be downloaded.
     */
    public Collection<File> resolveMaven(Collection<LibraryDependency> dependencies) {
        if (dependencies == null) {
            logger.warning("No dependencies provided to resolve (null list). Returning empty list.");
            return new ArrayList<>();
        }

        logger.info("Starting to resolve " + dependencies.size() + " dependencies...");

        Set<File> resolvedFiles = new HashSet<>();
        dependencies.forEach(dependency -> {
            try {
                logger.fine("Resolving dependency: " + dependency);
                List<File> files = resolveMaven(dependency);
                resolvedFiles.addAll(files);
                logger.fine("Successfully resolved dependency: " + dependency);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to resolve dependency: " + dependency, e);
                throw new RuntimeException("Dependency resolution failed for " + dependency, e);
            }
        });

        logger.info("Finished resolving all dependencies.");
        return resolvedFiles;
    }

    /**
     * Resolves and downloads the provided dependencies, if they are not already cached locally.
     *
     * @param dependencies The list of dependencies to resolve and download.
     * @return The list of files pointing to the downloaded jars.
     * @throws IOException If any dependency could not be downloaded.
     */
    public List<File> resolve(Collection<LibraryDependency> dependencies) {
        if (dependencies == null) {
            logger.warning("No dependencies provided to resolve (null list). Returning empty list.");
            return new ArrayList<>();
        }

        logger.info("Starting to resolve " + dependencies.size() + " dependencies...");

        List<File> resolvedFiles = dependencies.stream().map(dep -> {
            try {
                logger.fine("Resolving dependency: " + dep);
                File file = resolve(dep);
                logger.fine("Successfully resolved dependency: " + file.getAbsolutePath());
                return file;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to resolve dependency: " + dep, e);
                throw new RuntimeException("Dependency resolution failed for " + dep, e);
            }
        }).collect(Collectors.toList());

        logger.info("Finished resolving all dependencies.");

        return resolvedFiles;
    }

    public List<File> resolveMaven(LibraryDependency dependency) throws IOException {
        String coords = dependency.toCoordinates();
        try {
            List<File> resolvedFiles = mavenDependencyResolver.resolveDependencies(coords);
            if (resolvedFiles.isEmpty())
                throw new IOException("No artifacts found for: " + coords);

            logger.info("Resolved " + resolvedFiles.size() + " artifacts for: " + coords);
            return resolvedFiles;
        } catch (DependencyResolutionException e) {
            throw new IOException("Failed to resolve dependency: " + coords, e);
        }
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

        if (!dependency.isSnapshot() && outputFile.exists()) {
            logger.info("Dependency already cached locally: " + outputFile.getAbsolutePath());
            return outputFile;
        }

        logger.fine("Dependency not cached, preparing to download: " + dependency);
        outputFile.getParentFile().mkdirs();

        IOException lastError = null;
        for (LibraryRepository repository : repositories) {
            try {
                URL url = dependency.getDownloadURI(repository).toURL();
                logger.fine(String.format("Trying to download from %s", url));
                download(url, outputFile);
                logger.info(String.format("Downloaded dependency successfully: %s", url));
                return outputFile;
            } catch (IOException e) {
                lastError = e;
            }
        }

        if (outputFile.exists())
            return outputFile;

        logger.log(Level.SEVERE, "Failed to resolve dependency after checking all repositories: " + dependency, lastError);
        throw new IOException("Failed to resolve dependency: " + dependency, lastError);
    }

    private void download(URL url, File destination) throws IOException {
        logger.fine("Opening connection to URL: " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "CraftLib Resolver");
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            logger.log(Level.FINE, "Failed to download " + url + ": HTTP " + connection.getResponseCode());
            throw new IOException("Failed to download " + url + ": HTTP " + connection.getResponseCode());
        }

        logger.fine("Starting download to file: " + destination.getAbsolutePath());

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        logger.fine("Download finished for file: " + destination.getAbsolutePath());
    }
}