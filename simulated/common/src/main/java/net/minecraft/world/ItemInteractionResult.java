package net.minecraft.world;

public final class ItemInteractionResult {
    public static final InteractionResult SUCCESS = InteractionResult.SUCCESS;
    public static final InteractionResult CONSUME = InteractionResult.CONSUME;
    public static final InteractionResult CONSUME_PARTIAL = InteractionResult.CONSUME_PARTIAL;
    public static final InteractionResult PASS = InteractionResult.PASS;
    public static final InteractionResult FAIL = InteractionResult.FAIL;
    public static final InteractionResult PASS_TO_DEFAULT_BLOCK_INTERACTION = InteractionResult.PASS;
    public static final InteractionResult SKIP_DEFAULT_BLOCK_INTERACTION = InteractionResult.PASS;
    public static final InteractionResult SUCCESS_NO_ITEM_USED = InteractionResult.SUCCESS;

    private ItemInteractionResult() {
    }

    public static InteractionResult sidedSuccess(final boolean clientSide) {
        return InteractionResult.sidedSuccess(clientSide);
    }
}
