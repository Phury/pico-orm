package be.phury.simpleorm.db;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import be.phury.simpleorm.annotation.Column;
import be.phury.simpleorm.annotation.Table;
import be.phury.simpleorm.refelect.ReflectUtils;

public class SqlBuilder {
	
	public String select(Class<?> typeOf, String...clauses) {
		return select(typeOf, Arrays.asList(clauses));
	}
	public String select(Class<?> typeOf, List<String> clauses) {
		Table t = ReflectUtils.getTableWithError(typeOf);
		final List<String> clausesStr = new ArrayList<String>();
		for (String fieldName : clauses) {
			final Field field = ReflectUtils.getColumnFieldWithError(typeOf, fieldName);
			final Column column = field.getAnnotation(Column.class);
			if (column != null) {
				clausesStr.add(column.name() + "=? ");
			}
		}
		final String sql = MessageFormat.format("select * from {0} where 1=1 {1}",
				t.name(),
				clausesStr.size() == 0 ? "" : " and " + StringUtils.join(clausesStr, " and "));
		System.out.println(sql);
		return sql;
	}
	
	public String delete(Class<?> typeOf, String...clauses) {
		final Table t = ReflectUtils.getTableWithError(typeOf);
		final List<String> clausesStr = new ArrayList<String>();
		for (String fieldName : clauses) {
			final Column column = ReflectUtils.getColumnWithError(typeOf, fieldName);
			if (column != null) {
				clausesStr.add(column.name() + "=? ");
			}
		}
		final String sql = MessageFormat.format(
				"delete from {0} where 1=1 {1}",
				t.name(),
				clausesStr.size() == 0 ? "" : " and " + StringUtils.join(clausesStr, " and "));
		System.out.println(sql);
		return sql;
	}
	
	public String insert(Class<?> typeOf) {
		final Table t = ReflectUtils.getTableWithError(typeOf);
		final List<Field> fields = ReflectUtils.getColumnFields(typeOf);
		final List<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < fields.size(); i++) {
			final Column column = ReflectUtils.getColumnWithError(typeOf, fields.get(i).getName());
			if (column != null) {
				fieldNames.add(column.name());
			}
		}
		final String sql = MessageFormat.format(
				"insert into {0} ({1}) values ({2});", 
				t.name(), 
				StringUtils.join(fieldNames, ","), 
				StringUtils.join(times("?", fieldNames.size()), ","));
		System.out.println(sql);
		return sql;
	}
	
	public String update(Object original, Object updated) {
		final Class<?> typeOf = original.getClass();
		final Table t = ReflectUtils.getTableWithError(typeOf);
		final List<String> clauses = new ArrayList<String>();
		for (Field field : ReflectUtils.getModifiedFields(original, updated)) {
			try {
				final Column column = ReflectUtils.getColumnWithError(typeOf, field.getName());
				clauses.add(column.name() + "=? ");
			} catch (IllegalArgumentException e) {
				// do nothing
			}
		}
		final String sql = MessageFormat.format(
				"update {0} set {1} where {2}=?;", 
				t.name(), 
				StringUtils.join(clauses, ","), 
				ReflectUtils.getColumnWithError(typeOf, ReflectUtils.getIdFieldWithError(typeOf).getName()).name());
		System.out.println(sql);
		return sql;
	}
	
	private List<String> times(String str, Integer n) {
		List<String> list = new ArrayList<String>(n);
		for (int i = 0; i < n; i++) {
			list.add(str);
		}
		return list;
	}
}
