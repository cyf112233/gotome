package byd.cxkcxkckx.gotome.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.phys.Vec3;

public class MotionCamera {
    public Vec3 cameraPos;
    private Float lastYaw = null;
    private Float lastPitch = null;
    private Float inertiaYawSpeed = 0f;
    private Float inertiaPitchSpeed = 0f;
    private static final double SLEEP_CAM_LERP = 0.2;
    private static final double SLEEP_CAM_HEIGHT = 2.5;
    private static final float SLEEP_CAM_PITCH = 90f;

    public boolean firstPerson() {
        return Minecraft.getInstance().options.getCameraType() == Options.CameraType.FIRST_PERSON;
    }

    public Vec3 getCameraPos() {
        if (firstPerson()) {
            return new Vec3(
                    Minecraft.getInstance().player.getX(),
                    Minecraft.getInstance().player.getY() + Minecraft.getInstance().player.getEyeHeight(Minecraft.getInstance().player.getPose()),
                    Minecraft.getInstance().player.getZ()
            );
        }
        return cameraPos;
    }

    public void update(Vec3 playerPos, float partialTick) {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().player.isSleeping()) return;
        if (ConfigManager.config.viewLockEnabled) return;
        if (ConfigManager.config.motionCameraEnabled) {
            if (cameraPos == null) {
                cameraPos = playerPos;
            }
            double distance = cameraPos.distanceTo(playerPos);
            double maxDist = ConfigManager.config.motionCameraMaxDistance;
            if (distance > maxDist) {
                cameraPos = new Vec3(playerPos.x, playerPos.y + 1.0, playerPos.z);
            } else {
                double smoothFactor = ConfigManager.config.motionCameraSmoothness;
                double dynamicFactor = smoothFactor * (1.0 - Math.exp(-distance / maxDist));
                double dx = playerPos.x - cameraPos.x;
                double dy = playerPos.y + Minecraft.getInstance().player.getEyeHeight(Minecraft.getInstance().player.getPose()) - cameraPos.y;
                double dz = playerPos.z - cameraPos.z;
                cameraPos = new Vec3(
                        cameraPos.x + dx * dynamicFactor,
                        cameraPos.y + dy * dynamicFactor,
                        cameraPos.z + dz * dynamicFactor
                );
            }
            if (ConfigManager.config.motionCameraYawInertiaEnabled) {
                float currentYaw = Minecraft.getInstance().player.getYRot();
                float currentPitch = Minecraft.getInstance().player.getXRot();
                if (lastYaw == null) lastYaw = currentYaw;
                if (lastPitch == null) lastPitch = currentPitch;
                float deltaYaw = currentYaw - lastYaw;
                float deltaPitch = currentPitch - lastPitch;
                float inertia = (float) ConfigManager.config.motionCameraYawInertia;
                float speed = (float) Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
                float threshold = 0.01f;
                if (speed > threshold) {
                    inertiaYawSpeed = deltaYaw;
                    inertiaPitchSpeed = deltaPitch;
                } else {
                    inertiaYawSpeed *= (1f - inertia);
                    inertiaPitchSpeed *= (1f - inertia);
                    Minecraft.getInstance().player.setYRot(currentYaw + inertiaYawSpeed);
                    Minecraft.getInstance().player.setXRot(currentPitch + inertiaPitchSpeed);
                }
                if (Math.abs(inertiaYawSpeed) < 0.001f) inertiaYawSpeed = 0f;
                if (Math.abs(inertiaPitchSpeed) < 0.001f) inertiaPitchSpeed = 0f;
                lastYaw = Minecraft.getInstance().player.getYRot();
                lastPitch = Minecraft.getInstance().player.getXRot();
            } else {
                lastYaw = null;
                lastPitch = null;
                inertiaYawSpeed = 0f;
                inertiaPitchSpeed = 0f;
            }
        }
    }
}
