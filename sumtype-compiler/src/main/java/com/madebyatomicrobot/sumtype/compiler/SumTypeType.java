package com.madebyatomicrobot.sumtype.compiler;

import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.Map;

class SumTypeType {
    private static final Map<TypeName, TypeName> primitiveMap = new HashMap<TypeName, TypeName>();
    static {
        primitiveMap.put(TypeName.BYTE, TypeName.get(Byte.class));
        primitiveMap.put(TypeName.SHORT, TypeName.get(Short.class));
        primitiveMap.put(TypeName.INT, TypeName.get(Integer.class));
        primitiveMap.put(TypeName.LONG, TypeName.get(Long.class));
        primitiveMap.put(TypeName.FLOAT, TypeName.get(Float.class));
        primitiveMap.put(TypeName.DOUBLE, TypeName.get(Double.class));
        primitiveMap.put(TypeName.BOOLEAN, TypeName.get(Boolean.class));
        primitiveMap.put(TypeName.CHAR, TypeName.get(Character.class));
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
            return primitiveMap.get(typeName);
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
