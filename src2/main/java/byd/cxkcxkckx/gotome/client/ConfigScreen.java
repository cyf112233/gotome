package byd.cxkcxkckx.gotome.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
    private Screen parent;
    private SliderWidget smoothnessSlider;
    private SliderWidget maxDistanceSlider;
    private ButtonWidget motionCameraEnableButton;
    private ButtonWidget freeLookEnableButton;
    private ButtonWidget freeLookInvertYCheckbox;
    private SliderWidget freeLookSensitivitySlider;
    private ButtonWidget disableFirstPersButton;
    private ButtonWidget motionCameraYawInertiaEnableButton;
    private SliderWidget motionCameraYawInertiaSlider;

    public ConfigScreen(Screen parent) {
        super(Text.literal("运动相机与自由视角设置"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int col1x = this.width / 2 - 210;
        int col2x = this.width / 2 + 10;
        int yStart = this.height / 4;
        int y1 = yStart + 24; // 留出标题高度
        int y2 = yStart + 24;
        // 运动相机分组内容
        motionCameraEnableButton = ButtonWidget.builder(Text.literal((ConfigManager.config.motionCameraEnabled ? "§a■ " : "§c■ ") + "启用运动相机功能"), btn -> {
            boolean wasEnabled = ConfigManager.config.motionCameraEnabled;
            ConfigManager.config.motionCameraEnabled = !ConfigManager.config.motionCameraEnabled;
            btn.setMessage(Text.literal((ConfigManager.config.motionCameraEnabled ? "§a■ " : "§c■ ") + "启用运动相机功能"));
            if (!wasEnabled && ConfigManager.config.motionCameraEnabled && MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + 360f);
            }
        }).dimensions(col1x, y1, 200, 20).build();
        this.addDrawableChild(motionCameraEnableButton);
        y1 += 24;
        motionCameraYawInertiaEnableButton = ButtonWidget.builder(Text.literal((ConfigManager.config.motionCameraYawInertiaEnabled ? "[√] " : "[  ] ") + "启用视角惯性"), btn -> {
            ConfigManager.config.motionCameraYawInertiaEnabled = !ConfigManager.config.motionCameraYawInertiaEnabled;
            btn.setMessage(Text.literal((ConfigManager.config.motionCameraYawInertiaEnabled ? "[√] " : "[  ] ") + "启用视角惯性"));
        }).dimensions(col1x, y1, 200, 20).build();
        this.addDrawableChild(motionCameraYawInertiaEnableButton);
        y1 += 24;
        motionCameraYawInertiaSlider = new SliderWidget(col1x, y1, 200, 20, Text.literal("视角惯性强度: "), (ConfigManager.config.motionCameraYawInertia - 0.0) / (0.5 - 0.0)) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("视角惯性强度: " + String.format("%.2f", getValue())));
            }
            @Override
            protected void applyValue() {
                ConfigManager.config.motionCameraYawInertia = 0.0 + (0.5 - 0.0) * this.value;
                this.updateMessage();
            }
            private double getValue() {
                return 0.0 + (0.5 - 0.0) * this.value;
            }
        };
        this.addDrawableChild(motionCameraYawInertiaSlider);
        y1 += 24;
        smoothnessSlider = new SliderWidget(col1x, y1, 200, 20, Text.literal("运动相机平滑度: "), (ConfigManager.config.motionCameraSmoothness - 0.1) / (0.95 - 0.1)) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("运动相机平滑度: " + String.format("%.2f", getValue())));
            }
            @Override
            protected void applyValue() {
                ConfigManager.config.motionCameraSmoothness = 0.1 + (0.95 - 0.1) * this.value;
                this.updateMessage();
            }
            private double getValue() {
                return 0.1 + (0.95 - 0.1) * this.value;
            }
        };
        this.addDrawableChild(smoothnessSlider);
        y1 += 24;
        maxDistanceSlider = new SliderWidget(col1x, y1, 200, 20, Text.literal("运动相机最大跟随距离: "), (ConfigManager.config.motionCameraMaxDistance - 1.0) / (50.0 - 1.0)) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("运动相机最大跟随距离: " + String.format("%.1f", getValue())));
            }
            @Override
            protected void applyValue() {
                ConfigManager.config.motionCameraMaxDistance = 1.0 + (50.0 - 1.0) * this.value;
                this.updateMessage();
            }
            private double getValue() {
                return 1.0 + (50.0 - 1.0) * this.value;
            }
        };
        this.addDrawableChild(maxDistanceSlider);
        y1 += 24;
        disableFirstPersButton = ButtonWidget.builder(Text.literal((ConfigManager.config.motionCameraDisableFirstPers ? "[√] " : "[  ] ") + "第一人称下禁用运动相机"), btn -> {
            ConfigManager.config.motionCameraDisableFirstPers = !ConfigManager.config.motionCameraDisableFirstPers;
            btn.setMessage(Text.literal((ConfigManager.config.motionCameraDisableFirstPers ? "[√] " : "[  ] ") + "第一人称下禁用运动相机"));
        }).dimensions(col1x, y1, 200, 20).build();
        this.addDrawableChild(disableFirstPersButton);
        y1 += 24;
        // 自由视角分组内容
        y2 += 24;
        freeLookEnableButton = ButtonWidget.builder(Text.literal((ConfigManager.config.freeLookEnabled ? "[√] " : "[  ] ") + "启用自由视角功能"), btn -> {
            ConfigManager.config.freeLookEnabled = !ConfigManager.config.freeLookEnabled;
            btn.setMessage(Text.literal((ConfigManager.config.freeLookEnabled ? "[√] " : "[  ] ") + "启用自由视角功能"));
        }).dimensions(col2x, y2, 200, 20).build();
        this.addDrawableChild(freeLookEnableButton);
        y2 += 24;
        // 新增视角锁定按钮，移到右侧分组
        ButtonWidget viewLockButton = ButtonWidget.builder(Text.literal((ConfigManager.config.viewLockEnabled ? "§a■ " : "§c■ ") + "视角锁定"), btn -> {
            ConfigManager.config.viewLockEnabled = !ConfigManager.config.viewLockEnabled;
            btn.setMessage(Text.literal((ConfigManager.config.viewLockEnabled ? "§a■ " : "§c■ ") + "视角锁定"));
        }).dimensions(col2x, y2, 200, 20).build();
        this.addDrawableChild(viewLockButton);
        y2 += 24;
        freeLookSensitivitySlider = new SliderWidget(col2x, y2, 200, 20, Text.literal("自由视角水平灵敏度: "), (ConfigManager.config.freeLookSensitivity - 0.1f) / (2.0f - 0.1f)) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("自由视角水平灵敏度: " + String.format("%.2f", getValue())));
            }
            @Override
            protected void applyValue() {
                ConfigManager.config.freeLookSensitivity = 0.1f + (2.0f - 0.1f) * (float)this.value;
                this.updateMessage();
            }
            private float getValue() {
                return 0.1f + (2.0f - 0.1f) * (float)this.value;
            }
        };
        this.addDrawableChild(freeLookSensitivitySlider);
        y2 += 24;
        // Add vertical sensitivity slider for FreeLook
        SliderWidget freeLookVerticalSensitivitySlider = new SliderWidget(col2x, y2, 200, 20, Text.literal("自由视角垂直灵敏度: "), (ConfigManager.config.freeLookVerticalSensitivity - 0.1f) / (2.0f - 0.1f)) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("自由视角垂直灵敏度: " + String.format("%.2f", getValue())));
            }
            @Override
            protected void applyValue() {
                ConfigManager.config.freeLookVerticalSensitivity = 0.1f + (2.0f - 0.1f) * (float)this.value;
                this.updateMessage();
            }
            private float getValue() {
                return 0.1f + (2.0f - 0.1f) * (float)this.value;
            }
        };
        this.addDrawableChild(freeLookVerticalSensitivitySlider);
        y2 += 24;
        // Remove freeLookInvertYButton (Y-axis invert) from the menu
        y2 += 32;
        // 保存和返回按钮放在底部中间
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存并返回"), btn -> {
            ConfigManager.save();
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build());
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        this.renderBackground(context);
        context.getMatrices().pop();
        // 绘制分组标题
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§l运动相机设置"), this.width / 2 - 110, this.height / 4, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§l自由视角设置"), this.width / 2 + 110, this.height / 4, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
} 