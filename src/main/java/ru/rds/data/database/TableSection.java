package ru.rds.data.database;

import java.util.Collections;
import java.util.List;
import ru.rds.data.storage.Sectionable;
import ru.rds.data.storage.SelectionType;
import ru.rds.data.storage.StorageSection;

/**
 * Секция Строк - реализауия {@link StorageSection} при работе с БД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class TableSection extends StorageSection<Row> {

	public TableSection() {
		setSort(Collections.emptyList());
		setElementsSelectionConditions(Collections.emptyList());
		setSelectionType(SelectionType.AND);
		setSectionNumber(0);
		setSectionSize(0);
	}

	public static TableSection of(Sectionable sectionable, long totalRowsCount, List<Row> rows) {
		TableSection section = empty(sectionable);
		section.setTotalElementsCount(totalRowsCount);
		section.setElements(rows);
		return section;
	}

	/**
	 * Создание пустой Секции Строк
	 *
	 * @param sectionable
	 * @return
	 */
	public static TableSection empty(Sectionable sectionable) {
		TableSection section = new TableSection();
		section.setSort(sectionable.getSort());
		section.setElementsSelectionConditions(sectionable.getElementsSelectionConditions());
		section.setSelectionType(sectionable.getSelectionType());
		section.setSectionNumber(sectionable.getSectionNumber());
		section.setSectionSize(sectionable.getSectionSize());
		return section;
	}

	/**
	 * Создание пустой Секции Строк
	 *
	 * @return
	 */
	public static TableSection empty() {
		return new TableSection();
	}

}
