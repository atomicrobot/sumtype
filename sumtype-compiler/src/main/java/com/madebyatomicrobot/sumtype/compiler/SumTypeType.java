package com.madebyatomicrobot.sumtype.compiler;

import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.Map;

class SumTypeType {
    private static final Map<TypeName, TypeName> PRIMITIVE_MAP = new HashMap<TypeName, TypeName>();
    static {
        PRIMITIVE_MAP.put(TypeName.BYTE, TypeName.get(Byte.class));
        PRIMITIVE_MAP.put(TypeName.SHORT, TypeName.get(Short.class));
        PRIMITIVE_MAP.put(TypeName.INT, TypeName.get(Integer.class));
        PRIMITIVE_MAP.put(TypeName.LONG, TypeName.get(Long.class));
        PRIMITIVE_MAP.put(TypeName.FLOAT, TypeName.get(Float.class));
        PRIMITIVE_MAP.put(TypeName.DOUBLE, TypeName.get(Double.class));
        PRIMITIVE_MAP.put(TypeName.BOOLEAN, TypeName.get(Boolean.class));
        PRIMITIVE_MAP.put(TypeName.CHAR, TypeName.get(Character.class));
    }

    final TypeName typeName;
    final String name;

    public SumTypeType(TypeName typeName, String name) {
        this.typeName = typeName;
        this.name = name;
    }

    boolean isVoidType() {
        return TypeName.VOID.equals(typeName);
    }

    boolean isPrimitiveType() {
        return typeName.isPrimitive();
    }

    TypeName getObjectType() {
        if (isVoidType()) {
            return TypeName.get(Object.class);
        }

        if (typeName.isPrimitive()) {
            return PRIMITIVE_MAP.get(typeName);
        }

        return typeName;
    }

    @Override
    public String toString() {
        return "SumTypeType{"
                + "typeName=" + typeName
                + ", name='" + name + '\''
                + '}';
    }
}
