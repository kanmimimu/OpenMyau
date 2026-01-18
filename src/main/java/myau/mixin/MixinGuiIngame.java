package myau.mixin;

import myau.Myau;
import myau.module.modules.AutoBlockIn;
import myau.module.modules.Scaffold;
import myau.util.DomainUtil;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SideOnly(Side.CLIENT)
@Mixin(value = {GuiIngame.class}, priority = 9999)
public abstract class MixinGuiIngame {
    @Redirect(
            method = {"updateTick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack updateTick(InventoryPlayer inventoryPlayer) {
        Scaffold scaffold = (Scaffold) Myau.moduleManager.modules.get(Scaffold.class);
        if (scaffold.isEnabled() && scaffold.itemSpoof.getValue()) {
            int slot = scaffold.getSlot();
            if (slot >= 0) {
                return inventoryPlayer.getStackInSlot(slot);
            }
        }
        AutoBlockIn autoBlockIn = (AutoBlockIn) Myau.moduleManager.modules.get(AutoBlockIn.class);
        if (autoBlockIn.itemSpoof.getValue() && autoBlockIn.isEnabled()) {
            int slot = autoBlockIn.getSlot();
            if (slot >= 0) {
                return inventoryPlayer.getStackInSlot(slot);
            }
        }
        return inventoryPlayer.getCurrentItem();
    }

    @Redirect(
            method = {"renderScoreboard"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScoreObjective;getDisplayName()Ljava/lang/String;"
            )
    )
    private String redirectDisplayName(ScoreObjective objective) {
        String name = objective.getDisplayName();
        if (DomainUtil.isEnabled() && DomainUtil.containsDomain(name)) {
            return DomainUtil.replaceDomain(name);
        }
        return name;
    }

    @Redirect(
            method = {"renderScoreboard"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;formatPlayerName(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private String redirectFormatPlayerName(Team team, String playerName) {
        String formatted = ScorePlayerTeam.formatPlayerName(team, playerName);
        if (DomainUtil.isEnabled() && DomainUtil.containsDomain(formatted)) {
            return DomainUtil.replaceDomain(formatted);
        }
        return formatted;
    }
}
