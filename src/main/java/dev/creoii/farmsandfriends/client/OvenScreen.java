package dev.creoii.farmsandfriends.client;

import dev.creoii.farmsandfriends.menu.OvenMenu;
import dev.creoii.farmsandfriends.util.FarmsAndFriendsSearchRecipeBookCategories;
import dev.creoii.greatbigworld.GreatBigWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OvenScreen extends AbstractRecipeBookScreen<OvenMenu> {
    private static final Identifier LIT_PROGRESS_LEFT_SPRITE = Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "container/oven/lit_progress_left");
    private static final Identifier LIT_PROGRESS_RIGHT_SPRITE = Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "container/oven/lit_progress_right");
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "container/oven/burn_progress");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "textures/gui/container/oven.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.cookable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(new RecipeBookComponent.TabInfo(FarmsAndFriendsSearchRecipeBookCategories.OVEN));

    public OvenScreen(OvenMenu ovenMenu, Inventory inventory, Component component) {
        super(ovenMenu, new OvenRecipeBookComponent(ovenMenu, FILTER_NAME, TABS), inventory, component);
    }

    public void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(leftPos + 6, height / 2 - 60);
    }

    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        int k = leftPos;
        int l = topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, k, l, 0f, 0f, imageWidth, imageHeight, 256, 256);
        if (menu.isLit()) {
            int n = Mth.ceil(menu.getLitProgress() * 15f);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_LEFT_SPRITE, 19, 15, 0, 15 - n, k + 27, l + 54 + 15 - n, 19, n);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_RIGHT_SPRITE, 19, 15, 0, 15 - n, k + 67, l + 54 + 15 - n, 19, n);
        }

        int n = Mth.ceil(menu.getBurnProgress() * 24f);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, k + 89, l + 34, n, 16);
    }
}
