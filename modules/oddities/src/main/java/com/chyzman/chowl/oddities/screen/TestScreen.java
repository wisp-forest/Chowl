package com.chyzman.chowl.oddities.screen;

import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.LayoutAxis;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Align;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.basic.Transform;
import io.wispforest.owo.braid.widgets.drag.DragArena;
import io.wispforest.owo.braid.widgets.slider.Slider;
import io.wispforest.owo.braid.widgets.window.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class TestScreen extends BraidScreen {

    public TestScreen() {
        super(
                new DragArena(
                        new FunniWindow()
                )
        );
    }

    public static class FunniWindow extends StatefulWidget {
        @Override
        public WidgetState<FunniWindow> createState() {
            return new State();
        }

        public static class State extends WidgetState<FunniWindow> {
            private double slide = 0;

            @Override
            public void init() {
                this.update();
            }

            private void update() {
                this.setState(() -> {});
                this.scheduleAnimationCallback(v -> this.update());
            }

            @Override
            public Widget build(BuildContext context) {
                return new Window(
                        true,
                        Text.literal("funni window moment"),
                        () -> MinecraftClient.getInstance().setScreen(null),
                        null,
                        new Align(
                                Alignment.RIGHT,
                                new Sized(
                                        500d,
                                        10d,
                                        new Transform(
                                                new Matrix4f().translate((float) -this.slide / 11, 0, 0),
                                                new Slider(
                                                    slide,
                                                    0, 500,
                                                    null,
                                                    LayoutAxis.HORIZONTAL,
                                                    value -> this.setState(() -> this.slide = value)
                                                )
                                        )
                                )
                        )
                );
            }
        }
    }
}
