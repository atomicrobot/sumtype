package com.madebyatomicrobot.sumtype.compiler;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.squareup.javapoet.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

class SumTypeWriter {
    private final SumTypeFields parsed;

    private final ClassName sumTypeQualifiedClassName;
    private final String generatedClassName;
    private final String generatedVisitorName;

    SumTypeWriter(SumTypeFields parsed) {
        this.parsed = parsed;

        sumTypeQualifiedClassName = ClassName.bestGuess(parsed.getQualifiedAnnotatedClassName());
        generatedClassName = String.format("%sSumType", parsed.typeName);
        generatedVisitorName = generatedClassName + "Visitor";
    }

    void writeJava(Filer filer) throws IOException {
        JavaFile.builder(parsed.packageName, buildTypeSpec()).build().writeTo(filer);
    }

    private TypeSpec buildTypeSpec() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(sumTypeQualifiedClassName);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);

            constructorBuilder
                    .addParameter(sumTypeType.typeName, sumTypeType.name)
                    .addStatement("this.$L = $L", sumTypeType.name, sumTypeType.name);

            builder.addField(buildSumTypeField(sumTypeType));
            builder.addMethod(buildStaticFactoryMethod(i, parsed.types.size(), sumTypeType));
            builder.addMethod(buildSumTypeInterfaceImplementation(sumTypeType));
        }

        builder.addMethod(constructorBuilder.build());

        builder.addType(buildVisitorInterface());
        builder.addMethod(buildVisitorAcceptMethod());

        builder.addJavadoc("Generated sum type class for {@link $N}", parsed.typeName);

        return builder.build();
    }

    private MethodSpec buildStaticFactoryMethod(int typeIndex, int totalTypes, SumTypeType sumTypeType) {
        String staticFactoryMethodName = "of" + getUpperCamelCaseName(sumTypeType);
        return MethodSpec.methodBuilder(staticFactoryMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.bestGuess(generatedClassName))
                .addParameter(sumTypeType.typeName, sumTypeType.name)
                .addStatement(buildStaticFactoryMethodStatement(typeIndex, totalTypes), generatedClassName, sumTypeType.name)
                .build();
    }

    private String buildStaticFactoryMethodStatement(int typeIndex, int totalTypes) {
        List<String> parameters = new ArrayList<String>();
        for (int i = 0; i < totalTypes; i++) {
            if (i == typeIndex) {
                parameters.add("$L");
            } else {
                parameters.add("null");
            }
        }

        String formattedParameters = Joiner.on(", ").join(parameters);
        return String.format("return new $L(%s)", formattedParameters);
    }

    private FieldSpec buildSumTypeField(SumTypeType sumTypeType) {
        return FieldSpec.builder(
                sumTypeType.typeName,
                sumTypeType.name,
                Modifier.FINAL, Modifier.PRIVATE)
                .build();
    }

    private MethodSpec buildSumTypeInterfaceImplementation(SumTypeType sumTypeType) {
        return MethodSpec.methodBuilder(sumTypeType.name)
                .returns(sumTypeType.typeName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $L", sumTypeType.name)
                .build();
    }

    private MethodSpec buildVisitorAcceptMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("accept")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(generatedVisitorName), "visitor");

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);

            builder.beginControlFlow("if ($L != null)", sumTypeType.name);
            builder.addStatement("visitor.$L($L)", buildVisitorMethodName(sumTypeType), sumTypeType.name);
            builder.addStatement("return");
            builder.endControlFlow();
        }

        return builder.build();
    }

    private TypeSpec buildVisitorInterface() {
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(generatedVisitorName)
                .addModifiers(Modifier.PUBLIC);

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);

            String methodName = buildVisitorMethodName(sumTypeType);
            builder.addMethod(
                    MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .addParameter(sumTypeType.typeName, sumTypeType.name)
                            .build());
        }

        return builder.build();
    }

    private String buildVisitorMethodName(SumTypeType sumTypeType) {
        return "visit" + getUpperCamelCaseName(sumTypeType);
    }

    private String getUpperCamelCaseName(SumTypeType sumTypeType) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, sumTypeType.name);
    }
}
