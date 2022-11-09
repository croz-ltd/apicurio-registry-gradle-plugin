package com.github.dmuharemagic.registry.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class IOUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtil.class);

    private IOUtil() {
        throw new UnsupportedOperationException("Utility class")
    }

    final static URL getResource(String path) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (!resource) {
            resource = IOUtil.class.getClassLoader().getResource(path);
            if (!resource) {
                LOGGER.warn("Could not load classpath resource: {}", path);
            }
        }

        resource
    }
}
