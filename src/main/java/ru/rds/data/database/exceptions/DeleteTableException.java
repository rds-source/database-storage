package ru.rds.data.database.exceptions;

import ru.rds.data.storage.exceptions.StorageException;

/**
 * Исключение, которое может возникнуть при удалении Таблицы ({@link ru.rds.data.database.Table})
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class DeleteTableException extends StorageException {

	public DeleteTableException(String message) {
		super(message);
	}

	public DeleteTableException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeleteTableException(Throwable cause) {
		super(cause);
	}

	public DeleteTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
