package foundry.veil.platform.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryObject<T> extends Supplier<T> {
    ResourceLocation getId();

    default Holder<T> asHolder() {
        return Holder.direct(this.get());
    }

    default boolean isPresent() {
        return this.get() != null;
    }
}
