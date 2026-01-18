package myau.module.modules;

import myau.Myau;
import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.FloatProperty;
import myau.property.properties.IntProperty;
import myau.property.properties.ModeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;

public class Animations extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final ModeProperty blockingModeValue = new ModeProperty(
            "blocking-mode", 0,
            new String[]{
                    "1.7", "1.8", "Akrien", "Avatar", "ETB", "Exhibition", "Dortware", "Grim", "Plat",
                    "Push", "Reverse", "Shield", "SigmaNew", "SigmaOld", "Slide", "SlideDown", "HSlide",
                    "Moon", "Swong", "VisionFX", "Swank", "Jello", "Rotate", "Liquid", "Leaked", "Fall",
                    "Yeet", "Yeet2", "None"
            }
    );

    public final FloatProperty itemPosXValue = new FloatProperty("item-pos-x", 0F, -1.0F, 1.0F);
    public final FloatProperty itemPosYValue = new FloatProperty("item-pos-y", 0F, -1.0F, 1.0F);
    public final FloatProperty itemPosZValue = new FloatProperty("item-pos-z", 0F, -1.0F, 1.0F);
    public final IntProperty itemScaleValue = new IntProperty("item-scale", 100, 0, 100);

    public final FloatProperty swingSpeedValue = new FloatProperty("swing-speed", 1f, 0.5f, 5.0f);
    public final FloatProperty blockSwingSpeed = new FloatProperty("block-swing-speed", 1f, 0.5f, 5.0f);

    public final FloatProperty blockItemPosX = new FloatProperty("block-item-pos-x", 0F, -1.0F, 1.0F);
    public final FloatProperty blockItemPosY = new FloatProperty("block-item-pos-y", 0F, -1.0F, 1.0F);
    public final FloatProperty blockItemPosZ = new FloatProperty("block-item-pos-z", 0F, -1.0F, 1.0F);

    public final BooleanProperty fluxAnimation = new BooleanProperty("flux-swing", false);
    public final BooleanProperty anythingBlock = new BooleanProperty("anything-block", false);

    public final BooleanProperty fakeBlock = new BooleanProperty("fake-block", false);
    public final BooleanProperty fakeBlockOnlySwing = new BooleanProperty("fake-block-only-swing", false, fakeBlock::getValue);

    public Animations() {
        super("Animations", true, true);
    }

    @Override
    public String[] getSuffix() {
        return new String[]{blockingModeValue.getModeString()};
    }

    public boolean isFakeBlockActive() {
        if (!this.isEnabled() || !fakeBlock.getValue()) return false;
        if (mc.thePlayer == null) return false;
        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) return false;
        return !fakeBlockOnlySwing.getValue() || mc.thePlayer.isSwingInProgress;
    }

    public boolean isBlockingForRender() {
        if (mc.thePlayer == null) return false;
        KillAura killAura = (KillAura) Myau.moduleManager.modules.get(KillAura.class);
        boolean killAuraBlocking = killAura != null && killAura.isEnabled() && killAura.isBlocking();
        return mc.thePlayer.isBlocking() || killAuraBlocking || isFakeBlockActive();
    }

    public float getEffectiveSwingSpeed() {
        return isBlockingForRender() ? blockSwingSpeed.getValue() : swingSpeedValue.getValue();
    }

    public float getCustomItemPosX() {
        return isBlockingForRender() ? blockItemPosX.getValue() : itemPosXValue.getValue();
    }

    public float getCustomItemPosY() {
        return isBlockingForRender() ? blockItemPosY.getValue() : itemPosYValue.getValue();
    }

    public float getCustomItemPosZ() {
        return isBlockingForRender() ? blockItemPosZ.getValue() : itemPosZValue.getValue();
    }
}
