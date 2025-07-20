# CraftLib

CraftLib is a lightweight and flexible runtime library loader for Bukkit plugins, with built-in support for Maven repositories.  
It is inspired by Paper’s `libraries` system, but provides more control, extensibility, and ease of use for plugin developers.

## ✨ Features

- Load external dependencies (JARs) at runtime.
- Support for custom Maven repositories (defaults to Maven Central).
- Simple configuration via `plugin.yml`.
- Fully compatible with Paper’s `libraries` entry.
- Isolation to avoid conflicts with Paper’s own dependency system.
- Future-proof with a custom configuration namespace (`craftlib`) for expansion.

## 📦 plugin.yml Example

```yaml
libraries:
  - [groupId]:[artifactId]:[version]  
  - com.github.theprogmatheus:1.0-SNAPSHOT 

repositories:
  - https://jitpack.io  
```

## 🔧 How It Works

During plugin startup, CraftLib scans the plugin.yml for libraries and optional repositories.
Dependencies are downloaded and loaded dynamically using a custom class loader.
You don’t need to bundle external JARs inside your plugin — CraftLib handles it for you.

## 💡 Use Cases

Reduce plugin size by avoiding shading.
Load internal or private libraries directly from GitHub or JitPack.
Experiment with modular or pluggable plugin architectures.

## 🛠️ How to Use

1. Add CraftLib to your plugin
To use CraftLib, simply add it as a required plugin in your plugin.yml:

```yaml
depend: [CraftLib]
```
Your plugin will only load if CraftLib is present on the server.
You don't need to bundle CraftLib in your own JAR — just include it on the server like any other plugin.

>💡 Tip: You can download the latest version of CraftLib from Releases.

2. Declare dependencies in plugin.yml
Use the libraries and repositories sections to declare what your plugin needs at runtime:

```yaml
name: "MyAwesomePlugin"
main: "com.example.myplugin.MyAwesomePlugin"
version: "1.0.0"
depend: ["CraftLib"]

repositories:
  - https://jitpack.io
  - https://repo.maven.apache.org/maven2

libraries:
  - com.github.User:LibraryName:VERSION
  - org.apache.commons:commons-lang3:3.12.0
```
CraftLib will:

Automatically download the JARs from the specified repositories.

Load them in a classloader isolated from other plugins and the server itself.

3. Use your libraries like magic 🪄
After loading, all classes from the declared libraries will be available in your plugin’s classpath as if they were bundled — but without increasing your JAR size or risking class conflicts.

You can now code using those libraries just like usual:

```java
import org.apache.commons.lang3.StringUtils;

public class MyAwesomePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Is empty? " + StringUtils.isEmpty("CraftLib rocks!"));
    }
}
```
✅ Done!
No shading, no fat JARs, no hassle.
CraftLib handles the dependency resolution and class loading at runtime, so you can focus on coding your plugin.

## 🚀 Future Plans

- Built-in cache system.
- Checksum verification.
- Logging and diagnostics.
- Runtime unloading and reloading support.

## 📄 License

[MIT License](LICENSE).

---

Made with ☕ and ❤️ by [@TheProgMatheus](https://github.com/theprogmatheus)
