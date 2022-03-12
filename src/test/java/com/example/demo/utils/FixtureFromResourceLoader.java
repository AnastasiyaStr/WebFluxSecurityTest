package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.adelean.inject.resources.core.InjectResources.resource;

public class FixtureFromResourceLoader implements ArgumentsProvider {

    private static final Map<Class<?>, String> CACHE = new HashMap<>();

    private String resourcePath;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        Class<?> testClass = extensionContext.getTestClass()
                .orElseThrow(() -> new RuntimeException("Can not access test class"));
        Method testMethod = extensionContext.getTestMethod().orElseThrow(() ->
                new RuntimeException("Test class must be annotated with @TestResource"));

        resourcePath = Optional.ofNullable(testClass.getAnnotation(TestResource.class))
                .orElseThrow(() ->
                        new RuntimeException("Test class must be annotated with @TestResource")).path();

        if (!CACHE.containsKey(testClass)) {
            CACHE.put(testClass, resource().withPath(resourcePath).asText().text());
        }
        return Stream.of(Arguments.of(Stream.of(testMethod.getParameters())
                .map(parameter -> {
                    try {
                        boolean isJson = parameter.getAnnotation(Json.class) != null;
                        return parseParameter(CACHE.get(testClass), testMethod.getName(),
                                parameter.getName(),
                                parameter.getType(),
                                isJson);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray()));
    }

    private Object parseParameter(String is, String testMethodName, String name,
                                  Class<?> type, boolean isJson)
            throws IOException {
        YAMLFactory factory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(factory)
                .configure(MapperFeature.USE_ANNOTATIONS, false);
        YAMLParser parser = factory.createParser(is);
        TreeNode tree = parser.readValueAsTree();

        tree = tree.at("/testMethods");
        if (tree.isMissingNode()) {
            throw new RuntimeException("Can not find 'testMethods' object in the resource file: "
                    + resourcePath);
        }
        tree = tree.at("/" + testMethodName);
        if (tree.isMissingNode()) {
            throw new RuntimeException("Can not find test method '" + testMethodName +
                    "' in the 'testMethods' object");
        }
        tree = tree.at("/parameters");
        if (tree.isMissingNode()) {
            throw new RuntimeException("Can not find 'parameters' object in test method: "
                    + testMethodName);
        }
        tree = tree.at("/" + name);
        if (tree.isMissingNode()) {
            throw new RuntimeException("Can not find parameter '" + name + "' in test method: "
                    + testMethodName);
        }
        if (isJson) {
            StringWriter stringWriter = new StringWriter();

            mapper.writeTree(new JsonFactory().createGenerator(stringWriter), tree);
            return stringWriter.toString();
        }
        return mapper.readValue(tree.traverse(), type);
    }
}
