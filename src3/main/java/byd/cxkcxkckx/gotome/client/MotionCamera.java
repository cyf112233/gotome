package byd.cxkcxkckx.gotome.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.Vec3d;

public class MotionCamera {
    private Vec3d cameraPos;
    private Float lastYaw = null;
    private Float lastPitch = null;
    private Float inertiaYawSpeed = 0f;
    private Float inertiaPitchSpeed = 0f;
    private boolean lastInputActive = false;

    public boolean firstPerson() {
        return MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
    }

    public Vec3d getCameraPos() {
        if (firstPerson()) {
            return new Vec3d(
                    MinecraftClient.getInstance().player.getX(),
                    MinecraftClient.getInstance().player.getY() + MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()),
                    MinecraftClient.getInstance().player.getZ()
            );
        }
        return cameraPos;
    }

    public void update(Vec3d playerPos, float tickDelta) {
        if (MinecraftClient.getInstance().player == null) return;
        if (cameraPos == null) {
            cameraPos = playerPos;
        }
        double distance = cameraPos.distanceTo(playerPos);
        double maxDist = ConfigManager.config.motionCameraMaxDistance;
        if (distance > maxDist) {
            cameraPos = playerPos;
        } else {
            double smoothFactor = ConfigManager.config.motionCameraSmoothness;
            double dynamicFactor = smoothFactor * (1.0 - Math.exp(-distance / maxDist));
            double dx = playerPos.x - cameraPos.x;
            double dy = playerPos.y + MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()) - cameraPos.y;
            double dz = playerPos.z - cameraPos.z;
            cameraPos = new Vec3d(
                    cameraPos.x + dx * dynamicFactor,
                    cameraPos.y + dy * dynamicFactor,
                    cameraPos.z + dz * dynamicFactor
            );
        }
        // 真实视角惯性
        if (ConfigManager.config.motionCameraYawInertiaEnabled) {
            float currentYaw = MinecraftClient.getInstance().player.getYaw();
            float currentPitch = MinecraftClient.getInstance().player.getPitch();
            boolean inputActive = MinecraftClient.getInstance().mouse.isCursorLocked();
            if (lastYaw == null) lastYaw = currentYaw;
            if (lastPitch == null) lastPitch = currentPitch;
            float deltaYaw = currentYaw - lastYaw;
            float deltaPitch = currentPitch - lastPitch;
            float inertia = (float) ConfigManager.config.motionCameraYawInertia;
            float speed = (float)Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            float threshold = 0.01f;
            if (speed > threshold) {
                inertiaYawSpeed = deltaYaw;
                inertiaPitchSpeed = deltaPitch;
            } else {
                inertiaYawSpeed *= (1f - inertia);
                inertiaPitchSpeed *= (1f - inertia);
                MinecraftClient.getInstance().player.setYaw(currentYaw + inertiaYawSpeed);
                MinecraftClient.getInstance().player.setPitch(currentPitch + inertiaPitchSpeed);
            }
            if (Math.abs(inertiaYawSpeed) < 0.001f) inertiaYawSpeed = 0f;
            if (Math.abs(inertiaPitchSpeed) < 0.001f) inertiaPitchSpeed = 0f;
            lastYaw = MinecraftClient.getInstance().player.getYaw();
            lastPitch = MinecraftClient.getInstance().player.getPitch();
        } else {
            lastYaw = null;
            lastPitch = null;
            inertiaYawSpeed = 0f;
            inertiaPitchSpeed = 0f;
            lastInputActive = false;
        }
    }
} 