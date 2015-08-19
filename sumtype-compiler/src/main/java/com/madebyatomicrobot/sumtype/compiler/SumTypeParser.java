package com.madebyatomicrobot.sumtype.compiler;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class SumTypeParser {
    private final Elements elements;
    private final Types types;
    private final TypeElement sumType;

    SumTypeParser(Elements elements, Types types, TypeElement record) {
        this.elements = elements;
        this.types = types;
        this.sumType = record;
    }

    // TODO
    // - Only on interfaces
    // - Parent interfaces

    SumTypeFields parse() {
        String packageName = elements.getPackageOf(sumType).getQualifiedName().toString();
        String typeName = sumType.getSimpleName().toString();
        SumTypeFields parsed = new SumTypeFields(packageName, typeName);
        parseTypeElement(parsed, sumType);
        return parsed;
    }

    private void parseTypeElement(SumTypeFields parsed, TypeElement typeElement) {
        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind().equals(ElementKind.METHOD)) {
                ExecutableElement executableElement = (ExecutableElement) element;
                if (!executableElement.getParameters().isEmpty()) {
                    parseError(String.format("%s is not a no-arg method.", element.getSimpleName()));
                }

                parsed.types.add(parseType(parsed, executableElement));
            }
        }

        //parseParentInterfaces(parsed, typeElement);
    }

    private SumTypeType parseType(SumTypeFields parsed, ExecutableElement executableElement) {
        TypeMirror typeMirror = executableElement.getReturnType();
        Name name = executableElement.getSimpleName();
        return new SumTypeType(TypeName.get(typeMirror), name.toString());
    }

    private void parseError(String message) {
        throw new IllegalArgumentException(String.format(
                "%s: %s",
                sumType.getSimpleName().toString(),
                message));
    }
}
