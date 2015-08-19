package com.madebyatomicrobot.sumtype.compiler;

import com.squareup.javapoet.TypeName;

class SumTypeType {
    final TypeName typeName;
    final String name;

    public SumTypeType(TypeName typeName, String name) {
        this.typeName = typeName;
        this.name = name;
    }

    boolean isVoidType() {
        return TypeName.VOID.equals(typeName);
    }

    TypeName getNonVoidTypeName() {
        return isVoidType() ? TypeName.get(Object.class) : typeName;
    }

    @Override
    public String toString() {
        return "SumTypeType{"
                + "typeName=" + typeName
                + ", name='" + name + '\''
                + '}';
    }
}
