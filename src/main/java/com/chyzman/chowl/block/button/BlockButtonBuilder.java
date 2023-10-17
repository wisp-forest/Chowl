package com.chyzman.chowl.block.button;

import org.joml.Vector2f;

public class BlockButtonBuilder {
    private Vector2f min = new Vector2f();
    private Vector2f max = new Vector2f();

    private BlockButtonProvider.UseFunction use;
    private BlockButtonProvider.AttackFunction attack;
    private BlockButtonProvider.DoubleClickFunction doubleClick;
    private ButtonRenderCondition renderWhen = ButtonRenderCondition.NEVER;
    private ButtonRenderer renderer = ButtonRenderer.empty();

    BlockButtonBuilder(float minX, float minY, float maxX, float maxY) {
        this.min.x = minX;
        this.min.y = minY;
        this.max.x = maxX;
        this.max.y = maxY;
    }

    public BlockButtonBuilder onUse(BlockButtonProvider.UseFunction use) {
        this.use = use;
        return this;
    }

    public BlockButtonBuilder onAttack(BlockButtonProvider.AttackFunction attack) {
        this.attack = attack;
        return this;
    }

    public BlockButtonBuilder onDoubleClick(BlockButtonProvider.DoubleClickFunction doubleClick) {
        this.doubleClick = doubleClick;
        return this;
    }

    public BlockButtonBuilder renderWhen(ButtonRenderCondition when) {
        this.renderWhen = when;
        return this;
    }

    public BlockButtonBuilder renderer(ButtonRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public BlockButton build() {
        return new BlockButton(min.x, min.y, max.x, max.y, use, attack, doubleClick, renderWhen, renderer);
    }
}
