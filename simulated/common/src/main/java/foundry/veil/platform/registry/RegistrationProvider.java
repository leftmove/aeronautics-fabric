package foundry.veil.platform.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class RegistrationProvider<T> {
    private static final Map<ResourceKey<?>, Registry<?>> CUSTOM = new ConcurrentHashMap<>();

    private final Registry<T> registry;
    private final String modId;

    private RegistrationProvider(final Registry<T> registry, final String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> RegistrationProvider<T> get(final ResourceKey<? extends Registry<T>> key, final String modId) {
        return new RegistrationProvider<>(registry(key), modId);
    }

    public static <T> RegistrationProvider<T> get(final Registry<T> registry, final String modId) {
        return new RegistrationProvider<>(registry, modId);
    }

    @SuppressWarnings("unchecked")
    private static <T> Registry<T> registry(final ResourceKey<? extends Registry<T>> key) {
        final Registry<?> existing = CUSTOM.get(key);
        if (existing != null) {
            return (Registry<T>) existing;
        }
        final Registry<?> builtin = BuiltInRegistries.REGISTRY.get(key.location());
        if (builtin != null) {
            return (Registry<T>) builtin;
        }
        final MappedRegistry<T> created = new MappedRegistry<>(key, Lifecycle.stable(), false);
        CUSTOM.put(key, created);
        return created;
    }

    public RegistryObject<T> register(final String name, final Supplier<? extends T> supplier) {
        return this.register(new ResourceLocation(this.modId, name), supplier);
    }

    public RegistryObject<T> register(final ResourceLocation id, final Supplier<? extends T> supplier) {
        final T value = Registry.register(this.registry, id, supplier.get());
        if (value instanceof final DataComponentType<?> type) {
            type.bind(id);
        }
        return new DirectRegistryObject<>(id, value);
    }

    public Registry<T> asVanillaRegistry() {
        return this.registry;
    }

    private record DirectRegistryObject<T>(ResourceLocation getId, T value) implements RegistryObject<T> {
        @Override
        public T get() {
            return this.value;
        }
    }
}
