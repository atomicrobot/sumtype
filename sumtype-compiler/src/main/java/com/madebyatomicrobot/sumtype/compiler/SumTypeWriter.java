package com.madebyatomicrobot.sumtype.compiler;

import com.google.common.base.Joiner;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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

        builder.addField(
                FieldSpec.builder(Object.class, "voidPlaceholder", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", Object.class)
                        .build());

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);

            constructorBuilder
                    .addParameter(sumTypeType.getObjectType(), sumTypeType.name)
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
        MethodSpec.Builder builder = MethodSpec.methodBuilder(staticFactoryMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.bestGuess(generatedClassName));

        if (!sumTypeType.isVoidType()) {
            builder.addParameter(sumTypeType.typeName, sumTypeType.name);
        }

        builder.addCode(buildStaticFactoryMethodImplementation(typeIndex, totalTypes, sumTypeType));

        return builder.build();
    }

    private CodeBlock buildStaticFactoryMethodImplementation(int typeIndex, int totalTypes, SumTypeType sumTypeType) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (!sumTypeType.isVoidType() && !sumTypeType.isPrimitiveType()) {
            builder.beginControlFlow("if ($L == null)", sumTypeType.name);
            builder.addStatement("throw new IllegalArgumentException()");
            builder.endControlFlow();
        }

        builder.add(buildStaticFactoryMethodInstantiation(typeIndex, totalTypes, sumTypeType));
        return builder.build();
    }

    private CodeBlock buildStaticFactoryMethodInstantiation(int typeIndex, int totalTypes, SumTypeType sumTypeType) {
        List<String> parameters = new ArrayList<String>();
        for (int i = 0; i < totalTypes; i++) {
            if (i == typeIndex) {
                parameters.add("$L");
            } else {
                parameters.add("null");
            }
        }

        String formattedParameters = Joiner.on(", ").join(parameters);
        String statementFormat = String.format("return new $L(%s)", formattedParameters);
        String name = sumTypeType.isVoidType() ? "voidPlaceholder" : sumTypeType.name;
        return CodeBlock.builder()
                .addStatement(statementFormat, generatedClassName, name)
                .build();
    }

    private FieldSpec buildSumTypeField(SumTypeType sumTypeType) {
        TypeName typeName = sumTypeType.getObjectType();
        return FieldSpec.builder(typeName, sumTypeType.name, Modifier.FINAL, Modifier.PRIVATE).build();
    }

    private MethodSpec buildSumTypeInterfaceImplementation(SumTypeType sumTypeType) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(sumTypeType.name)
                .returns(sumTypeType.typeName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.beginControlFlow("if ($L == null)", sumTypeType.name);
        codeBuilder.addStatement("throw new $T()", IllegalStateException.class);
        codeBuilder.endControlFlow();

        builder.addCode(codeBuilder.build());

        if (!sumTypeType.isVoidType()) {
            builder.addStatement("return $L", sumTypeType.name);
        }

        return builder.build();
    }

    private MethodSpec buildVisitorAcceptMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("accept")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(generatedVisitorName), "visitor");

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);
            builder.addCode(buildVisitorAcceptMethodImplementation(sumTypeType));
        }

        return builder.build();
    }

    private CodeBlock buildVisitorAcceptMethodImplementation(SumTypeType sumTypeType) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("if ($L != null)", sumTypeType.name);
        if (sumTypeType.isVoidType()) {
            builder.addStatement("visitor.$L()", buildVisitorMethodName(sumTypeType));
        } else {
            builder.addStatement("visitor.$L($L)", buildVisitorMethodName(sumTypeType), sumTypeType.name);
        }

        builder.addStatement("return");
        builder.endControlFlow();
        return builder.build();
    }

    private TypeSpec buildVisitorInterface() {
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(generatedVisitorName)
                .addModifiers(Modifier.PUBLIC);

        for (int i = 0; i < parsed.types.size(); i++) {
            SumTypeType sumTypeType = parsed.types.get(i);
            builder.addMethod(buildVisitorInterfaceMethod(sumTypeType));
        }

        return builder.build();
    }

    private MethodSpec buildVisitorInterfaceMethod(SumTypeType sumTypeType) {
        String methodName = buildVisitorMethodName(sumTypeType);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        if (!sumTypeType.isVoidType()) {
            methodBuilder.addParameter(sumTypeType.typeName, sumTypeType.name);
        }
        return methodBuilder.build();
    }

    private String buildVisitorMethodName(SumTypeType sumTypeType) {
        return "visit" + getUpperCamelCaseName(sumTypeType);
    }

    private String getUpperCamelCaseName(SumTypeType sumTypeType) {
        return sumTypeType.name.substring(0, 1).toUpperCase() + sumTypeType.name.substring(1);
    }
}
