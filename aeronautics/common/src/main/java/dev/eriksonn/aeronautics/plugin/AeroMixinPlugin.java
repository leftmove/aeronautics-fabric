package dev.eriksonn.aeronautics.plugin;

import foundry.veil.api.compat.IrisCompat;
import foundry.veil.api.compat.SodiumCompat;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AeroMixinPlugin implements IMixinConfigPlugin {
    private boolean sodiumPresent;
    private boolean irisPresent;
    private boolean neoforgePresent;

    @Override
    public void onLoad(final String mixinPackage) {
        this.sodiumPresent = SodiumCompat.isLoaded();
        this.irisPresent = IrisCompat.isLoaded();
        try {
            Class.forName("net.minecraftforge.client.ChunkRenderTypeSet");
            this.neoforgePresent = true;
        } catch (final ClassNotFoundException ignored) {
            this.neoforgePresent = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        if (mixinClassName.endsWith("ChunkRenderTypeSetAccessor")) {
            return this.neoforgePresent;
        }

        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.vanilla")) {
            return !this.sodiumPresent;
        }

        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.sodium")) {
            return this.sodiumPresent;
        }

        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.iris")) {
            return this.irisPresent;
        }

        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {

    }
}
