package byd.cxkcxkckx.gotome.mixin;

import byd.cxkcxkckx.gotome.client.GotomeClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
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
    @Shadow private boolean detached;
    @Shadow private Vec3 position;
    @Shadow private float yRot;
    @Shadow private float xRot;

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Unique
    private float partialTick;

    @Inject(method = "setup", at = @At("HEAD"))
    private void onSetupHead(BlockGetter level, Entity entity, boolean detached, boolean mirror, float partialTick, CallbackInfo info) {
        this.partialTick = partialTick;
    }

    @ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void onSetCameraPosition(Args args) {
        if (Minecraft.getInstance().player == null) return;
        Vec3 cameraAnchorPos = new Vec3((Double) args.get(0), (Double) args.get(1), (Double) args.get(2));
        Object[] coords = new Object[] { cameraAnchorPos.x, cameraAnchorPos.y, cameraAnchorPos.z };
        GotomeClient.cameraController.applyCameraPosition(Minecraft.getInstance(), cameraAnchorPos, partialTick, coords);
        args.set(0, (Double) coords[0]);
        args.set(1, (Double) coords[1]);
        args.set(2, (Double) coords[2]);
    }

    @ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void onSetupSetRotationArgs(Args args) {
        Object[] rot = new Object[] { args.get(0), args.get(1) };
        GotomeClient.cameraController.applyCameraRotation(rot);
        args.set(0, (Float) rot[0]);
        args.set(1, (Float) rot[1]);
    }
}
