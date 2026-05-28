package byd.cxkcxkckx.gotome.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public final class ConfigManager {
    private static final String CONFIG_PATH = "config/gotome_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {}

    public static ModConfig config = new ModConfig();

    public static void load() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            if (loaded != null) {
                config = loaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        File file = new File(CONFIG_PATH);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
