package be.phury.simpleorm.db;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.phury.simpleorm.annotation.Column;
import be.phury.simpleorm.refelect.ReflectUtils;

public class EntityMapper {
	
	private void setStatementValue(PreparedStatement stmt, Object value, Integer fieldIndex) {
		try {
			if (value == null) {
				stmt.setObject(fieldIndex, null);
			} else {
				if (value instanceof Integer) {
					stmt.setInt(fieldIndex, (Integer)value);
				} else if (value instanceof String) {
					stmt.setString(fieldIndex, (String)value);
				} else if (value instanceof Date) {
					stmt.setDate(fieldIndex, new java.sql.Date(((Date)value).getTime()));
				} else if (value instanceof byte[]) {
					stmt.setBytes(fieldIndex, (byte[])value);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void fromFieldsInEntity(PreparedStatement stmt, Class<?> typeOf, List<String> names, List<Object> values) {
		for (int i = 0; i < names.size(); i++) {
			final Field field = ReflectUtils.getColumnFieldWithError(typeOf, names.get(i));
			final Integer fieldIndex = ReflectUtils.getFieldIndex(typeOf, field);
			setStatementValue(stmt, values.get(i), fieldIndex);
		}
	}
	
	public void fromEntity(PreparedStatement stmt, Object entity) {
		final Class<?> typeOf = entity.getClass();
		for (final Field field : ReflectUtils.getColumnFields(typeOf)) {
			try {
				final Integer fieldIndex = ReflectUtils.getFieldIndex(typeOf, field);
				setStatementValue(stmt, field.get(entity), fieldIndex);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void fromEntity(PreparedStatement stmt, Object original, Object modified) {
		final Class<?> typeOf = modified.getClass();
		for (final Field field : ReflectUtils.getModifiedFields(original, modified)) {
			try {
				final Integer fieldIndex = ReflectUtils.getFieldIndex(typeOf, field);
				setStatementValue(stmt, field.get(modified), fieldIndex);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public <T> T toEntity(ResultSet rs, Class<T> typeOf) {
		try {
			
			rs.next();
			T entity = toEntityInternal(rs, typeOf);
			rs.close();
			return entity;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private <T> T toEntityInternal(ResultSet rs, Class<T> typeOf) {
		try {
			
			T entity = ReflectUtils.newInstance(typeOf);
			final List<Field> fields = ReflectUtils.getColumnFields(typeOf);
			for (int i = 0; i < fields.size(); i++) {
				final Field field = fields.get(i);
				final Column column = ReflectUtils.getColumnWithError(typeOf, field.getName());
				field.set(entity, rs.getObject(column.name()));
			}
			return entity;
			
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T> List<T> toEntityList(ResultSet rs, Class<T> typeOf) {
		final List<T> list = new LinkedList<T>();
		try {
			
			while (rs.next()) {
				list.add((T) toEntityInternal(rs, typeOf));
			}
			rs.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

}
