package byd.cxkcxkckx.gotome.mixin;

import byd.cxkcxkckx.gotome.client.ConfigManager;
import byd.cxkcxkckx.gotome.client.GotomeClient;
import byd.cxkcxkckx.gotome.client.MotionCamera;
import byd.cxkcxkckx.gotome.client.FreeLook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean thirdPerson;
    @Shadow private Vec3d pos;
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Unique
    private float tickDelta;

    private static final MotionCamera motionCamera = GotomeClient.motionCamera;
    private static final FreeLook freeLook = GotomeClient.freeLook;

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        this.tickDelta = tickDelta;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onSetCameraPosition(Args args) {
        if (ConfigManager.config.motionCameraEnabled && net.minecraft.client.MinecraftClient.getInstance().player != null && (!ConfigManager.config.motionCameraDisableFirstPers || !motionCamera.firstPerson())) {
            Vec3d playerPos = net.minecraft.client.MinecraftClient.getInstance().player.getPos();
            motionCamera.update(playerPos, tickDelta);
            Vec3d cameraPos = motionCamera.getCameraPos();
            args.set(0, cameraPos.x);
            args.set(1, cameraPos.y);
            args.set(2, cameraPos.z);
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        if (ConfigManager.config.freeLookEnabled && freeLook.freelookActive) {
            args.set(0, freeLook.cameraYaw);
            args.set(1, freeLook.cameraPitch);
        }
    }

    // 新增：渲染全屏黑色遮罩（仅运动相机开启且玩家睡觉时）
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && ConfigManager.config.motionCameraEnabled && client.player.isSleeping()) {
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();
            context.fill(0, 0, width, height, 0xFF000000); // 画全屏黑色
        }
    }
}
