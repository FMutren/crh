package top.fmutren.crh.platform;

import net.minecraft.resources.ResourceLocation;

public final class ResourceLocations {

    private ResourceLocations() {
    }

    @SuppressWarnings("removal")
    public static ResourceLocation parse(String value) {
        return new ResourceLocation(value);
    }

}
