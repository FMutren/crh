package top.fmutren.crh.compat.createcasing;

public final class CreateCasingCompatLoader {

    private CreateCasingCompatLoader() {
    }

    public static CreateCasingBridge load(boolean createCasingLoaded) {
        if (!createCasingLoaded) {
            return CreateCasingBridge.NOOP;
        }

        try {
            var clazz = Class.forName("top.fmutren.crh.compat.createcasing.CreateCasingBridgeImpl");
            return (CreateCasingBridge) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | LinkageError error) {
            return CreateCasingBridge.NOOP;
        }
    }

}
