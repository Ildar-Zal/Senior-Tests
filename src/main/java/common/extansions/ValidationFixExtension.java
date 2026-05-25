package common.extansions;

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