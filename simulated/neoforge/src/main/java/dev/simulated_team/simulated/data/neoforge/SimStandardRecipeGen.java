package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.neoforge.SimNeoForgeRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public class SimStandardRecipeGen extends BaseRecipeProvider {

    GeneratedRecipe PORTABLE_ENGINE_DYEING = this.createSpecial("crafting", "portable_engine_dyeing");

    public SimStandardRecipeGen(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Simulated.MOD_ID);
    }

    private GeneratedRecipe createSpecial(final String recipeType, final String path) {
        return this.register(consumer -> SpecialRecipeBuilder.special(SimNeoForgeRecipeTypes.PORTABLE_ENGINE_DYEING.getSerializer())
                .save(consumer, Simulated.path(recipeType + "/" + path).toString()));
    }
}
