package com.solstice.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.solstice.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** TNT Timer: draws the remaining fuse above every primed TNT (TntRenderer calls super.render at its end). */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "render", at = @At("TAIL"))
    private void solstice$tntTimer(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        var timer = ModuleManager.tnt;
        if (timer == null || !timer.isEnabled() || !(entity instanceof PrimedTnt tnt)) return;
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        String text = timer.label(tnt, partialTick);
        poseStack.pushPose();
        poseStack.translate(0.0, tnt.getBbHeight() + 0.75, 0.0);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.04f, -0.04f, 0.04f);
        Matrix4f pose = poseStack.last().pose();
        font.drawInBatch(text, -font.width(text) / 2f, 0f, timer.color(tnt, partialTick), false, pose, buffer, Font.DisplayMode.SEE_THROUGH, 0x66000000, 0xF000F0);
        poseStack.popPose();
    }
}
