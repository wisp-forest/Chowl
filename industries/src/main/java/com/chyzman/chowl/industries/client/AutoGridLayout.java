package com.chyzman.chowl.industries.client;

import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;

public class AutoGridLayout extends GridLayout {
    private int posX = 0;
    private int posY = 0;

    public AutoGridLayout(Sizing horizontalSizing, Sizing verticalSizing, int rows, int columns) {
        super(horizontalSizing, verticalSizing, rows, columns);
    }

    public AutoGridLayout child(Component component) {
        child(component, posY, posX);

        posX++;
        if (posX == columns) {
            posX = 0;
            posY++;
        }

        return this;
    }
}
