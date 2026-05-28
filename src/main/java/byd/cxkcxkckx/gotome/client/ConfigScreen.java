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

        builder.setSavingRunnable(ConfigManager::save);

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

        cameraCategory.addEntry(entryBuilder.startDoubleField(Text.literal("视角惯性强度"), ConfigManager.config.motionCameraYawInertia)
                .setDefaultValue(0.15)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraYawInertia = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startDoubleField(Text.literal("运动相机平滑度"), ConfigManager.config.motionCameraSmoothness)
                .setDefaultValue(0.3)
                .setSaveConsumer(value -> ConfigManager.config.motionCameraSmoothness = value)
                .build());

        cameraCategory.addEntry(entryBuilder.startDoubleField(Text.literal("运动相机最大跟随距离"), ConfigManager.config.motionCameraMaxDistance)
                .setDefaultValue(20.0)
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
                .setSaveConsumer(value -> ConfigManager.config.freeLookSensitivity = value)
                .build());

        freeLookCategory.addEntry(entryBuilder.startFloatField(Text.literal("自由视角垂直灵敏度"), ConfigManager.config.freeLookVerticalSensitivity)
                .setDefaultValue(1.0f)
                .setSaveConsumer(value -> ConfigManager.config.freeLookVerticalSensitivity = value)
                .build());

        return builder.build();
    }
}
