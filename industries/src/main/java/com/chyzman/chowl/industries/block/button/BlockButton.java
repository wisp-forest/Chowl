package com.chyzman.chowl.industries.block.button;

public record BlockButton(
    float minX, float minY,
    float maxX, float maxY,
    BlockButtonProvider.UseFunction use,
    BlockButtonProvider.AttackFunction attack,
    BlockButtonProvider.DoubleClickFunction doubleClick,
    ButtonRenderCondition renderWhen,
    ButtonRenderer renderer
) {
    public static BlockButtonBuilder builder(float minX, float minY, float maxX, float maxY) {
        return new BlockButtonBuilder(minX, minY, maxX, maxY);
    }

    public boolean isIn(float x, float y) {
        return minX <= x * 16 && x * 16 <= maxX && minY <= y * 16 && y * 16 <= maxY;
    }

    public boolean equals(BlockButton button) {
        if (button == null) return false;
        return button.minX == minX && button.minY == minY && button.maxX == maxX && button.maxY == maxY;
    }
}
