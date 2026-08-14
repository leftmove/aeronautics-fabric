package dev.simulated_team.simulated.content.end_sea;

import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;

import java.io.IOException;

public class EndSeaFadeTransformer implements ShaderPreProcessor {
    @Override
    public String modify(final Context ctx, final String source) throws IOException {
        return source;
    }
}
