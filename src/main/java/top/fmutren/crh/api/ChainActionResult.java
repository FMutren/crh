package top.fmutren.crh.api;

public enum ChainActionResult {

    PASS,
    SUCCESS,
    CONSUME;

    public boolean consumesAction() {
        return this == SUCCESS || this == CONSUME;
    }

}
