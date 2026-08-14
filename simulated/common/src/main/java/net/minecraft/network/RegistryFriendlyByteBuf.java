package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
    private final RegistryAccess registryAccess;

    public RegistryFriendlyByteBuf(final ByteBuf source, final RegistryAccess registryAccess) {
        super(source);
        this.registryAccess = registryAccess;
    }

    public RegistryFriendlyByteBuf(final ByteBuf source) {
        this(source, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }
}
