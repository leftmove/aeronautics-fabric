package dev.ryanhcode.offroad.index;

import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.entities.BoreheadContraptionEntity;
import dev.simulated_team.simulated.index.SimEntityTypes;
import net.minecraft.world.entity.MobCategory;

public class OffroadEntityTypes {

    private static final SimulatedRegistrate REGISTRATE = Offroad.getRegistrate();

    public static final EntityEntry<BoreheadContraptionEntity> BOREHEAD_CONTRAPTION_ENTITY =
            REGISTRATE.entity("borehead_contraption_entity", BoreheadContraptionEntity::new, MobCategory.MISC)
                    .visual(() -> ContraptionVisual::new)
                    .renderer(() -> ContraptionEntityRenderer::new)
                    .transform((builder) -> SimEntityTypes.applyLoaderSpecificTransform(builder,
                            new SimEntityTypes.EntityLoaderData(20, 40, 1, 1, 0, true, true, false)))
                    .register();

    public static void init() {

    }

}
