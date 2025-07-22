package byd.cxkcxkckx.gotome.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class GotomeClient {
    public static KeyBinding motionCameraKey;
    public static KeyBinding freeLookKey;
    public static KeyBinding openConfigKey;
    public static KeyBinding viewLockKey;
    public static MotionCamera motionCamera = new MotionCamera();
    public static FreeLook freeLook = new FreeLook();

    public static void init() {
        ConfigManager.load();
        // 注册按键
        motionCameraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.motion_camera",
                org.lwjgl.glfw.GLFW.GLFW_KEY_F6,
                "category.gotome"
        ));
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.freelook",
                org.lwjgl.glfw.GLFW.GLFW_KEY_F8,
                "category.gotome"
        ));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.open_config",
                org.lwjgl.glfw.GLFW.GLFW_KEY_F7,
                "category.gotome"
        ));
        viewLockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.gotome.view_lock",
            ConfigManager.config.viewLockKey,
            "category.gotome"
        ));
        // 监听按键
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (motionCameraKey.wasPressed()) {
                boolean wasEnabled = ConfigManager.config.motionCameraEnabled;
                ConfigManager.config.motionCameraEnabled = !ConfigManager.config.motionCameraEnabled;
                ConfigManager.save();
                if (!wasEnabled && ConfigManager.config.motionCameraEnabled && client.player != null) {
                    client.player.setYaw(client.player.getYaw() + 360f);
                }
            }
            while (freeLookKey.wasPressed()) {
                ConfigManager.config.freeLookEnabled = !ConfigManager.config.freeLookEnabled;
                ConfigManager.save();
            }
            while (openConfigKey.wasPressed()) {
                net.minecraft.client.MinecraftClient.getInstance().setScreen(new ConfigScreen(null));
            }
            while (viewLockKey.wasPressed()) {
                ConfigManager.config.viewLockEnabled = !ConfigManager.config.viewLockEnabled;
                ConfigManager.save();
            }
            freeLook.enabled = ConfigManager.config.freeLookEnabled;
            freeLook.keyCode = ConfigManager.config.freeLookKey;
            freeLook.sensitivity = ConfigManager.config.freeLookSensitivity;
            freeLook.invertY = ConfigManager.config.freeLookInvertY;
            freeLook.onTick();
        });
    }
}
