package org.logscanner.util.fs;

public interface ScannerErrorHandler {
	static ScannerErrorHandler NOOP_ERROR_HANDLER = new ScannerErrorHandler() {
		@Override
		public boolean handleError(Throwable error) {
			return false;
		}
	};
	
	/**
	 * @param error
	 * @return true - if error was processed
	 */
	boolean handleError(Throwable error);
}
