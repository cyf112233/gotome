package byd.cxkcxkckx.gotome.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;

public class GotomeClient {
    public static KeyBinding motionCameraKey;
    public static KeyBinding freeLookKey;
    public static KeyBinding openConfigKey;
    public static KeyBinding viewLockKey;
    public static final CameraController cameraController = new CameraController();
    private static Perspective lastPerspective = null;
    private static boolean lastWorldLoaded = false;

    public static void init() {
        motionCameraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.motion_camera",
                ConfigManager.config.motionCameraKey,
                "category.gotome"
        ));
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.freelook",
                ConfigManager.config.freeLookKey,
                "category.gotome"
        ));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.open_config",
                net.minecraft.client.util.InputUtil.UNKNOWN_KEY.getCode(),
                "category.gotome"
        ));
        viewLockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.view_lock",
                ConfigManager.config.viewLockKey,
                "category.gotome"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (motionCameraKey.wasPressed()) {
                boolean wasEnabled = ConfigManager.config.motionCameraEnabled;
                ConfigManager.config.motionCameraEnabled = !ConfigManager.config.motionCameraEnabled;
                if (!wasEnabled && ConfigManager.config.motionCameraEnabled && client.player != null) {
                    client.player.setYaw(client.player.getYaw() + 360f);
                }
            }
            while (freeLookKey.wasPressed()) {
                ConfigManager.config.freeLookEnabled = !ConfigManager.config.freeLookEnabled;
            }
            while (openConfigKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(ConfigScreen.create(null));
            }
            while (viewLockKey.wasPressed()) {
                ConfigManager.config.viewLockEnabled = !ConfigManager.config.viewLockEnabled;
            }

            cameraController.tick(client);

            Perspective currentPerspective = client.options.getPerspective();
            boolean worldLoaded = client.world != null;
            if (client.player != null && ConfigManager.config.motionCameraEnabled && !currentPerspective.isFirstPerson()) {
                if ((lastPerspective != null && lastPerspective.isFirstPerson() && !currentPerspective.isFirstPerson()) || (!lastWorldLoaded && worldLoaded)) {
                    client.player.setYaw(client.player.getYaw());
                }
            }
            lastPerspective = currentPerspective;
            lastWorldLoaded = worldLoaded;
        });
    }
}