package byd.cxkcxkckx.gotome.mixin;

import byd.cxkcxkckx.gotome.client.GotomeClient;
import net.minecraft.client.MinecraftClient;
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

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        this.tickDelta = tickDelta;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onSetCameraPosition(Args args) {
        if (MinecraftClient.getInstance().player == null) return;
        Vec3d cameraAnchorPos = new Vec3d((Double) args.get(0), (Double) args.get(1), (Double) args.get(2));
        Object[] coords = new Object[] { cameraAnchorPos.x, cameraAnchorPos.y, cameraAnchorPos.z };
        GotomeClient.cameraController.applyCameraPosition(MinecraftClient.getInstance(), cameraAnchorPos, tickDelta, coords);
        args.set(0, (Double) coords[0]);
        args.set(1, (Double) coords[1]);
        args.set(2, (Double) coords[2]);
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        Object[] rot = new Object[] { args.get(0), args.get(1) };
        GotomeClient.cameraController.applyCameraRotation(rot);
        args.set(0, (Float) rot[0]);
        args.set(1, (Float) rot[1]);
    }
}