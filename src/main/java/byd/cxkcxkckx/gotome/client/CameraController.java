package byd.cxkcxkckx.gotome.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public final class CameraController {
    private Vec3 cameraPos;
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

    public void tick(Minecraft client) {
        LocalPlayer player = client.player;
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

    public void applyCameraPosition(Minecraft client, Vec3 cameraAnchorPos, float partialTick, Object[] argsHolder) {
        if (client.player == null) return;
        if (!ConfigManager.config.motionCameraEnabled) return;
        if (ConfigManager.config.motionCameraDisableFirstPers && firstPerson(client)) return;

        updateCameraPosition(client, cameraAnchorPos, partialTick);
        Vec3 pos = getCameraPos(client);
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

    public Vec3 getCameraPos(Minecraft client) {
        if (client.player == null) {
            return cameraPos;
        }
        if (firstPerson(client)) {
            return new Vec3(
                    client.player.getX(),
                    client.player.getY() + client.player.getEyeHeight(client.player.getPose()),
                    client.player.getZ()
            );
        }
        return cameraPos;
    }

    private void updateCameraPosition(Minecraft client, Vec3 cameraAnchorPos, float partialTick) {
        if (cameraPos == null) {
            cameraPos = new Vec3(cameraAnchorPos.x, cameraAnchorPos.y + 1.0, cameraAnchorPos.z);
        }

        double distance = cameraPos.distanceTo(cameraAnchorPos);
        double maxDist = Math.max(1.0, ConfigManager.config.motionCameraMaxDistance);
        if (distance > maxDist) {
            cameraPos = new Vec3(cameraAnchorPos.x, cameraAnchorPos.y + 1.0, cameraAnchorPos.z);
            return;
        }

        double smoothFactor = Math.max(0.05, Math.min(ConfigManager.config.motionCameraSmoothness, 0.98));
        double dynamicFactor = smoothFactor * (1.0 - Math.exp(-distance / maxDist));
        double horizontalFactor = Math.max(0.02, Math.min(dynamicFactor, 0.9));
        double verticalFactor = Math.max(0.05, Math.min(horizontalFactor + 0.12, 0.95));

        double targetY = cameraAnchorPos.y + client.player.getEyeHeight(client.player.getPose());
        double dx = cameraAnchorPos.x - cameraPos.x;
        double dy = targetY - cameraPos.y;
        double dz = cameraAnchorPos.z - cameraPos.z;

        cameraPos = new Vec3(
                cameraPos.x + dx * horizontalFactor,
                cameraPos.y + dy * verticalFactor,
                cameraPos.z + dz * horizontalFactor
        );
    }

    private void updateFreeLookState(Minecraft client, LocalPlayer player) {
        if (!ConfigManager.config.freeLookEnabled || !ConfigManager.config.motionCameraEnabled || firstPerson(client)) {
            freeLookActive = false;
            return;
        }

        boolean keyDown = InputConstants.isKeyDown(client.getWindow(), ConfigManager.config.freeLookKey);
        if (keyDown && !freeLookActive) {
            freeLookActive = true;
            freeLookYaw = player.getYRot();
            freeLookPitch = player.getXRot();
        } else if (!keyDown && freeLookActive) {
            freeLookActive = false;
        }
    }

    private void updateMovementFollow(Minecraft client, LocalPlayer player) {
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
        double sensitivity = Math.max(0.1f, ConfigManager.config.freeLookVerticalSensitivity);
        double targetOffset = -deltaY * sensitivity * 2.5;

        verticalFollowVelocity = verticalFollowVelocity * 0.78 + targetOffset * 0.22;
        verticalFollowOffset = verticalFollowOffset * 0.82 + verticalFollowVelocity * 0.18;

        float nextPitch = (float) Math.max(-90.0f, Math.min(player.getXRot() + (float) verticalFollowOffset, 90.0f));
        player.setXRot(nextPitch);
        lastFollowY = currentY;
    }

    private void updateMouseInertia(LocalPlayer player) {
        if (!ConfigManager.config.motionCameraYawInertiaEnabled || !ConfigManager.config.motionCameraEnabled || firstPerson(Minecraft.getInstance()) || freeLookActive) {
            lastPlayerYaw = null;
            lastPlayerPitch = null;
            inertiaYawVelocity = 0f;
            inertiaPitchVelocity = 0f;
            return;
        }

        float currentYaw = player.getYRot();
        float currentPitch = player.getXRot();
        if (lastPlayerYaw == null || lastPlayerPitch == null) {
            lastPlayerYaw = currentYaw;
            lastPlayerPitch = currentPitch;
            return;
        }

        float deltaYaw = currentYaw - lastPlayerYaw;
        float deltaPitch = currentPitch - lastPlayerPitch;
        float response = (float) Math.max(0.02f, Math.min(ConfigManager.config.motionCameraYawInertia, 0.35f));

        inertiaYawVelocity += (deltaYaw - inertiaYawVelocity) * response;
        inertiaPitchVelocity += (deltaPitch - inertiaPitchVelocity) * response;
        inertiaYawVelocity *= 1.0f - response * 0.12f;
        inertiaPitchVelocity *= 1.0f - response * 0.12f;

        float nextYaw = currentYaw + inertiaYawVelocity;
        float nextPitch = (float) Math.max(-90.0f, Math.min(currentPitch + inertiaPitchVelocity, 90.0f));
        player.setYRot(nextYaw);
        player.setXRot(nextPitch);

        lastPlayerYaw = nextYaw;
        lastPlayerPitch = nextPitch;
    }

    private boolean firstPerson(Minecraft client) {
        return client.options.getCameraType().isFirstPerson();
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
