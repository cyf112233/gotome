package byd.cxkcxkckx.gotome.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.math.Vec3d;
import byd.cxkcxkckx.gotome.client.ConfigManager;

public class FreeLook {
    public boolean enabled = false;
    public int keyCode = GLFW.GLFW_KEY_LEFT_ALT;
    public float sensitivity = 1.0f;
    public boolean invertY = false;
    public boolean freelookActive = false;
    public float cameraYaw = 0.0f;
    public float cameraPitch = 0.0f;
    public float turnSpeed = 2.0f; // degrees per tick, can be adjusted
    private Double lastY = null;

    public void onTick() {
        if (!enabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Get player velocity
        Vec3d velocity = client.player.getVelocity();
        // Calculate player's facing direction (yaw)
        float yawRad = (float) Math.toRadians(client.player.getYaw());
        Vec3d forward = new Vec3d(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vec3d left = new Vec3d(-forward.z, 0, forward.x);
        double sideSpeed = velocity.dotProduct(left);
        double threshold = 0.01;
        if (sideSpeed > threshold) {
            client.player.setYaw(client.player.getYaw() + (float)(turnSpeed * sideSpeed));
        } else if (sideSpeed < -threshold) {
            client.player.setYaw(client.player.getYaw() + (float)(turnSpeed * sideSpeed));
        }

        boolean keyDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), keyCode);
        if (keyDown && !freelookActive) {
            freelookActive = true;
            cameraYaw = client.player.getYaw();
            cameraPitch = client.player.getPitch();
        } else if (!keyDown && freelookActive) {
            freelookActive = false;
        }
        if (freelookActive) {
            // 这里应有鼠标监听，后续在Screen里实现
        }

        // Vertical follow
        double currentY = client.player.getY();
        if (lastY == null) lastY = currentY;
        double deltaY = currentY - lastY;
        if (Math.abs(deltaY) > 0.2) {
            client.player.setPitch(client.player.getPitch() - (float)(ConfigManager.config.freeLookVerticalSensitivity * deltaY));
        }
        lastY = currentY;
    }
} 