package io.julian.appchooser.util;

public final class Preconditions {

  private Preconditions() {
    throw new AssertionError("No instances.");
  }

  public static void checkArgument(boolean assertion) {
    if (!assertion) {
      throw new IllegalArgumentException();
    }
  }

  public static void checkArgument(boolean assertion, String message) {
    if (!assertion) {
      throw new IllegalArgumentException(message);
    }
  }

  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  public static <T> T checkNotNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }
}