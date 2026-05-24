package top.fmutren.crh.platform;

import net.minecraft.resources.ResourceLocation;

public final class ResourceLocations {

    private ResourceLocations() {
    }

    public static ResourceLocation parse(String value) {
        return ResourceLocation.parse(value);
    }

}
