package io.github.aratakileo.elegantia

final class Constants {
    private Constants() {}

    final static class Dirs {
        private Dirs() {}

        public final static String ROOT = "elegantia-gradle"
        public final static String LIBS = ROOT + "/libs"
        public final static String CACHED = ROOT + "/cached"
        public final static String GENERATED = ROOT + "/generated"
        public final static String GENERATED_RESOURCES = GENERATED + "/resources"
        public final static String GENERATED_JAVA = GENERATED + "/java"
        public final static String LOCAL_PROTO = ROOT + "/proto"
        public final static String MAIN_RESOURCES = "src/main/resources"
        public final static String MAIN_JAVA = "src/main/java"
        public final static String PROTO = "buildSrc/" + MAIN_RESOURCES + "/proto"
        public final static String CLASSES = "classes/java/main"
        public final static String ELEGANTIA_PACKAGE = "io/github/aratakileo/elegantia"
        public final static String ENTRYPOINT_PACKAGE = ELEGANTIA_PACKAGE + "/core/entrypoint"
    }

    final static class Files {
        private Files() {}

        public final static String MANIFEST_BINDINGS = Dirs.CACHED + "/manifestBindings.json"
        public final static String ENTRY_POINTS = Dirs.CACHED + "/entryPoints.json"
        public final static String ORIGINAL_FABRIC_MANIFEST = Dirs.MAIN_RESOURCES + "/fabric.mod.json"
    }

    final static class Bytecodes {
        private Bytecodes() {}

        public final static String MOD_ENTRY_DESCRIPTOR = "L" + Dirs.ENTRYPOINT_PACKAGE + "/ModEntry;"
        public final static String ENTRY_INITIALIZER_DESCRIPTOR = "L" + Dirs.ENTRYPOINT_PACKAGE + "/EntryInitializer;"
    }

    public static Collection<String> SUBPROJECTS = Collections.unmodifiableCollection(
            ["Common", "Fabric", "Forge", "NeoForge"]
    )
}
