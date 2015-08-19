package com.madebyatomicrobot.sumtype.compiler;

import java.util.ArrayList;
import java.util.List;

class SumTypeFields {
    final String packageName;
    final String typeName;
    final List<SumTypeType> types = new ArrayList<SumTypeType>();

    public SumTypeFields(String packageName, String typeName) {
        this.packageName = packageName;
        this.typeName = typeName;
    }

    String getQualifiedAnnotatedClassName() {
        return String.format("%s.%s", packageName, typeName);
    }

    @Override
    public String toString() {
        return "SumTypeFields{"
                + "packageName='" + packageName + '\''
                + ", typeName='" + typeName + '\''
                + ", types=" + types
                + '}';
    }
}
