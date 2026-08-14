package foundry.veil.api.client.color;

public interface Colorc {
    int argb();

    default int red() {
        return (this.argb() >> 16) & 0xFF;
    }

    default int green() {
        return (this.argb() >> 8) & 0xFF;
    }

    default int blue() {
        return this.argb() & 0xFF;
    }

    default int alpha() {
        return (this.argb() >> 24) & 0xFF;
    }
}
