package com.itatiers.mixin.client;

import com.itatiers.ItaTiersClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class ModifyNameTagsClientMixin {
    @Shadow
    public abstract String getNameForScoreboard();

    @ModifyReturnValue(at = @At("RETURN"), method = "getDisplayName")
    private Text getDisplayName(Text originalNameText) {
        if (ItaTiersClient.toggleMod)
            return ItaTiersClient.getModifiedNametag(this.getNameForScoreboard(), originalNameText);
        return originalNameText;
    }
}