package com.github.theprogmatheus.craftlib.core;

import com.github.theprogmatheus.craftlib.core.maven.SnapshotMetadataResolver;
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
     * Creates a LibraryDependency from a Maven coordinate string.
     *
     * @param coordinates the Maven coordinates string in the format "groupId:artifactId:version"
     * @return a new LibraryDependency instance
     * @throws IllegalArgumentException if the coordinates string is invalid
     */
    public static LibraryDependency fromCoordinates(String coordinates) {
        if (coordinates == null || coordinates.trim().isEmpty()) {
            throw new IllegalArgumentException("Coordinates string cannot be null or empty");
        }

        String[] parts = coordinates.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Coordinates must be in the format 'groupId:artifactId:version'");
        }

        return new LibraryDependency(parts[0], parts[1], parts[2]);
    }

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
     * Checks if the version of this dependence is a snapshot
     *
     * @return if it is a snapshot version
     */
    public boolean isSnapshot() {
        return this.version.toUpperCase().endsWith("-SNAPSHOT");
    }

    /**
     * Helper method to construct the correct download URI with a given suffix.
     *
     * @param repository the repository base
     * @param suffix     the file extension or suffix (e.g., ".jar", ".pom", "-sources.jar")
     * @return the constructed URI
     */
    private URI buildURI(LibraryRepository repository, String suffix) {

        if (isSnapshot()) {
            String snapshotVersion = SnapshotMetadataResolver.resolveSnapshotVersion(this, repository);
            if (snapshotVersion != null)
                return repository.getUri().resolve(getArtifactPath(snapshotVersion, suffix));
        }
        return repository.getUri().resolve(getArtifactPath(this.version, suffix));
    }

    /**
     * Constructs the relative path to the artifact file inside a Maven repository.
     * <p>
     * This path includes the group ID (as folder structure), artifact ID, version, and file name,
     * and it is used to resolve or download the artifact from a Maven-compatible repository.
     * </p>
     *
     * @param version The version of the artifact. Typically something like "1.0.0" or "1.0.0-SNAPSHOT".
     * @param suffix  The file suffix, such as ".jar", ".pom", "-javadoc.jar", etc.
     * @return The relative path to the artifact file inside the repository.
     */
    public String getArtifactPath(String version, String suffix) {
        String groupPath = groupId.replace('.', '/');
        return String.format(
                "%s/%s/%s/%s-%s%s",
                groupPath,
                artifactId,
                this.version,
                artifactId,
                version,
                suffix
        );
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
