package byd.cxkcxkckx.gotome.mixin;

import byd.cxkcxkckx.gotome.client.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && ConfigManager.config.motionCameraEnabled && client.player.isSleeping()) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            context.fill(0, 0, width, height, 0xFF000000);
        }
    }
} 