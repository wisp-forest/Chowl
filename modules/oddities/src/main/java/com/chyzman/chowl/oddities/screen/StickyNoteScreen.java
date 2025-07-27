package com.chyzman.chowl.oddities.screen;

import com.chyzman.chowl.oddities.attachable.StickyNoteAttachable;
import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.Size;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.ItemStackWidget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.MessageButton;
import io.wispforest.owo.braid.widgets.checkbox.Checkbox;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.braid.widgets.textinput.EditableText;
import io.wispforest.owo.braid.widgets.textinput.TextBox;
import io.wispforest.owo.braid.widgets.textinput.TextEditingController;
import io.wispforest.owo.braid.widgets.textinput.TextInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.joml.Matrix4f;

public class StickyNoteScreen extends BraidScreen {

    public StickyNoteScreen(StickyNoteAttachable note) {
        super(
            new Stack(
                new Padding(
                    Insets.top(40),
                    new Align(
                        Alignment.TOP,
                        new Label(Text.translatable("ui.chowl-oddities.sticky_note.title"))
                    )
                ),
                new Center(new StickyNoteEditor(note)),
                new Padding(
                    Insets.bottom(5),
                    new Align(
                        Alignment.BOTTOM,
                        new Sized(
                            200, 20,
                            new MessageButton(
                                Text.translatable("gui.done"),
                                () -> MinecraftClient.getInstance().setScreen(null)
                            )
                        )
                    )
                )
            )
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static class StickyNoteEditor extends StatefulWidget {
        public final StickyNoteAttachable note;

        public StickyNoteEditor(StickyNoteAttachable note) {
            this.note = note;
        }

        @Override
        public WidgetState<StickyNoteEditor> createState() {
            return new State();
        }

        public static class State extends WidgetState<StickyNoteEditor> {
            public final TextEditingController textController = new TextEditingController();

            @Override
            public void init() {
                this.textController.setText(this.widget().note.text().getString());
                this.textController.addListener(() -> this.setState(() -> this.widget().note.text(Text.literal(this.textController.text()))));
            }

            @Override
            public Widget build(BuildContext context) {
                return new Sized(
                    94, 94,
                    new Stack(
                        Alignment.CENTER,
                        new ItemStackWidget(OdditiesItems.STICKY_NOTE.getDefaultStack(), false),
                        new Transform(
                            new Matrix4f().translate(0, 0, 100),
                            new Padding(
                                Insets.all(1).withTop(3),
                                new TextInput(
                                    textController,
                                    true,
                                    true,
                                    true,
                                    true,
                                    Style.EMPTY.withColor(Colors.BLACK)
                                )
                            )
                        )
                    )
                );
            }
        }
    }
}
