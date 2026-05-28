package byd.cxkcxkckx.gotome.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ConfigScreen {
    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("运动相机与自由视角设置"));

        final boolean wasMotionCameraEnabled = ConfigManager.config.motionCameraEnabled;
        builder.setSavingRunnable(() -> {
            ConfigManager.save();
            if (!wasMotionCameraEnabled && ConfigManager.config.motionCameraEnabled) {
                GotomeClient.cameraController.resetCameraPosition(net.minecraft.client.MinecraftClient.getInstance());
            }
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory cameraCategory = builder.getOrCreateCategory(Text.literal("运动相机设置"));
        ConfigCategory freeLookCategory = builder.getOrCreateCategory(Text.literal("自由视角设置"));

        cameraCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("启用运动相机功能"), ConfigManager.config.motionCameraEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraEnabled = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("启用视角惯性"), ConfigManager.config.motionCameraYawInertiaEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraYawInertiaEnabled = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startFloatField(Text.literal("视角惯性强度"), (float) ConfigManager.config.motionCameraYawInertia)
                .setDefaultValue(0.15f)
                .setMin(0.0f)
                .setMax(0.5f)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraYawInertia = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startFloatField(Text.literal("运动相机平滑度"), (float) ConfigManager.config.motionCameraSmoothness)
                .setDefaultValue(0.3f)
                .setMin(0.1f)
                .setMax(0.95f)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraSmoothness = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startFloatField(Text.literal("运动相机最大跟随距离"), (float) ConfigManager.config.motionCameraMaxDistance)
                .setDefaultValue(20.0f)
                .setMin(1.0f)
                .setMax(50.0f)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraMaxDistance = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("第一人称下禁用运动相机"), ConfigManager.config.motionCameraDisableFirstPers)
                .setDefaultValue(true)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraDisableFirstPers = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("启用自由视角功能"), ConfigManager.config.freeLookEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigManager.config.freeLookEnabled = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("视角锁定"), ConfigManager.config.viewLockEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(value -> ConfigManager.config.viewLockEnabled = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startFloatField(Text.literal("自由视角水平灵敏度"), ConfigManager.config.freeLookSensitivity)
                .setDefaultValue(1.0f)
                .setMin(0.1f)
                .setMax(2.0f)
                .setSaveConsumer(value -> ConfigManager.config.freeLookSensitivity = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startFloatField(Text.literal("自由视角垂直灵敏度"), ConfigManager.config.freeLookVerticalSensitivity)
                .setDefaultValue(1.0f)
                .setMin(0.1f)
                .setMax(2.0f)
                .setSaveConsumer(value -> ConfigManager.config.freeLookVerticalSensitivity = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startFloatField(Text.literal("左右移动视角跟随强度"), ConfigManager.config.sideMovementYawStrength)
                .setDefaultValue(2.0f)
                .setMin(0.0f)
                .setMax(10.0f)
                .setSaveConsumer(value -> ConfigManager.config.sideMovementYawStrength = value)
                .build());

        return builder.build();
    }
}