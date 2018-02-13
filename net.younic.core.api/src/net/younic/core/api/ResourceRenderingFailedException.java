package net.younic.core.api;

public class ResourceRenderingFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ResourceRenderingFailedException(String message) {
		super(message);
	}

	public ResourceRenderingFailedException(Throwable cause) {
		super(cause);
	}

	public ResourceRenderingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceRenderingFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
