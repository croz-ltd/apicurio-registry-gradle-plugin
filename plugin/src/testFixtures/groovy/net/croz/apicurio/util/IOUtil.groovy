package net.croz.apicurio.util

final class IOUtil {
  private IOUtil() {
    throw new UnsupportedOperationException("Utility class")
  }

  final static URL getResource(String path) {
    URL resource = Thread.currentThread().getContextClassLoader().getResource(path)
    if (!resource) {
      resource = IOUtil.getClassLoader().getResource(path)
    }

    resource
  }
}
