package com.github.theprogmatheus.craftlib.core;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Maven repository where dependencies can be fetched from.
 * Common examples include Maven Central, JitPack, or any custom repository.
 * <p>
 * Each repository has a unique base URL that is used to resolve library paths.
 */
@RequiredArgsConstructor
@Data
public class LibraryRepository {

    /**
     * Human-readable name of the repository (used for logging, debugging, etc).
     * Example: "Maven Central", "JitPack"
     */
    private final String name;

    /**
     * The base URI of the Maven repository.
     * This URI should point to the root directory where group/artifact/version structure begins.
     * Example: https://repo.maven.apache.org/maven2/
     */
    private final URI uri;

    // Common repositories used for resolving dependencies
    public static final LibraryRepository MAVEN_CENTRAL = new LibraryRepository("Maven Central", URI.create("https://repo.maven.apache.org/maven2/"));
    public static final LibraryRepository JITPACK = new LibraryRepository("JitPack", URI.create("https://jitpack.io"));
    public static final LibraryRepository SONATYPE_OSS = new LibraryRepository("Sonatype OSS", URI.create("https://oss.sonatype.org/content/repositories/snapshots/"));
    public static final LibraryRepository CODE_MC = new LibraryRepository("CodeMC", URI.create("https://repo.codemc.io/repository/maven-public/"));
    public static final LibraryRepository SPIGOT_MC = new LibraryRepository("SpigotMC", URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
    public static final LibraryRepository PAPER_MC = new LibraryRepository("PaperMC", URI.create("https://repo.papermc.io/repository/maven-public/"));
    public static final LibraryRepository SONATYPE_RELEASES = new LibraryRepository("Sonatype Releases", URI.create("https://oss.sonatype.org/content/repositories/releases/"));

    /**
     * Default repository priority list.
     * These repositories will be used in the order listed to resolve libraries.
     */
    public static final List<LibraryRepository> ALL = Arrays.asList(
            MAVEN_CENTRAL,
            SONATYPE_RELEASES,
            SONATYPE_OSS,
            PAPER_MC,
            SPIGOT_MC,
            CODE_MC,
            JITPACK
    );

}
