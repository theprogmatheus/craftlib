package com.github.theprogmatheus.craftlib.core.maven;

import com.github.theprogmatheus.craftlib.core.LibraryDependency;
import com.github.theprogmatheus.craftlib.core.LibraryRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SnapshotMetadataResolver {

    /**
     * Tries to resolve the actual snapshot version (with timestamp and build number)
     * by downloading and parsing the `maven-metadata.xml` file from the repository.
     *
     * @param dependency The library dependency
     * @param repository The Maven repository
     * @return The resolved version (e.g. "1.0.0-20250715.154406-1"), or null if failed
     */
    public static String resolveSnapshotVersion(LibraryDependency dependency, LibraryRepository repository) {
        try {
            String metadataPath = dependency.getRelativePath() + "maven-metadata.xml";
            URL metadataUrl = repository.getUri().resolve(metadataPath).toURL();

            HttpURLConnection connection = (HttpURLConnection) metadataUrl.openConnection();
            connection.setRequestProperty("User-Agent", "CraftLib Resolver");
            connection.connect();

            int code = connection.getResponseCode();
            if (code >= 400 && code < 600)
                return null;

            try (InputStream stream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(stream);

                NodeList snapshotVersions = doc.getElementsByTagName("snapshotVersion");
                for (int i = 0; i < snapshotVersions.getLength(); i++) {
                    Element versionElem = (Element) snapshotVersions.item(i);
                    String extension = getChildText(versionElem, "extension");
                    String classifier = getChildText(versionElem, "classifier");

                    if ("jar".equals(extension) && (classifier == null || classifier.isEmpty())) {
                        return getChildText(versionElem, "value");
                    }
                }
            }

        } catch (Exception ignored) {
        }
        return null;
    }

    private static String getChildText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
    }
}