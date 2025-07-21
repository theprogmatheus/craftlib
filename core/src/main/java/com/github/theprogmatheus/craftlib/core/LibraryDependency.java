package com.github.theprogmatheus.craftlib.core;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;

/**
 * Represents a Maven-style library dependency using groupId, artifactId, and version coordinates.
 * <p>
 * Can be used to resolve external dependencies dynamically at runtime or during plugin load.
 */
@RequiredArgsConstructor
@Data
public class LibraryDependency {

    /**
     * The group ID of the dependency.
     * Example: "com.google.guava"
     */
    private final String groupId;

    /**
     * The artifact ID of the dependency.
     * Example: "guava"
     */
    private final String artifactId;

    /**
     * The version of the dependency.
     * Example: "31.1-jre"
     */
    private final String version;

    /**
     * Returns the full Maven coordinates of the dependency.
     * Format: "groupId:artifactId:version"
     *
     * @return A string representing this dependency's coordinates.
     */
    public String toCoordinates() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }


    /**
     * Builds the full URI for downloading the main JAR of the dependency from a given repository.
     *
     * @param repository the Maven repository where the dependency is hosted
     * @return the full URI to the .jar file
     */
    public URI getDownloadURI(LibraryRepository repository) {
        return buildURI(repository, ".jar");
    }

    /**
     * Builds the full URI for downloading the POM file of the dependency from a given repository.
     * The POM file contains metadata like dependencies, developers, license, etc.
     *
     * @param repository the Maven repository where the dependency is hosted
     * @return the full URI to the .pom file
     */
    public URI getPomDownloadURI(LibraryRepository repository) {
        return buildURI(repository, ".pom");
    }

    /**
     * Builds the full URI for downloading the sources JAR of the dependency from a given repository.
     * This artifact contains the original Java source code and is useful for development and debugging.
     *
     * @param repository the Maven repository where the dependency is hosted
     * @return the full URI to the -sources.jar file
     */
    public URI getSourcesDownloadURI(LibraryRepository repository) {
        return buildURI(repository, "-sources.jar");
    }

    /**
     * Helper method to construct the correct download URI with a given suffix.
     *
     * @param repository the repository base
     * @param suffix     the file extension or suffix (e.g., ".jar", ".pom", "-sources.jar")
     * @return the constructed URI
     */
    private URI buildURI(LibraryRepository repository, String suffix) {
        String basePath = String.format("%s/%s/%s/%s-%s%s",
                groupId.replace('.', '/'),
                artifactId,
                version,
                artifactId,
                version,
                suffix);

        return repository.getUri().resolve(basePath);
    }


    /**
     * Returns the name of the dependency's JAR file.
     * Example: commons-lang3-3.12.0.jar
     *
     * @return the JAR filename
     */
    public String getJarFileName() {
        return artifactId + "-" + version + ".jar";
    }

    /**
     * Returns the name of the dependency's POM file.
     * Example: commons-lang3-3.12.0.pom
     *
     * @return the POM filename
     */
    public String getPomFileName() {
        return artifactId + "-" + version + ".pom";
    }

    /**
     * Returns the relative Maven path to the dependency (used to resolve URL or local path).
     * Example: org/apache/commons/commons-lang3/3.12.0/
     *
     * @return relative path
     */
    public String getRelativePath() {
        return groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/";
    }

    /**
     * Returns the complete Maven path to the JAR.
     * Example: org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar
     *
     * @return JAR relative path
     */
    public String getJarPath() {
        return getRelativePath() + getJarFileName();
    }

    /**
     * Returns the complete Maven path to the POM.
     * Example: org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.pom
     *
     * @return POM relative path
     */
    public String getPomPath() {
        return getRelativePath() + getPomFileName();
    }
}
