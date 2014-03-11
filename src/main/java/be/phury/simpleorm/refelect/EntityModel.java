package be.phury.simpleorm.refelect;

import java.lang.reflect.Field;
import java.util.Map;

public class EntityModel {

	private Field idField;
	private Map<String, Field> columnFields;
	private Class<?> typeOf;
	
}
