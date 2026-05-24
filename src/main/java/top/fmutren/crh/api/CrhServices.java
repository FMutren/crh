package top.fmutren.crh.api;

public final class CrhServices {

    private static NetworkBridge network;
    private static CreateBridge create;

    private CrhServices() {
    }

    public static void bootstrap(NetworkBridge networkBridge, CreateBridge createBridge) {
        network = networkBridge;
        create = createBridge;
    }

    public static NetworkBridge network() {
        if (network == null) {
            throw new IllegalStateException("CRH network bridge has not been initialized");
        }
        return network;
    }

    public static CreateBridge create() {
        if (create == null) {
            throw new IllegalStateException("CRH Create bridge has not been initialized");
        }
        return create;
    }

}
