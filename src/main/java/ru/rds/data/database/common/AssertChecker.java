package ru.rds.data.database.common;

/**
 * Для удобства
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public abstract class AssertChecker {

	/**
	 * Проверка на NULL
	 *
	 * @param object
	 * @param message
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

}
