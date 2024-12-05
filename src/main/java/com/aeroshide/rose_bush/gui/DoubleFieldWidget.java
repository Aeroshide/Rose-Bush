package com.aeroshide.rose_bush.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;

public class DoubleFieldWidget extends TextFieldWidget {
    public DoubleFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, MutableText message) {
        super(textRenderer, x, y, width, height, message);
    }

    @Override
    public void write(String text) {
        String oldText = this.getText();
        super.write(text);
        String newText = this.getText();

        try {
            double number = Double.parseDouble(newText);
            this.setText(Double.toString(number));
        } catch (NumberFormatException e) {
            this.setText(oldText);
        }
    }

    public double getDouble() {
        String text = this.getText();
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}