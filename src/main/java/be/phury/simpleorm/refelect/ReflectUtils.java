package be.phury.simpleorm.refelect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import be.phury.simpleorm.annotation.Column;
import be.phury.simpleorm.annotation.Id;
import be.phury.simpleorm.annotation.Table;

public class ReflectUtils {
	
	public static Table getTableWithError(Class<?> typeOf) {
		final Table t = typeOf.getAnnotation(Table.class);
		if (t==null) {
			throw new IllegalArgumentException(typeOf  + " has no table annotation for mapping");
		}
		return t;
	}
	
	public static Integer getIdValueWithError(Object entity) {
		try {
			return getIdFieldWithError(entity.getClass()).getInt(entity);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void setIdValueWithError(Object entity, Integer id) {
		try {
			getIdFieldWithError(entity.getClass()).set(entity, id);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static Field getIdFieldWithError(Class<?> typeOf) {
		for (Field f : getColumnFields(typeOf)) {
			if (f.getAnnotation(Id.class) != null) {
				return f;
			}
		}
		throw new IllegalArgumentException(typeOf + " has no identity field");
	}
	
	public static Field getColumnFieldWithError(Class<?> typeOf, String fieldName) {
		try {
			return typeOf.getDeclaredField(fieldName);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(typeOf + " has no field " + fieldName);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(typeOf + " has no field " + fieldName);
		}
	}
	
	public static List<Field> getColumnFields(Class<?> typeOf) {
		final Field[] declaredFields = typeOf.getDeclaredFields();
		final List<Field> columnFields = new ArrayList<Field>(declaredFields.length);
		for (Field field : declaredFields) {
			if (field.getAnnotation(Column.class) != null) {
				columnFields.add(field);
			}
		}
		return columnFields;
	}
	
	public static Column getColumnWithError(Class<?> typeOf, String fieldName) {
		final Column column = getColumnFieldWithError(typeOf, fieldName).getAnnotation(Column.class);
		if (column == null) {
			throw new IllegalArgumentException(typeOf + " has no column field: " + fieldName);
		}
		return column;
	}
	
	private static boolean areSameFieldValue(Field f, Object o1, Object o2) {
		try {
			
			final Object v1 = f.get(o1);
			final Object v2 = f.get(o2);
			return (v1 == null && v2 == null) || (v1 != null && v1.equals(v2));
			
		} catch (IllegalArgumentException e) {
			// do nothing
		} catch (IllegalAccessException e) {
			// do nothing
		}
		return false;
	}
	
	public static List<Field> getModifiedFields(Object original, Object modified) {
		final List<Field> modifiedFields = new ArrayList<Field>();
		for (final Field field : getColumnFields(original.getClass())) {
			if (!areSameFieldValue(field, original, modified)) {
				modifiedFields.add(field);
			}
		}
		return modifiedFields;
	}
	
	public static Integer getFieldIndex(Class<?> typeOf, Field f) {
		return getColumnFields(typeOf).indexOf(f) + 1;
	}
	
	public static <T> T newInstance(Class<T> typeOf) {
		try {
			return typeOf.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
