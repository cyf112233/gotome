package byd.cxkcxkckx.gotome.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.resources.ResourceLocation;

public class GotomeClient {
    public static KeyMapping motionCameraKey;
    public static KeyMapping freeLookKey;
    public static KeyMapping openConfigKey;
    public static KeyMapping viewLockKey;
    public static final CameraController cameraController = new CameraController();
    private static Options.CameraType lastCameraType = null;
    private static boolean lastWorldLoaded = false;

    private static final KeyMapping.Category GOTOME_CATEGORY = KeyMapping.Category.create(ResourceLocation.fromNamespaceAndPath("gotome", "category"));

    public static void init() {
        motionCameraKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gotome.motion_camera",
                InputConstants.Type.KEYSYM,
                ConfigManager.config.motionCameraKey,
                GOTOME_CATEGORY
        ));
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gotome.freelook",
                InputConstants.Type.KEYSYM,
                ConfigManager.config.freeLookKey,
                GOTOME_CATEGORY
        ));
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gotome.open_config",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                GOTOME_CATEGORY
        ));
        viewLockKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gotome.view_lock",
                InputConstants.Type.KEYSYM,
                ConfigManager.config.viewLockKey,
                GOTOME_CATEGORY
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (motionCameraKey.consumeClick()) {
                boolean wasEnabled = ConfigManager.config.motionCameraEnabled;
                ConfigManager.config.motionCameraEnabled = !ConfigManager.config.motionCameraEnabled;
                if (!wasEnabled && ConfigManager.config.motionCameraEnabled && client.player != null) {
                    client.player.setYRot(client.player.getYRot() + 360f);
                }
            }
            while (freeLookKey.consumeClick()) {
                ConfigManager.config.freeLookEnabled = !ConfigManager.config.freeLookEnabled;
            }
            while (openConfigKey.consumeClick()) {
                Minecraft.getInstance().setScreen(ConfigScreen.create(null));
            }
            while (viewLockKey.consumeClick()) {
                ConfigManager.config.viewLockEnabled = !ConfigManager.config.viewLockEnabled;
            }

            cameraController.tick(client);

            Options.CameraType currentCameraType = client.options.getCameraType();
            boolean worldLoaded = client.level != null;
            if (client.player != null && ConfigManager.config.motionCameraEnabled && !currentCameraType.isFirstPerson()) {
                if ((lastCameraType != null && lastCameraType.isFirstPerson() && !currentCameraType.isFirstPerson()) || (!lastWorldLoaded && worldLoaded)) {
                    client.player.setYRot(client.player.getYRot());
                }
            }
            lastCameraType = currentCameraType;
            lastWorldLoaded = worldLoaded;
        });
    }
}
