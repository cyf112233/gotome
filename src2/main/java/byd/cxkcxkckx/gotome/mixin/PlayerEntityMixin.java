package byd.cxkcxkckx.gotome.mixin;

import byd.cxkcxkckx.gotome.client.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private boolean gotome$freeLookWasEnabled = false;
    @Unique
    private boolean gotome$motionCameraWasEnabled = false;

    @Inject(method = "sleep", at = @At("HEAD"))
    private void onSleep(CallbackInfo ci) {
        gotome$freeLookWasEnabled = ConfigManager.config.freeLookEnabled;
        gotome$motionCameraWasEnabled = ConfigManager.config.motionCameraEnabled;
        if (ConfigManager.config.freeLookEnabled) {
            ConfigManager.config.freeLookEnabled = false;
            ConfigManager.save();
        }
        if (ConfigManager.config.motionCameraEnabled) {
            ConfigManager.config.motionCameraEnabled = false;
            ConfigManager.save();
        }
    }

    @Inject(method = "wakeUp", at = @At("HEAD"))
    private void onWakeUp(CallbackInfo ci) {
        if (gotome$freeLookWasEnabled) {
            ConfigManager.config.freeLookEnabled = true;
            ConfigManager.save();
        }
        if (gotome$motionCameraWasEnabled) {
            ConfigManager.config.motionCameraEnabled = true;
            ConfigManager.save();
        }
    }
} 