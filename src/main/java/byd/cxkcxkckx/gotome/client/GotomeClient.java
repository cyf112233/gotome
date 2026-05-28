package byd.cxkcxkckx.gotome.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

public class GotomeClient {
    public static final CameraController cameraController = new CameraController();
    private static boolean lastWorldLoaded = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            cameraController.tick(client);

            boolean worldLoaded = client.level != null;
            if (client.player != null && ConfigManager.config.motionCameraEnabled && !client.options.getCameraType().isFirstPerson()) {
                if (!lastWorldLoaded && worldLoaded) {
                    client.player.setYRot(client.player.getYRot());
                }
            }
            lastWorldLoaded = worldLoaded;
        });
    }
}
