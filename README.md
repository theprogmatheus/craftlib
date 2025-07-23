# CraftLib

CraftLib is a lightweight and flexible runtime dependency loader for Bukkit plugins. It integrates with Maven repositories, handles download and classloading dynamically, and gives plugin developers an easy, powerful way to manage libraries at runtime.

---

## ✨ Features

* Load external dependencies (JARs) at runtime.
* Support for custom Maven repositories (defaults to Maven Central).
* Simple configuration via a dedicated `craftlib` section in `plugin.yml`.
* Injects dependencies directly into the plugin’s own classloader (when possible).
* Built-in fallback that shades and loads libraries via a helper plugin if needed.
* Future-proof with a namespaced configuration block (`craftlib`).

---

## 📦 `plugin.yml` Example

```yaml
name: "MyAwesomePlugin"
main: "com.example.myplugin.MyAwesomePlugin"
version: "1.0.0"
depend: ["CraftLib"]

craftlib:
  libraries:
    - com.github.User:LibraryName:VERSION
    - org.apache.commons:commons-lang3:3.12.0
    - com.github.LMS5413:inventory-api:v1.0.10
  repositories:
    - https://jitpack.io
    - https://repo.maven.apache.org/maven2
````

---

## ⚙️ How It Works

CraftLib offers **two mechanisms** for loading libraries:

### ✅ 1. Direct Classloader Injection

CraftLib first attempts to inject the resolved libraries directly into the plugin’s classloader using `URLClassLoader.addURL`.

> 🔐 **Note for Java 16+ users**:
> Due to module restrictions, access to this method is blocked by default.
> You must start your server with:
>
> ```bash
> --add-opens java.base/java.net=ALL-UNNAMED
> ```
>
> This allows CraftLib to inject dependencies cleanly and safely, fully isolated in your plugin’s classloader.

This is the **recommended approach**, as it avoids dependency conflicts and keeps each plugin isolated.

---

### 🔁 2. Shaded Plugin Fallback (`CraftLibs`)

If classloader injection is not possible (e.g., no `--add-opens` and `URLClassLoader.addURL` is inaccessible), CraftLib automatically falls back to:

* Resolving and downloading the required libraries.
* Creating a **shaded JAR** (`libraries.jar`) with all the dependencies.
* Registering this JAR as a **separate plugin**, named `CraftLibs`.

Your plugin will then share this runtime helper and access its classes via the shared classloader.

> ⚠️ This fallback is not ideal:
>
> * All plugins share the same set of loaded dependencies.
> * If two plugins require different versions of the same library, **conflicts can happen**.
> * The first-loaded version of a class takes precedence.

Still, this ensures your plugin **can run** even under restricted environments or server setups.

---

## 🧪 Use Cases

* Reduce plugin size by loading libraries on demand.
* Avoid bundling huge fat JARs.
* Fetch libraries directly from GitHub, JitPack, or private Maven repos.
* Modular plugin architectures where each plugin manages its own dependencies.

---

## 🛠️ How to Use

### 1. Add CraftLib as a dependency

Declare `CraftLib` in your `plugin.yml` as a `depend`:

```yaml
depend: [CraftLib]
```

This ensures CraftLib is loaded before your plugin and ready to manage libraries.

You don't need to bundle CraftLib with your plugin – just install it on the server like any other plugin.

---

### 2. Declare runtime dependencies

In your `plugin.yml`, declare what libraries your plugin requires using the `craftlib` section:

```yaml
craftlib:
  repositories:
    - https://jitpack.io
    - https://repo.maven.apache.org/maven2
  libraries:
    - org.apache.commons:commons-lang3:3.12.0
    - com.github.User:LibraryName:VERSION
```

CraftLib will resolve and load them automatically before your plugin’s `onEnable()` runs.

---

### 3. Use your dependencies like magic 🪄

Once loaded, your plugin can use the libraries as if they were bundled:

```java
import org.apache.commons.lang3.StringUtils;

public class MyAwesomePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Empty? " + StringUtils.isEmpty("CraftLib rocks!"));
    }
}
```

No extra configuration, no need to package them inside your own plugin JAR.

---

## 🚨 Notes and Compatibility

* Java 16+ requires `--add-opens java.base/java.net=ALL-UNNAMED` to enable classloader injection.
* On older versions of Java (8–15), classloader injection works out of the box.
* The fallback shaded plugin (`CraftLibs`) is a workaround but may introduce classpath conflicts.
* CraftLib works best when each plugin uses isolated dependencies. Avoid sharing core libraries (e.g., SLF4J, Guava) across multiple plugins.

---

## 🚀 Future Plans

* Dependency checksum validation.
* Dependency caching and reuse.
* Optional integration with plugin update systems.
* Runtime unloading and reloading support.
* Auto-isolation of shaded plugins to reduce conflicts.

---

## 📄 License

Licensed under the [MIT License](LICENSE).

---

Made with ☕ and 💙 by [@TheProgMatheus](https://github.com/theprogmatheus)