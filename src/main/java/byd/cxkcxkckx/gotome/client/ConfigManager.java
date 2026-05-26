package byd.cxkcxkckx.gotome.client;

import org.lwjgl.glfw.GLFW;

public final class ConfigManager {
    private ConfigManager() {}

    public static final ModConfig config = new ModConfig();

    public static final class ModConfig {
        public boolean motionCameraEnabled = false;
        public double motionCameraSmoothness = 0.3;
        public double motionCameraMaxDistance = 20.0;
        public boolean motionCameraDisableFirstPers = true;
        public int motionCameraKey = GLFW.GLFW_KEY_F6;

        public boolean freeLookEnabled = false;
        public int freeLookKey = GLFW.GLFW_KEY_LEFT_ALT;
        public float freeLookSensitivity = 1.0f;
        public boolean freeLookInvertY = false;
        public boolean motionCameraYawInertiaEnabled = false;
        public double motionCameraYawInertia = 0.15;
        public float freeLookVerticalSensitivity = 1.0f;
        public boolean viewLockEnabled = false;
        public int viewLockKey = GLFW.GLFW_KEY_F9;
    }
}