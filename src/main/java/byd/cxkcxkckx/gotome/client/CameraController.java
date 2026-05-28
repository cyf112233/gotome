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
            return;
        }

        if (ConfigManager.config.viewLockEnabled) {
            freeLookActive = false;
            return;
        }

        updateFreeLookState(client, player);
        updateMovementFollow(client, player);
        updateMouseInertia(player);
    }

    public void applyCameraPosition(MinecraftClient client, Vec3d playerPos, float tickDelta, Object[] argsHolder) {
        if (client.player == null) return;
        if (!ConfigManager.config.motionCameraEnabled) return;
        if (ConfigManager.config.motionCameraDisableFirstPers && firstPerson(client)) return;

        updateCameraPosition(client, playerPos, tickDelta);
        Vec3d pos = getCameraPos(client);
        argsHolder[0] = pos.x;
        argsHolder[1] = pos.y;
        argsHolder[2] = pos.z;
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

    private void updateCameraPosition(MinecraftClient client, Vec3d playerPos, float tickDelta) {
        if (cameraPos == null) {
            cameraPos = new Vec3d(playerPos.x, playerPos.y + 1.0, playerPos.z);
        }

        double distance = cameraPos.distanceTo(playerPos);
        double maxDist = ConfigManager.config.motionCameraMaxDistance;
        if (!Double.isFinite(maxDist) || maxDist <= 0.0) {
            maxDist = 1.0;
        }
        if (distance > maxDist) {
            cameraPos = new Vec3d(playerPos.x, playerPos.y + 1.0, playerPos.z);
            return;
        }

        double smoothFactor = ConfigManager.config.motionCameraSmoothness;
        if (!Double.isFinite(smoothFactor)) {
            smoothFactor = 0.3;
        }
        double dynamicFactor = smoothFactor * (1.0 - Math.exp(-distance / maxDist));
        double horizontalFactor = dynamicFactor;
        double verticalFactor = horizontalFactor + 0.12;

        double targetY = playerPos.y + client.player.getEyeHeight(client.player.getPose());
        double dx = playerPos.x - cameraPos.x;
        double dy = targetY - cameraPos.y;
        double dz = playerPos.z - cameraPos.z;

        cameraPos = new Vec3d(
                cameraPos.x + dx * horizontalFactor,
                cameraPos.y + dy * verticalFactor,
                cameraPos.z + dz * horizontalFactor
        );
    }

    private void updateFreeLookState(MinecraftClient client, PlayerEntity player) {
        if (!ConfigManager.config.freeLookEnabled || !ConfigManager.config.motionCameraEnabled || firstPerson(client)) {
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
        if (!ConfigManager.config.freeLookEnabled || freeLookActive || !ConfigManager.config.motionCameraEnabled || firstPerson(client)) {
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
        double sensitivity = ConfigManager.config.freeLookVerticalSensitivity;
        if (!Double.isFinite(sensitivity)) {
            sensitivity = 1.0;
        }
        double targetOffset = -deltaY * sensitivity * 2.5;

        verticalFollowVelocity = verticalFollowVelocity * 0.78 + targetOffset * 0.22;
        verticalFollowOffset = verticalFollowOffset * 0.82 + verticalFollowVelocity * 0.18;

        float nextPitch = MathHelper.clamp(player.getPitch() + (float) verticalFollowOffset, -90.0f, 90.0f);
        player.setPitch(nextPitch);
        lastFollowY = currentY;
    }

    private void updateMouseInertia(PlayerEntity player) {
        if (!ConfigManager.config.motionCameraYawInertiaEnabled || !ConfigManager.config.motionCameraEnabled || firstPerson(MinecraftClient.getInstance()) || freeLookActive) {
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
        float response = (float) ConfigManager.config.motionCameraYawInertia;
        if (!Float.isFinite(response)) {
            response = 0.15f;
        }

        // Use a continuous filter instead of a hard stop threshold so mouse motion feels natural.
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

    public void resetCameraPosition(Vec3d position) {
        cameraPos = position;
    }

    private void resetTransientState() {
        freeLookActive = false;
        lastFollowY = null;
        lastPlayerYaw = null;
        lastPlayerPitch = null;
        inertiaYawVelocity = 0f;
        inertiaPitchVelocity = 0f;
        verticalFollowOffset = 0.0;
        verticalFollowVelocity = 0.0;
    }
}
