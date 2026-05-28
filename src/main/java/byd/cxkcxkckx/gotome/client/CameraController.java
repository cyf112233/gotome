package byd.cxkcxkckx.gotome.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class CameraController {
    private Vec3d cameraPos;
    private boolean freeLookActive = false;
    private float freeLookYaw;
    private float freeLookPitch;
    private Double lastFollowY;
    private Float lastPlayerYaw;
    private Float lastPlayerPitch;
    private float inertiaYawVelocity;
    private float inertiaPitchVelocity;
    private double verticalFollowOffset;
    private double verticalFollowVelocity;

    public void tick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) {
            resetTransientState();
            cameraPos = null;
            return;
        }

        if (ConfigManager.config.viewLockEnabled || !ConfigManager.config.motionCameraEnabled || firstPerson(client)) {
            freeLookActive = false;
            resetMotionState();
            return;
        }

        updateFreeLookState(client, player);
        updateMovementFollow(client, player);
        updateMouseInertia(player);
    }

    public void applyCameraPosition(MinecraftClient client, Vec3d playerPos, float tickDelta, Object[] argsHolder) {
        if (client.player == null) return;
        if (!ConfigManager.config.motionCameraEnabled) return;
        if (ConfigManager.config.viewLockEnabled) return;
        if (client.player.isSleeping()) return;
        if (ConfigManager.config.motionCameraDisableFirstPers && firstPerson(client)) return;

        updateCameraPosition(client, playerPos);
        if (cameraPos == null) return;
        argsHolder[0] = cameraPos.x;
        argsHolder[1] = cameraPos.y;
        argsHolder[2] = cameraPos.z;
    }

    public void applyCameraRotation(Object[] argsHolder) {
        if (freeLookActive) {
            argsHolder[0] = freeLookYaw;
            argsHolder[1] = freeLookPitch;
        }
    }

    public boolean isFreeLookActive() {
        return freeLookActive;
    }

    public void resetCameraPosition(MinecraftClient client) {
        if (client.player == null) {
            cameraPos = null;
            return;
        }
        cameraPos = new Vec3d(
                client.player.getX(),
                client.player.getY() + 1.0,
                client.player.getZ()
        );
        resetMotionState();
    }

    public Vec3d getCameraPos(MinecraftClient client) {
        if (client.player == null) {
            return cameraPos;
        }
        if (firstPerson(client)) {
            return new Vec3d(
                    client.player.getX(),
                    client.player.getY() + client.player.getEyeHeight(client.player.getPose()),
                    client.player.getZ()
            );
        }
        return cameraPos;
    }

    private void updateCameraPosition(MinecraftClient client, Vec3d playerPos) {
        if (cameraPos == null) {
            resetCameraPosition(client);
        }
        if (cameraPos == null) return;

        double distance = cameraPos.distanceTo(playerPos);
        double maxDist = Math.max(1.0, ConfigManager.config.motionCameraMaxDistance);
        if (distance > maxDist) {
            resetCameraPosition(client);
            return;
        }

        double smoothFactor = MathHelper.clamp(ConfigManager.config.motionCameraSmoothness, 0.1, 0.95);
        double dynamicFactor = smoothFactor * (1.0 - Math.exp(-distance / maxDist));
        double targetY = playerPos.y + client.player.getEyeHeight(client.player.getPose());
        double dx = playerPos.x - cameraPos.x;
        double dy = targetY - cameraPos.y;
        double dz = playerPos.z - cameraPos.z;

        cameraPos = new Vec3d(
                cameraPos.x + dx * dynamicFactor,
                cameraPos.y + dy * dynamicFactor,
                cameraPos.z + dz * dynamicFactor
        );
    }

    private void updateFreeLookState(MinecraftClient client, PlayerEntity player) {
        if (!ConfigManager.config.freeLookEnabled) {
            freeLookActive = false;
            return;
        }

        boolean keyDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), ConfigManager.config.freeLookKey);
        if (keyDown && !freeLookActive) {
            freeLookActive = true;
            freeLookYaw = player.getYaw();
            freeLookPitch = player.getPitch();
        } else if (!keyDown && freeLookActive) {
            freeLookActive = false;
        }
    }

    private void updateMovementFollow(MinecraftClient client, PlayerEntity player) {
        if (!ConfigManager.config.freeLookEnabled || freeLookActive) {
            lastFollowY = null;
            verticalFollowOffset = 0.0;
            verticalFollowVelocity = 0.0;
            return;
        }

        double currentY = player.getY();
        if (lastFollowY == null) {
            lastFollowY = currentY;
            return;
        }

        double deltaY = currentY - lastFollowY;
        double sensitivity = Math.max(0.1f, ConfigManager.config.freeLookVerticalSensitivity);
        double targetOffset = -deltaY * sensitivity * 2.5;

        verticalFollowVelocity = verticalFollowVelocity * 0.78 + targetOffset * 0.22;
        verticalFollowOffset = verticalFollowOffset * 0.82 + verticalFollowVelocity * 0.18;

        float nextPitch = MathHelper.clamp(player.getPitch() + (float) verticalFollowOffset, -90.0f, 90.0f);
        player.setPitch(nextPitch);
        lastFollowY = currentY;
    }

    private void updateMouseInertia(PlayerEntity player) {
        if (!ConfigManager.config.motionCameraYawInertiaEnabled || freeLookActive) {
            lastPlayerYaw = null;
            lastPlayerPitch = null;
            inertiaYawVelocity = 0f;
            inertiaPitchVelocity = 0f;
            return;
        }

        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();
        if (lastPlayerYaw == null || lastPlayerPitch == null) {
            lastPlayerYaw = currentYaw;
            lastPlayerPitch = currentPitch;
            return;
        }

        float deltaYaw = MathHelper.wrapDegrees(currentYaw - lastPlayerYaw);
        float deltaPitch = currentPitch - lastPlayerPitch;
        float response = MathHelper.clamp((float) ConfigManager.config.motionCameraYawInertia, 0.02f, 0.35f);

        inertiaYawVelocity += (deltaYaw - inertiaYawVelocity) * response;
        inertiaPitchVelocity += (deltaPitch - inertiaPitchVelocity) * response;
        inertiaYawVelocity *= 1.0f - response * 0.12f;
        inertiaPitchVelocity *= 1.0f - response * 0.12f;

        float nextYaw = MathHelper.wrapDegrees(currentYaw + inertiaYawVelocity);
        float nextPitch = MathHelper.clamp(currentPitch + inertiaPitchVelocity, -90.0f, 90.0f);
        player.setYaw(nextYaw);
        player.setPitch(nextPitch);

        lastPlayerYaw = nextYaw;
        lastPlayerPitch = nextPitch;
    }

    private boolean firstPerson(MinecraftClient client) {
        return client.options.getPerspective() == Perspective.FIRST_PERSON;
    }

    private void resetTransientState() {
        freeLookActive = false;
        resetMotionState();
    }

    private void resetMotionState() {
        lastFollowY = null;
        lastPlayerYaw = null;
        lastPlayerPitch = null;
        inertiaYawVelocity = 0f;
        inertiaPitchVelocity = 0f;
        verticalFollowOffset = 0.0;
        verticalFollowVelocity = 0.0;
    }
}
