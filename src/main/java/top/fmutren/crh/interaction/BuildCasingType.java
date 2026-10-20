package top.fmutren.crh.interaction;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildCasingType {

    private BuildCasingType(){
    }

    public record Encasing(Block base, Block variant){}
    public static Map<Item, Set<Encasing>> CASINGS = new HashMap<>();

    public static void buildCasingMap(FMLLoadCompleteEvent event){
        for (Block base : List.of(
                AllBlocks.SHAFT.get(),
                AllBlocks.COGWHEEL.get(),
                AllBlocks.FLUID_PIPE.get()
        )){
            for(Block variant : EncasingRegistry.getVariants(base)){
                if(variant instanceof EncasedBlock encasedBlock){
                    Item casing = encasedBlock.getCasing().asItem();
                    CASINGS.computeIfAbsent(casing, k -> new HashSet<>()).add(new Encasing(base, variant));
                }
            }
        }
    }
}
