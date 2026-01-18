package myau.mixin;

import myau.Myau;
import myau.module.modules.Animations;
import myau.module.modules.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityPlayerSP, float partialTicks);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    private Animations animations;

    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void renderItemInFirstPerson(float partialTicks, CallbackInfo ci) {
        if (Myau.moduleManager == null) return;
        if (animations == null) {
            animations = (Animations) Myau.moduleManager.modules.get(Animations.class);
        }
        if (animations == null || !animations.isEnabled()) return;

        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP player = mc.thePlayer;
        float f1 = player.getSwingProgress(partialTicks);
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(player);
        this.rotateWithPlayerRotations(player, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            KillAura killAura = (KillAura) Myau.moduleManager.modules.get(KillAura.class);
            boolean isBlocking = player.isUsingItem() || animations.isFakeBlockActive() ||
                    (itemToRender.getItem() instanceof ItemSword && killAura != null && killAura.isEnabled() && killAura.isBlocking());

            if (isBlocking && itemToRender.getItem() instanceof ItemSword) {
                switch (animations.blockingModeValue.getValue()) {
                    case 0: // 1.7
                        transformFirstPersonItem(f, f1);
                        doBlockTransformations();
                        break;
                    case 1: // 1.8
                        transformFirstPersonItem(f, f1);
                        doBlockTransformations();
                        GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                        break;
                    case 2: // Akrien
                        transformFirstPersonItem(f1, 0.0F);
                        doBlockTransformations();
                        break;
                    case 3: // Avatar
                        avatar(f1);
                        doBlockTransformations();
                        break;
                    case 4: // ETB
                        etb(f, f1);
                        doBlockTransformations();
                        break;
                    case 5: // Exhibition
                        transformFirstPersonItem(f, 0.83F);
                        float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                        GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                        GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                        doBlockTransformations();
                        break;
                    case 6: // Dortware
                        float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                        transformFirstPersonItem(f / 2.5F, 0.0f);
                        GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                        break;
                    case 7: // Grim
                        float swingProgress = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        doItemRenderGLTranslate();
                        doItemRenderGLScale();
                        GlStateManager.translate(0.0F, f * -0.4F, 0.0F);
                        GlStateManager.translate(0.0F, -0.15F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-18.0F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
                        GlStateManager.rotate(-swingProgress * 27.0F, 1.0F, 0.0F, 0.0F);
                        doBlockTransformations();
                        break;
                    case 8: // Plat
                        doItemRenderGLTranslate();
                        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
                        GlStateManager.translate(0.0F, 0.1F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        float var12 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(var12 * -60.0F + 20F, 1.0F, 0.0F, 0.0F);
                        doItemRenderGLScale();
                        doBlockTransformations();
                        break;
                    case 9: // Push
                        push(f1);
                        doBlockTransformations();
                        break;
                    case 10: // Reverse
                        transformFirstPersonItem(f1, f1);
                        doBlockTransformations();
                        break;
                    case 11: // Shield
                        jello(f1);
                        doBlockTransformations();
                        break;
                    case 12: // SigmaNew
                        doItemRenderGLTranslate();
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        float var11 = MathHelper.sin(f1 * f1 * 3.1415927F);
                        float var12b = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(var12b * -5.0F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(var12b * 25.0F, 0.0F, 1.0F, 0.0F);
                        doItemRenderGLScale();
                        doBlockTransformations();
                        break;
                    case 13: // SigmaOld
                        sigmaOld(f);
                        float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(-var15 * 55.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                        GlStateManager.rotate(-var15 * 45.0F, 1.0F, var15 / 2.0F, -0.0F);
                        doBlockTransformations();
                        GL11.glTranslated(1.2D, 0.3D, 0.5D);
                        GL11.glTranslatef(-1.0F, mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                        GlStateManager.scale(1.2F, 1.2F, 1.2F);
                        break;
                    case 14: // Slide
                        slide(f1);
                        doBlockTransformations();
                        break;
                    case 15: // SlideDown
                        transformFirstPersonItem(0.2F, f1);
                        doBlockTransformations();
                        break;
                    case 16: // HSlide
                        transformFirstPersonItem(f1 != 0 ? Math.max(1 - (f1 * 2), 0) * 0.7F : 0, 1F);
                        doBlockTransformations();
                        break;
                    case 17: // Moon
                        transformFirstPersonItem(f, f1);
                        doBlockTransformations();
                        break;
                    case 18: // Swong
                        transformFirstPersonItem(f / 2.0F, 0.0F);
                        GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F) * 40.0F / 2.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F, 9.0F);
                        GlStateManager.rotate(-MathHelper.sqrt_float(f1) * 30.0F, 1.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F);
                        doBlockTransformations();
                        break;
                    case 19: // VisionFX
                        continuity(f1);
                        doBlockTransformations();
                        break;
                    case 20: // Swank
                        GL11.glTranslated(-0.1, 0.15, 0.0);
                        transformFirstPersonItem(f / 0.15f, f1);
                        float rot = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                        GlStateManager.rotate(rot * 30.0f, 2.0f, -rot, 9.0f);
                        GlStateManager.rotate(rot * 35.0f, 1.0f, -rot, -0.0f);
                        doBlockTransformations();
                        break;
                    case 21: // Jello
                        transformFirstPersonItem(0.0f, 0.0f);
                        doBlockTransformations();
                        int alpha = (int) Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                        GlStateManager.translate(0.3f, -0.0f, 0.4f);
                        GlStateManager.translate(0.0f, 0.5f, 0.0f);
                        GlStateManager.rotate(90.0f, 1.0f, 0.0f, -1.0f);
                        GlStateManager.translate(0.6f, 0.5f, 0.0f);
                        GlStateManager.rotate(-90.0f, 1.0f, 0.0f, -1.0f);
                        GlStateManager.rotate(-10.0f, 1.0f, 0.0f, -1.0f);
                        GlStateManager.rotate(player.isSwingInProgress ? (-alpha / 5.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                        break;
                    case 22: // Rotate
                        rotateSword(f1);
                        break;
                    case 23: // Liquid
                        transformFirstPersonItem(f + 0.1F, f1);
                        doBlockTransformations();
                        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                        break;
                    case 24: // Leaked
                        transformFirstPersonItem(f, f1);
                        doBlockTransformations();
                        break;
                    case 25: // Fall
                        doItemRenderGLTranslate();
                        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        doItemRenderGLScale();
                        doBlockTransformations();
                        break;
                    case 26: // Yeet
                        doItemRenderGLTranslate();
                        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        float var11y = MathHelper.sin(f1 * f1 * 3.1415927F);
                        float var12y = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(var12y * -40.0F + 10F, 1.0F, 0.0F, 0.0F);
                        doItemRenderGLScale();
                        doBlockTransformations();
                        break;
                    case 27: // Yeet2
                        doItemRenderGLTranslate();
                        GlStateManager.translate(0.0F, f * -0.8F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        float var12y2 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(var12y2 * -20.0F - 9.5F, 1.0F, 0.0F, 0.0F);
                        doItemRenderGLScale();
                        doBlockTransformations();
                        break;
                    default: // None (28)
                        transformFirstPersonItem(0F, 0F);
                        doBlockTransformations();
                        break;
                }
                this.renderItem(player, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else {
                return;
            }
        } else if (!player.isInvisible()) {
            this.renderPlayerArm(player, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        ci.cancel();
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void doItemRenderGLTranslate() {
        GlStateManager.translate(0.56F + animations.getCustomItemPosX(), -0.52F + animations.getCustomItemPosY(), -0.71999997F + animations.getCustomItemPosZ());
    }

    private void doItemRenderGLScale() {
        float scale = animations.itemScaleValue.getValue() / 100.0F * 0.4F;
        GlStateManager.scale(scale, scale, scale);
    }

    private void avatar(float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f2 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void etb(float equipProgress, float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        doItemRenderGLScale();
    }

    private void push(float idc) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.1F * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * 3.1415927F);
        GlStateManager.rotate(var3 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void jello(float var12) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(48.57F, 0.0F, 0.24F, 0.14F);
        float var13 = MathHelper.sin(var12 * var12 * 3.1415927F);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * 3.1415927F);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void slide(float var9) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var11 = MathHelper.sin(var9 * var9 * 3.1415927F);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * 3.1415927F);
        GlStateManager.rotate(var12 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void sigmaOld(float f) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void continuity(float var10) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var12 = -MathHelper.sin(var10 * var10 * 3.1415927F);
        float var13 = MathHelper.cos(MathHelper.sqrt_float(var10) * 3.1415927F);
        float var14 = MathHelper.abs(MathHelper.sqrt_float(0.1F) * 3.1415927F);
        GlStateManager.rotate(var12 * var14 * 30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var13 * 20.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void rotateSword(float f1) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        doBlockTransformations();
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(MathHelper.sqrt_float(f1) * 10.0F * 40.0F, 1.0F, -0.0F, 2.0F);
    }
}
