package net.minecraft.core.component;

public final class TypedDataComponent<T> {
    private final DataComponentType<T> type;
    private final T value;

    public TypedDataComponent(final DataComponentType<T> type, final T value) {
        this.type = type;
        this.value = value;
    }

    public DataComponentType<T> type() {
        return this.type;
    }

    public T value() {
        return this.value;
    }
}
