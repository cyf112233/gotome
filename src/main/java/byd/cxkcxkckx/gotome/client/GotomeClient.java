package byd.cxkcxkckx.gotome.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Identifier;

public class GotomeClient {
    public static KeyBinding motionCameraKey;
    public static KeyBinding freeLookKey;
    public static KeyBinding openConfigKey;
    public static KeyBinding viewLockKey;
    public static final CameraController cameraController = new CameraController();
    private static Perspective lastPerspective = null;
    private static boolean lastWorldLoaded = false;

    private static final Category GOTOME_CATEGORY = Category.create(Identifier.of("category.gotome"));

    public static void init() {
        motionCameraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.motion_camera",
                GOTOME_CATEGORY,
                ConfigManager.config.motionCameraKey,
                null
        ));
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.freelook",
                GOTOME_CATEGORY,
                ConfigManager.config.freeLookKey,
                null
        ));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.open_config",
                GOTOME_CATEGORY,
                net.minecraft.client.util.InputUtil.UNKNOWN_KEY.getCode(),
                null
        ));
        viewLockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gotome.view_lock",
                GOTOME_CATEGORY,
                ConfigManager.config.viewLockKey,
                null
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