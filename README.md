# CraftLib

CraftLib is a lightweight and flexible runtime library loader for Bukkit plugins, with built-in support for Maven repositories. It provides powerful control, extensibility, and ease of use for plugin developers, simplifying dependency management.

-----

## ✨ Features

* Load external dependencies (JARs) at runtime.
* Support for custom Maven repositories (defaults to Maven Central).
* Simple and isolated configuration via a dedicated `craftlib` section in `plugin.yml`.
* Attempts to resolve dependencies for plugins designed with Paper's library system (though success isn't guaranteed).
* Future-proof with a custom configuration namespace (`craftlib`) for expansion.

-----

## 📦 `plugin.yml` Example

```yaml
name: "MyAwesomePlugin"
main: "com.example.myplugin.MyAwesomePlugin"
version: "1.0.0"
depend: ["CraftLib"] # Highly recommended to ensure CraftLib is ready

craftlib:
  libraries:
    - com.github.User:LibraryName:VERSION
    - org.apache.commons:commons-lang3:3.12.0
    - com.github.LMS5413:inventory-api:v1.0.10 # Example of a specific library
  repositories:
    - https://jitpack.io
    - https://repo.maven.apache.org/maven2
    - https://blablabla.com # Example of a custom repository
```

-----

## 🔧 How It Works

During plugin startup, CraftLib scans your `plugin.yml` for the `craftlib` section, looking for declared libraries and optional repositories.

CraftLib then **downloads the necessary JAR artifacts** from the specified repositories. Once downloaded, it performs a **shading process to create a single "fat JAR" named `libraries.jar`**. This `libraries.jar` is also treated as a plugin named `CraftLibs` by the server.

So, when everything is running, your server will effectively have two CraftLib-related plugins:

* **CraftLib**: This is the core plugin containing all the logic for dependency resolution, downloading, and shading.
* **CraftLibs** (`libraries.jar`): This is the shaded plugin containing all the external libraries your plugins declared.

This unique two-plugin approach ensures that your plugin's dependencies are loaded dynamically using a custom class loader, isolated from other plugins and the server itself. This means you don't need to bundle external JARs inside your plugin – CraftLib handles it for you, minimizing conflicts and increasing compatibility.

-----

## 💡 Use Cases

* Reduce plugin size by avoiding shading your own dependencies.
* Load internal or private libraries directly from sources like GitHub or JitPack.
* Experiment with modular or pluggable plugin architectures.

-----

## 🔨 How to Use

### 1\. Add CraftLib to your plugin

To use CraftLib, simply add it as a required plugin in your `plugin.yml`. This is **highly recommended** to ensure CraftLib is fully initialized and ready before your plugin attempts to use its features.

```yaml
depend: [CraftLib]
```

Your plugin will only load if CraftLib is present on the server. You don't need to bundle CraftLib in your own JAR – just include it on the server like any other plugin.

> 💡 Tip: You can download the latest version of CraftLib from [Releases](https://github.com/theprogmatheus/craftlib/releases) (or wherever your releases are hosted).

### 2\. Declare dependencies in `plugin.yml`

Use the dedicated `craftlib` section to declare what your plugin needs at runtime:

```yaml
name: "MyAwesomePlugin"
main: "com.example.myplugin.MyAwesomePlugin"
version: "1.0.0"
depend: ["CraftLib"]

craftlib:
  repositories:
    - https://jitpack.io
    - https://repo.maven.apache.org/maven2
  libraries:
    - com.github.User:LibraryName:VERSION
    - org.apache.commons:commons-lang3:3.12.0
    - com.github.LMS5413:inventory-api:v1.0.10
```

CraftLib will:

* Automatically download the JARs from the specified repositories.
* Load them in a classloader isolated from other plugins and the server itself, via the `CraftLibs` plugin.

### 3\. Use your libraries like magic 🪄

After loading, all classes from the declared libraries will be available in your plugin’s classpath as if they were bundled – but without increasing your JAR size or risking class conflicts.

You can now code using those libraries just like usual:

```java
import org.apache.commons.lang3.StringUtils;
import com.github.LMS5413.inventoryapi.InventoryAPI; // Example of using a declared library

public class MyAwesomePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Is empty? " + StringUtils.isEmpty("CraftLib rocks!"));
        InventoryAPI.init(this); // Example of initializing a declared library
    }
}
```

✅ Done\!
No shading, no fat JARs, no hassle. CraftLib handles the dependency resolution and class loading at runtime, so you can focus on coding your plugin.

-----

## 🚀 Future Plans

* Built-in cache system.
* Checksum verification.
* Logging and diagnostics.
* Runtime unloading and reloading support.

-----

## 📄 License

[MIT License](LICENSE).

-----

Made with ☕ and 💙 by [@TheProgMatheus](https://github.com/theprogmatheus)