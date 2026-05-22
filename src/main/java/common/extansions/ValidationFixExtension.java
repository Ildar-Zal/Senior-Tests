package common.extansions;

import api.config.Config;
import api.specs.RequestSpecs;
import common.annotations.WithValidationFix;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import java.net.URI;
//
//public class ValidationFixExtension implements BeforeEachCallback, AfterEachCallback {
//
//    @Override
//    public void beforeEach(ExtensionContext context) {
//        boolean hasAnnotation = context.getTestMethod()
//                .map(method -> method.isAnnotationPresent(WithValidationFix.class))
//                .orElse(false);
//
//        RequestSpecs.setUseValidationFix(hasAnnotation);
//    }
//
//    @Override
//    public void afterEach(ExtensionContext context) {
//        RequestSpecs.clearValidationFix();  // 👈 очищаем после каждого теста
//    }
//}