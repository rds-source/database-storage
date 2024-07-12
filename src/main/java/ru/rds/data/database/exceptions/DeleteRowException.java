package ru.rds.data.database.exceptions;

import ru.rds.data.storage.exceptions.StorageException;

/**
 * Исключение, которое может возникнуть при удалении Строки ({@link ru.rds.data.database.Row})
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class DeleteRowException extends StorageException {

	public DeleteRowException(String message) {
		super(message);
	}

	public DeleteRowException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeleteRowException(Throwable cause) {
		super(cause);
	}

	public DeleteRowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
