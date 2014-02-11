package de.zalando.zomcat.util;

import java.io.IOException;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class: a simple true/false toggle using file existence. This is used for heartbeat and jobs toggle.
 *
 * @author  hjacobs
 */
public class FileBackedToggle {

    private static final Logger LOG = LoggerFactory.getLogger(FileBackedToggle.class);

    private final Path path;
    private final boolean trueIfNotExists;

    public FileBackedToggle(final String path, final boolean trueIfNotExists) {
        this.path = FileSystems.getDefault().getPath(path);
        this.trueIfNotExists = trueIfNotExists;
    }

    public boolean toggle() {
        set(!get());
        return get();
    }

    public boolean get() {
        if (trueIfNotExists) {
            return Files.notExists(path);
        } else {
            return Files.exists(path);
        }
    }

    public void set(final boolean value) {
        if (trueIfNotExists == value) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                LOG.error("Failed to delete file {}", path.toAbsolutePath(), ex);
            }
        } else {
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException e) {
                // the file is already there
            } catch (IOException ex) {
                LOG.error("Failed to create file {}", path.toAbsolutePath(), ex);
            }
        }
    }

}
