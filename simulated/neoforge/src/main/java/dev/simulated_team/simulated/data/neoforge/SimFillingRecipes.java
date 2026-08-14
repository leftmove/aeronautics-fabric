package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SimFillingRecipes extends FillingRecipeGen {
    private final GeneratedRecipe HONEY_GLUE = this.create("honey_glue",
            b -> b.require(AllTags.AllFluidTags.HONEY.tag, 500)
                  .require(CommonMetal.IRON.plates)
                  .output(SimItems.HONEY_GLUE));

    public SimFillingRecipes(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Simulated.MOD_ID);
    }
}
