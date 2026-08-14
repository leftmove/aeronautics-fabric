package dev.eriksonn.aeronautics.fabric.content.fluids.levitite;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

public abstract class LevititeBlendFluid extends FlowingFluid implements LevititeBlendDummyInterface {
	public static LevititeBlendFluid.Source SOURCE;
	public static LevititeBlendFluid.Flowing FLOWING;
	public static LiquidBlock BLOCK;
	public static Item BUCKET;

	public static void register() {
		SOURCE = Registry.register(BuiltInRegistries.FLUID, Aeronautics.path("levitite_blend"), new Source());
		FLOWING = Registry.register(BuiltInRegistries.FLUID, Aeronautics.path("flowing_levitite_blend"), new Flowing());
		BLOCK = Registry.register(BuiltInRegistries.BLOCK, Aeronautics.path("levitite_blend"), new LiquidBlock(SOURCE, BlockBehaviour.Properties.of()
				.replaceable()
				.noCollission()
				.strength(100.0F)
				.pushReaction(PushReaction.DESTROY)
				.noLootTable()
				.liquid()));
		BUCKET = Registry.register(BuiltInRegistries.ITEM, Aeronautics.path("levitite_blend_bucket"), new BucketItem(SOURCE, new Item.Properties()
				.craftRemainder(Items.BUCKET)
				.stacksTo(1)));
	}

	@Override
	public Fluid getFlowing() {
		return FLOWING;
	}

	@Override
	public Fluid getSource() {
		return SOURCE;
	}

	@Override
	public Item getBucket() {
		return BUCKET;
	}

	@Override
	protected boolean canConvertToSource(final Level level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(final LevelAccessor level, final BlockPos pos, final BlockState state) {
		net.minecraft.world.level.block.Block.dropResources(state, level, pos, level.getBlockEntity(pos));
	}

	@Override
	protected int getSlopeFindDistance(final LevelReader level) {
		return 3;
	}

	@Override
	protected int getDropOff(final LevelReader level) {
		return 2;
	}

	@Override
	protected boolean canBeReplacedWith(final FluidState fluidState, final BlockGetter reader, final BlockPos pos, final Fluid fluid, final Direction direction) {
		return direction == Direction.DOWN && !fluid.isSame(this);
	}

	@Override
	public int getTickDelay(final LevelReader level) {
		return 25;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	protected BlockState createLegacyBlock(final FluidState state) {
		return BLOCK.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(final Fluid fluid) {
		return fluid == SOURCE || fluid == FLOWING;
	}

	@Override
	public void tick(final Level level, final BlockPos pos, final FluidState state) {
		super.tick(level, pos, state);
		this.levititeBlendTick(level, pos, state);
	}

	public static class Flowing extends LevititeBlendFluid {
		@Override
		protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(final FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(final FluidState state) {
			return false;
		}
	}

	public static class Source extends LevititeBlendFluid {
		@Override
		public int getAmount(final FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(final FluidState state) {
			return true;
		}
	}
}
