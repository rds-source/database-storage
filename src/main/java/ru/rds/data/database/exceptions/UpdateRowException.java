package ru.rds.data.database.exceptions;

import ru.rds.data.storage.exceptions.StorageException;

/**
 * Исключение, которое может возникнуть при обновлении Строки ({@link ru.rds.data.database.Row})
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class UpdateRowException extends StorageException {

	public UpdateRowException(String message) {
		super(message);
	}

	public UpdateRowException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateRowException(Throwable cause) {
		super(cause);
	}

	public UpdateRowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
