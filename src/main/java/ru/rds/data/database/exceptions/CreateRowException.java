package ru.rds.data.database.exceptions;

import ru.rds.data.storage.exceptions.StorageException;

/**
 * Исключение, которое может возникнуть при создании Строки ({@link ru.rds.data.database.Row})
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class CreateRowException extends StorageException {

	public CreateRowException(String message) {
		super(message);
	}

	public CreateRowException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateRowException(Throwable cause) {
		super(cause);
	}

	public CreateRowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
