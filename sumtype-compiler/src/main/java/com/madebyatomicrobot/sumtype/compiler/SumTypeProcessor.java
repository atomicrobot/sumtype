package com.madebyatomicrobot.sumtype.compiler;

import com.google.auto.service.AutoService;
import com.madebyatomicrobot.sumtype.annotations.SumType;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class SumTypeProcessor extends AbstractProcessor {
    private Types types;
    private Elements elements;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        types = env.getTypeUtils();
        elements = env.getElementUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new LinkedHashSet<String>();
        supportedTypes.add(SumType.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        processSumTypes(env);
        return true;
    }

    private void processSumTypes(RoundEnvironment env) {
        for (Element sumType : env.getElementsAnnotatedWith(SumType.class)) {
            try {
                processSumType((TypeElement) sumType);
            } catch (IOException ex) {
                error("Unable to process " + sumType.asType().getKind().name());
            }
        }
    }

    private void processSumType(TypeElement sumType) throws IOException {
        try {
            SumTypeParser parser = new SumTypeParser(elements, types, sumType);
            SumTypeFields parsed = parser.parse();
            messager.printMessage(Kind.OTHER, parsed.toString());
            SumTypeWriter writer = new SumTypeWriter(parsed);
            writer.writeJava(filer);
        } catch (Exception ex) {
            printException(ex);
        }
    }

    private void printException(Exception ex) {
        error(ExceptionUtil.printThrowable(ex));
    }

    private void error(String error) {
        messager.printMessage(Kind.ERROR, error);
    }
}
