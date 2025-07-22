package byd.cxkcxkckx.gotome.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private static final String CONFIG_PATH = "config/gotome_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ModConfig config = new ModConfig();

    public static void load() {
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        File file = new File(CONFIG_PATH);
        file.getParentFile().mkdirs();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ModConfig：存储所有可调参数，支持热更和持久化。
     * - motionCameraEnabled：运动相机开关
     * - motionCameraSmoothness：运动相机平滑度
     * - motionCameraMaxDistance：运动相机最大距离
     * - motionCameraDisableFirstPers：第一人称下禁用运动相机
     * - motionCameraKey：运动相机开关按键
     * - freeLookEnabled：自由视角开关
     * - freeLookKey：自由视角激活按键
     * - freeLookSensitivity：自由视角灵敏度
     * - freeLookInvertY：自由视角Y轴反转
     */
    public static class ModConfig {
        public boolean motionCameraEnabled = false;
        public double motionCameraSmoothness = 0.3;
        public double motionCameraMaxDistance = 20.0;
        public boolean motionCameraDisableFirstPers = true;
        public int motionCameraKey = 0x75; // F6

        public boolean freeLookEnabled = false;
        public int freeLookKey = 0x39; // Left Alt
        public float freeLookSensitivity = 1.0f;
        public boolean freeLookInvertY = false;
        public boolean motionCameraYawInertiaEnabled = false;
        public double motionCameraYawInertia = 0.15;
        public float freeLookVerticalSensitivity = 1.0f;
        // 新增：视角锁定开关
        public boolean viewLockEnabled = false;
        // 新增：视角锁定快捷键
        public int viewLockKey = 0x78; // F9
    }
} 