package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public final class FileUtils {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static boolean exists(Path path) {
        return path != null && Files.exists(path);
    }

    public static boolean notExists(Path path) {
        return path == null || Files.notExists(path);
    }

    public static boolean isDirectory(Path path) {
        return path != null && Files.isDirectory(path);
    }

    public static boolean isFile(Path path) {
        return path != null && Files.isRegularFile(path);
    }

    public static String readString(Path path) {
        try {
            return Files.readString(path, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static Stream<String> lines(Path path) {
        try {
            return Files.lines(path, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void writeString(Path path, String content) {
        createParentDirectories(path);
        try {
            Files.writeString(path, content, DEFAULT_CHARSET,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void writeBytes(Path path, byte[] bytes) {
        createParentDirectories(path);
        try {
            Files.write(path, bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void appendString(Path path, String content) {
        createParentDirectories(path);
        try {
            Files.writeString(path, content, DEFAULT_CHARSET,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void copy(Path source, Path target, boolean replace) {
        createParentDirectories(target);
        try {
            if (replace) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(source, target);
            }
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void move(Path source, Path target, boolean replace) {
        createParentDirectories(target);
        try {
            if (replace) {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(source, target);
            }
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void deleteRecursively(Path path) {
        if (notExists(path)) return;

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()) // delete children first
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            throw ExceptionUtils.wrap(e);
                        }
                    });
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static void createParentDirectories(Path path) {
        Path parent = path.getParent();
        if (parent != null) {
            createDirectories(parent);
        }
    }

    public static InputStream newInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static OutputStream newOutputStream(Path path) {
        createParentDirectories(path);
        try {
            return Files.newOutputStream(path);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static Path resolve(Path base, String... more) {
        Path result = base;
        for (String m : more) {
            result = result.resolve(m);
        }
        return result.normalize();
    }

    public static String fileName(Path path) {
        return path.getFileName().toString();
    }

    public static String extension(Path path) {
        String name = fileName(path);
        int idx = name.lastIndexOf('.');
        return (idx == -1) ? "" : name.substring(idx + 1);
    }
}
