package dev.eriksonn.aeronautics.plugin;

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
        this.sodiumPresent = classExists("me.jellysquid.mods.sodium.client.SodiumClientMod")
                || classExists("net.caffeinemc.mods.sodium.client.SodiumClientMod");
        this.irisPresent = classExists("net.irisshaders.iris.Iris");
        this.neoforgePresent = classExists("net.minecraftforge.client.ChunkRenderTypeSet");
    }

    private static boolean classExists(final String name) {
        try {
            Class.forName(name, false, AeroMixinPlugin.class.getClassLoader());
            return true;
        } catch (final ClassNotFoundException ignored) {
            return false;
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
