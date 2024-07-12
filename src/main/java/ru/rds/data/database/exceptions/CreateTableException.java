package ru.rds.data.database.exceptions;

import ru.rds.data.storage.exceptions.StorageException;

/**
 * Исключение, которое может возникнуть при создании Таблицы ({@link ru.rds.data.database.Table})
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class CreateTableException extends StorageException {

	public CreateTableException(String message) {
		super(message);
	}

	public CreateTableException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateTableException(Throwable cause) {
		super(cause);
	}

	public CreateTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
