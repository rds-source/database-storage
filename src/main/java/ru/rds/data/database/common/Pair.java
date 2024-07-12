package ru.rds.data.database.common;

/**
 * Класс, который хранит два значения любого типа. Предназначен в основном для удобного возвращения сразу пары результатов
 *
 * @param <FIRST>
 * @param <SECOND>
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Pair<FIRST, SECOND> {

	private FIRST  first;
	private SECOND second;

	public Pair() {
	}

	public Pair(FIRST first, SECOND second) {
		this.first = first;
		this.second = second;
	}

	public FIRST getFirst() {
		return first;
	}

	public void setFirst(FIRST first) {
		this.first = first;
	}

	public SECOND getSecond() {
		return second;
	}

	public void setSecond(SECOND second) {
		this.second = second;
	}

}
