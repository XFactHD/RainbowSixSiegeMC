package xfacthd.r6mod.common.data.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.util.render.*;

public abstract class AbstractEffect
{
    protected static final ResourceLocation PROGRESS = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/effect_progress.png");

    protected final ServerPlayerEntity player;
    protected final EnumEffect effect;
    protected final int effectTime;
    protected final long effectStart;
    protected boolean invalid = false;
    protected ResourceLocation icon;

    public AbstractEffect(ServerPlayerEntity player, EnumEffect effect, int effectTime, long effectStart)
    {
        this.player = player;
        this.effect = effect;
        this.effectTime = effectTime;
        this.effectStart = effectStart;
    }

    public final void tick()
    {
        int runTime = (int)(player.world.getGameTime() - effectStart);
        if (effectTime != -1 && runTime > effectTime) { invalidate(); }
        else { handleEffect(runTime); }
    }

    public final void tickClient()
    {
        //noinspection ConstantConditions
        int diff = (int)(Minecraft.getInstance().world.getGameTime() - effectStart);
        if (diff > effectTime) { invalidateClient(); }
    }

    protected abstract void handleEffect(int runTime);

    public abstract void drawEffect(MatrixStack matrix);

    public void drawIcon(MatrixStack matrix, int x, int y)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(getIconLocation());

        Color4i color = isPositive() ? Color4i.BLUE : Color4i.RED;

        RenderSystem.enableBlend();
        TextureDrawer.drawTexture(matrix, x, y, 12, 12, 0, 1, 0, 1, color.packed());
        if (showProgress()) { drawProgress(matrix, x, y, color); }
    }

    private void drawProgress(MatrixStack matrix, int x, int y, Color4i color)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(PROGRESS);

        //noinspection ConstantConditions
        float progress = getEffectProgress(Minecraft.getInstance().world.getGameTime());
        UIRenderHelper.drawProgressCircle(matrix, x, y, 12, 32, 2, 1, progress, color, PROGRESS);
    }

    protected ResourceLocation getIconLocation()
    {
        if (icon == null) { icon = new ResourceLocation(R6Mod.MODID, "textures/gui/symbols/effect_" + effect.getName() + ".png"); }
        return icon;
    }

    public float getEffectProgress(long worldTime)
    {
        if (effectStart == -1) { return 0F; }
        float diff = (float)(worldTime - effectStart);
        return diff / (float)effectTime;
    }

    public void invalidate() { invalid = true; }

    public void invalidateClient() { invalid = true; }

    public final boolean isInvalid() { return invalid; }

    public boolean showIcon() { return true; }

    protected boolean showProgress() { return false; }

    protected abstract boolean isPositive();
}