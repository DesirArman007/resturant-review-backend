package com.desirArman.restaurant.exceptions;

public class ReviewNotAllowedException extends BaseException {
    public ReviewNotAllowedException(String message) {
        super(message);
    }

  public ReviewNotAllowedException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReviewNotAllowedException(Throwable cause) {
    super(cause);
  }

  public ReviewNotAllowedException() {
  }
}
