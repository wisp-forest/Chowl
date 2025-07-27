package com.chyzman.chowl.oddities.screen;

import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Center;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.checkbox.Checkbox;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.textinput.TextBox;
import io.wispforest.owo.braid.widgets.textinput.TextEditingController;
import net.minecraft.text.Style;

public class ClipboardScreen extends BraidScreen {
    private ClipboardBlockEntity clipboard;

    public ClipboardScreen(ClipboardBlockEntity clipboard) {
        super(
            new Center(
                new ClipboardEditor(clipboard)
            )
        );
    }

    public static class ClipboardEditor extends StatelessWidget {
        public final ClipboardBlockEntity clipboard;

        public ClipboardEditor(ClipboardBlockEntity clipboard) {
            this.clipboard = clipboard;
        }

        @Override
        public Widget build(BuildContext context) {
            return new Sized(
                200, 250,
                new Column(
                    clipboard.contents.stream()
                        .map(ClipboardLineWidget::new)
                        .toList()
                )
            );
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static class ClipboardLineWidget extends StatefulWidget {
        public final ClipboardBlockEntity.ClipboardLine line;

        public ClipboardLineWidget(ClipboardBlockEntity.ClipboardLine line) {
            this.line = line;
        }

        @Override
        public WidgetState<ClipboardLineWidget> createState() {
            return new State();
        }

        public static class State extends WidgetState<ClipboardLineWidget> {
            private final TextEditingController textController = new TextEditingController();

            @Override
            public void init() {
                this.textController.setText(widget().line.text);
                this.textController.addListener(() -> widget().line.text = this.textController.text());
            }

            @Override
            public Widget build(BuildContext context) {
                return new Row(
                    new Checkbox(
                        this.widget().line.checked,
                        aBoolean -> this.setState(() -> this.widget().line.checked = aBoolean)
                    ),
                    new Sized(
                        220, 20,
                        new TextBox(
                            this.textController,
                            false,
                            false,
                            false,
                            Style.EMPTY
                        )
                    )
                );
            }
        }
    }
}
