package com.chyzman.chowl.industries.screen.component;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class TexturedCheckboxComponent extends BaseComponent {

    protected final EventStream<OnChanged> checkedEvents = OnChanged.newStream();

    protected boolean checked = false;

    protected Identifier uncheckedTexture;
    protected Identifier checkedTexture;


    public TexturedCheckboxComponent(
            Identifier uncheckedTexture,
            Identifier checkedTexture,
            boolean checked
    ) {
        this.uncheckedTexture = uncheckedTexture;
        this.checkedTexture = checkedTexture;
        this.checked = checked;
    }


    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        context.drawTexture(
                checked ? checkedTexture : uncheckedTexture,
                this.x, this.y,
                0, 0,
                this.width,this.height,
                this.width, this.height
        );
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        boolean result = super.onMouseDown(mouseX, mouseY, button);

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        boolean result = super.onKeyPress(keyCode, scanCode, modifiers);

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            this.toggle();
            return true;
        }

        return result;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    public void toggle() {
        this.checked(!this.checked);
        UISounds.playInteractionSound();
    }

    public EventSource<OnChanged> onChanged() {
        return this.checkedEvents.source();
    }

    public TexturedCheckboxComponent checked(boolean checked) {
        this.checked = checked;
        this.checkedEvents.sink().onChanged(this.checked);

        return this;
    }

    public boolean checked() {
        return checked;
    }


    public interface OnChanged {
        void onChanged(boolean nowChecked);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }
}
