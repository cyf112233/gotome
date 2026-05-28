package byd.cxkcxkckx.gotome.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;

public class GotomeClient {
    public static KeyBinding motionCameraKey;
    public static KeyBinding freeLookKey;
    public static KeyBinding openConfigKey;
    public static KeyBinding viewLockKey;
    public static final CameraController cameraController = new CameraController();
    private static Perspective lastPerspective = null;
    private static boolean lastWorldLoaded = false;

    private static final String GOTOME_CATEGORY = "category.gotome";

    public static void init() {
        motionCameraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.motion_camera",
                InputUtil.Type.KEYSYM,
                ConfigManager.config.motionCameraKey,
                GOTOME_CATEGORY
        ));
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.freelook",
                InputUtil.Type.KEYSYM,
                ConfigManager.config.freeLookKey,
                GOTOME_CATEGORY
        ));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.open_config",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                GOTOME_CATEGORY
        ));
        viewLockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.view_lock",
                InputUtil.Type.KEYSYM,
                ConfigManager.config.viewLockKey,
                GOTOME_CATEGORY
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