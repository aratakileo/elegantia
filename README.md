# Elegantia

GUI library for Minecraft and more. It is used in Araraki Leo mods. So far, the library is in the early stages of development, which is why it is not yet intended for widespread use (early access).

At the moment, the library contains tools:
- for working with gif images
- for creating configurations and presenting them as a graphical interface, which is automatically applied to Mod Menu
- for rendering widgets and positioning them in space both in relation to the screen and to other widgets
- for checking mod updates from Modrinth
- advanced mathematical interfaces representations (like Rect, Vector, etc.)
- a little list of custom widgets, among which you can highlight CompositeWidget, which allows you to effortlessly create a custom composite widget from other widgets (including vanilla ones)

### Getting started
Below is the version for Minecraft from `1.20.2` to `1.20.4`. For Minecraft version from `1.20` to `1.20.1`, replace `1.20.4` with `1.20.1`.
<details><summary><code>build.gradle</code></summary>

```groovy
repositories {
    maven {
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation "maven.modrinth:elegantia:0.0.1-alpha+fabric-1.20.4"
}
```
</details>

<details><summary><code>build.gradle.kts</code></summary>

```groovy
repositories {
    maven("https://api.modrinth.com/maven")
}

dependencies {
    modImplementation("maven.modrinth", "elegantia", "0.0.1-alpha+fabric-1.20.4")
}
```
</details>
