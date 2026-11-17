package com.bedrockgui.mixin;

import com.bedrockgui.client.BedrockSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true, require = 0)
    private Screen bedrockgui$replaceOptions(Screen screen) {
        if (screen instanceof OptionsScreen && !BedrockSettingsScreen.allowVanilla) {
            Minecraft mc = (Minecraft) (Object) this;
            return new BedrockSettingsScreen(mc.screen);
        }
        return screen;
    }
}
