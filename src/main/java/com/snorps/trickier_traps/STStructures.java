package com.snorps.trickier_traps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.snorps.trickier_traps.structures.DesertPyramidStructure;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class STStructures {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     *
     * HOWEVER, do note that Deferred Registries only work for anything that is a Forge Registry. This means that
     * configured structures and configured features need to be registered directly to WorldGenRegistries as there
     * is no Deferred Registry system for them.
     */
    public static final DeferredRegister<Structure<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, StructureTutorialMain.MODID);

    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the resourcelocation of trickier_traps:desert_pyramid.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It great for mod/datapacks compatibility.
     *
     * IMPORTANT: Once you have set the name for your structure below and distributed your mod,
     * it should NEVER be changed or else it can cause worlds to become corrupted if they generated
     * any chunks with your mod with the old structure name. See MC-194811 in Mojang's bug tracker for details.
     *
     * Forge has an issue report here: https://github.com/MinecraftForge/MinecraftForge/issues/7363
     * Keep watch on that to know when it is safe to remove or change structure's registry names
     */
    public static final RegistryObject<Structure<NoFeatureConfig>> DESERT_PYRAMID = setupStructure("desert_pyramid", () -> (new DesertPyramidStructure(NoFeatureConfig.field_236558_a_)));

    /**
     * Helper method for registering all structures
     */
    private static <T extends Structure<?>> RegistryObject<T> setupStructure(String name, Supplier<T> structure) {
        return DEFERRED_REGISTRY_STRUCTURE.register(name, structure);
    }

    /**
     * This is where we set the rarity of your structures and determine if land conforms to it.
     * See the comments in below for more details.
     */
    public static void setupStructures() {
        setupStructure(
                DESERT_PYRAMID.get(), /* The instance of the structure */
                //spacing temporarily changed to 4,0 for playtesting, default is 30,0
                new StructureSeparationSettings(4 /* maximum distance apart in chunks between spawn attempts */,
                        0 /* minimum distance apart in chunks between spawn attempts */,
                        1038494734 /* this modifies the seed of the structure so no two structures always spawn over each-other. Make this large and unique. */),
                false);

        // Add more structures here and so on
    }

    /**
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the structureSeparationSettings argument. Called by registerFeatures.
     */
    public static <F extends Structure<?>> void setupStructure(
            F structure,
            StructureSeparationSettings structureSeparationSettings,
            boolean transformSurroundingLand)
    {
        /*
         * We need to add our structures into the map in Structure alongside vanilla
         * structures or else it will cause errors. Called by registerStructure.
         *
         * If the registration is setup properly for the structure,
         * getRegistryName() should never return null.
         */
        Structure.field_236365_a_.put(structure.getRegistryName().toString(), structure);

        /*
         * Will add land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         */
        if(transformSurroundingLand){
            Structure.field_236384_t_ =
                    ImmutableList.<Structure<?>>builder()
                            .addAll(Structure.field_236384_t_)
                            .add(structure)
                            .build();
        }

        /*
         * Adds the structure's spacing into several places so that the structure's spacing remains
         * correct in any dimension or worldtype instead of not spawning.
         *
         * However, it seems it doesn't always work for code made dimensions as they read from
         * this list beforehand. Use the WorldEvent.Load event in StructureTutorialMain to add
         * the structure spacing from this list into that dimension.
         */
        DimensionStructuresSettings.field_236191_b_ =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.field_236191_b_)
                        .put(structure, structureSeparationSettings)
                        .build();
    }
}
