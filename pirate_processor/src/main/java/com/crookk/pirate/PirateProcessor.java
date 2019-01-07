package com.crookk.pirate;

import android.support.annotation.Nullable;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class PirateProcessor extends AbstractProcessor {

    private static final String DEFAULT_PACKAGE = "com.crookk.pirate";

    private static final String OPTIONS_PACKAGE = "piratePackage";
    private static final String OPTIONS_MODULE = "pirateModule";

    private static final String GENERATE_CLASS_NAME = "PirateTreasureMap";
    private static final String GENERATE_MODULE_CLASS_NAME = "SecretPirateMap";

    private Messager messager;
    private Elements elements;
    private Filer filer;

    private Map<String, Island> islandsWithName = new HashMap<>();
    private Map<String, String> sOptions;

    // create a Map<String, Class> variable type
    private ParameterizedTypeName aStringTreasureMapType = ParameterizedTypeName
            .get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Treasure.class));

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {

        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        sOptions = processingEnv.getOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        try {

            int count = 0;

            for (Element element : roundEnvironment.getElementsAnnotatedWith(PirateIsland.class)) {

                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, ">> PirateIsland can only be applied to class.");
                    return true;
                }

                PirateIsland pirate = element.getAnnotation(PirateIsland.class);

                TypeElement typeElement = (TypeElement) element;

                // make the annotation as an Island*
                Island island = new Island(elements.getPackageOf(typeElement).getQualifiedName().toString(), typeElement.getSimpleName().toString(), pirate.auth());

                messager.printMessage(Diagnostic.Kind.NOTE, ">> Find an island called: " + pirate.key());

                islandsWithName.put(pirate.key(), island);
                count++;
            }

            // this round has nothing to process...
            if (count == 0) { return true; }

            messager.printMessage(Diagnostic.Kind.NOTE, ">> prepare to setup Pirate.");

            if (sOptions.containsKey(OPTIONS_MODULE)) {

                createModuleClass(roundEnvironment);

            } else {

                createMainClass(roundEnvironment);
            }

            return true;

        } catch (IOException e) {

            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }
    }

    private void createMainClass(RoundEnvironment roundEnvironment) throws IOException {

        TypeSpec.Builder output = createClass(roundEnvironment, GENERATE_CLASS_NAME, Modifier.PRIVATE, true);

        String packageName = sOptions.getOrDefault(OPTIONS_PACKAGE, DEFAULT_PACKAGE);

        messager.printMessage(Diagnostic.Kind.NOTE, ">> " + packageName + ".Pirates is created.");

        // create static `sail` method to retrieve treasure
        MethodSpec sailMethodSpec = MethodSpec.methodBuilder("sail")
                                              .addParameter(ParameterSpec.builder(String.class, "key")
                                                                         .build())
                                              .addModifiers(Modifier.PUBLIC)
                                              .addAnnotation(Override.class)
                                              .addAnnotation(Nullable.class)
                                              .returns(ClassName.get(DEFAULT_PACKAGE, "Treasure"))
                                              .addCode(CodeBlock.builder()
                                                                .addStatement("return ISLANDS.get(key)")
                                                                .build())
                                              .build();

        output.addMethod(sailMethodSpec);

        // create static `island` method to retrieve treasure
        MethodSpec islandMethodSpec = MethodSpec.methodBuilder("islands")
                                                .addModifiers(Modifier.PUBLIC)
                                                .addAnnotation(Override.class)
                                                .returns(aStringTreasureMapType)
                                                .addCode(CodeBlock.builder()
                                                                  .addStatement("return ISLANDS")
                                                                  .build())
                                                .build();

        output.addMethod(islandMethodSpec);

        JavaFile.builder(packageName, output.build()).build().writeTo(filer);
    }

    private void createModuleClass(RoundEnvironment roundEnvironment) throws IOException {

        TypeSpec.Builder output = createClass(roundEnvironment, GENERATE_MODULE_CLASS_NAME, Modifier.PUBLIC, false);

        String packageName = sOptions.getOrDefault(OPTIONS_PACKAGE, DEFAULT_PACKAGE);

        JavaFile.builder(packageName, output.build()).build().writeTo(filer);
    }

    private TypeSpec.Builder createClass(RoundEnvironment roundEnvironment, String className, Modifier modifier, boolean compileExtra) {

        // create a class named GENERATE_CLASS_NAME
        TypeSpec.Builder output = TypeSpec.classBuilder(className)
                                          // add field with type Map<String, Class> to the class
                                          .addField(FieldSpec.builder(aStringTreasureMapType, "ISLANDS").addModifiers(modifier, Modifier.STATIC, Modifier.FINAL).build())
                                          // mark the class as final public
                                          .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // add interface TreasureMap for main module
        if (compileExtra) {
            output.addSuperinterface(ClassName.get(TreasureMap.class));
        }

        // create a CodeBlock for `static { }`
        CodeBlock.Builder block = CodeBlock.builder().addStatement("ISLANDS = new java.util.HashMap<>()");

        // set the annotated class to the map
        for (Map.Entry<String, Island> element : islandsWithName.entrySet()) {

            String routeKey = element.getKey();
            Island island = element.getValue();

            ClassName activityClass = ClassName.get(island.packageName, island.activityName);

            messager.printMessage(Diagnostic.Kind.NOTE, ">> Add an island called: " + routeKey + ", class: " + activityClass);

            block.addStatement("ISLANDS.put(\"" + routeKey + "\", new " + DEFAULT_PACKAGE + ".Treasure(\"" + routeKey + "\", " + activityClass + ".class, " + island.auth + "))");
        }

        if (compileExtra) {

            // add other packages' activities
            List<String> extraPackages = getExtraPackages(roundEnvironment);

            for (String extraPackage : extraPackages) {

                // add all other packages' ISLANDS to app module Pirates class
                block.addStatement("ISLANDS.putAll(" + extraPackage + "." + GENERATE_MODULE_CLASS_NAME + ".ISLANDS)");
            }
        }

        return output.addStaticBlock(block.build());
    }

    private List<String> getExtraPackages(RoundEnvironment roundEnv) {

        List<String> result = new ArrayList<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Pirate.class)) {

            if (element.getKind() != ElementKind.CLASS) {

                messager.printMessage(Diagnostic.Kind.ERROR, ">> Pirate can only be applied to class.");

            } else {

                String[] packages = element.getAnnotation(Pirate.class).value();
                messager.printMessage(Diagnostic.Kind.NOTE, ">> found other packages: " + packages.length);
                Collections.addAll(result, packages);
            }
        }

        return result;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> annotations = new LinkedHashSet<>();

        annotations.add(Pirate.class.getCanonicalName());
        annotations.add(PirateIsland.class.getCanonicalName());

        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.RELEASE_8;
    }

    private class Island {

        String packageName;
        String activityName;
        boolean auth;

        Island(String packageName, String activityName, boolean auth) {

            this.packageName = packageName;
            this.activityName = activityName;
            this.auth = auth;
        }
    }
}
