package io.github.aratakileo.elegantia;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EntryPointScanner {
    public static class EntryPointResult {
        public final String type;
        public final String fullPath;

        public EntryPointResult(@NotNull String type, @NotNull String fullPath) {
            this.type = type;
            this.fullPath = fullPath;
        }
    }

    private final Project project;

    public EntryPointScanner(@NotNull Project project) {
        this.project = project;
    }

    public List<EntryPointResult> scanCommonProject() {
        List<EntryPointResult> results = new ArrayList<>();
        Project commonProject = project.findProject(":Common");

        if (commonProject == null) {
            project.getLogger().error("[Elegantia] project :Common not found");
            return results;
        }

        File classesDir = commonProject.getLayout().getBuildDirectory().dir("classes/java/main").get().getAsFile();

        if (!classesDir.exists()) {
            project.getLogger().warn("[Elegantia] classes directory not found. Make sure :Common:classes has run");
            return results;
        }

        scanDirectory(classesDir, results);
        return results;
    }

    private void scanDirectory(File directory, List<EntryPointResult> results) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, results);
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                try (InputStream is = new FileInputStream(file)) {
                    scanClassFile(is, results);
                } catch (IOException e) {
                    project.getLogger().error("[Elegantia] Failed to read class file: " + file.getAbsolutePath(), e);
                }
            }
        }
    }

    private void scanClassFile(InputStream is, List<EntryPointResult> results) throws IOException {
        ClassReader reader = new ClassReader(is);
        String className = reader.getClassName().replace('/', '.');

        ClassAnnotationInfo info = new ClassAnnotationInfo();

        reader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor.equals(Constants.Bytecodes.MOD_ENTRY_DESCRIPTOR)) {
                    info.hasModEntry = true;
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitEnum(String name, String desc, String value) {
                            if ("value".equals(name) && desc.contains(Constants.Dirs.ENTRYPOINT_PACKAGE + "/EntryPoint")) {
                                info.entryPointType = value; // CLIENT, SERVER, COMMON
                            }
                        }
                    };
                }
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String methodName, String methodDesc, String signature, String[] exceptions) {
                if (!info.hasModEntry) return null;

                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        if (descriptor.equals(Constants.Bytecodes.ENTRY_INITIALIZER_DESCRIPTOR)) {
                            String fullPath = className + "." + methodName;
                            results.add(new EntryPointResult(
                                    info.entryPointType != null ? info.entryPointType : "COMMON",
                                    fullPath
                            ));
                        }
                        return null;
                    }
                };
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    private static class ClassAnnotationInfo {
        boolean hasModEntry = false;
        String entryPointType = null;
    }
}