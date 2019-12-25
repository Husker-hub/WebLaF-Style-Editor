package com.husker.editor.app.components;

import com.alee.laf.button.WebButton;
import com.husker.editor.app.project.StyleComponent;

import java.awt.*;

import static com.husker.editor.app.project.StyleComponent.Parameters.*;

public class Styled_Button extends StyleComponent {

    public Styled_Button() {
        super("Button", "button");

        addImplementedParameters(
                KIT_BACKGROUND,
                KIT_BORDER,
                KIT_INNER_SHADOW,
                KIT_OUTER_SHADOW,
                KIT_SHAPE,
                KIT_BUTTON_CONTENT
        );
        setDefaultValue(SHAPE_ENABLED, "true");
        setDefaultValue(ROUND_FULL, "3");
        setDefaultValue(OUTER_SHADOW_WIDTH, "2");
        setDefaultValue(BORDER_COLOR, "170,170,170");
        setDefaultValue(BACKGROUND_TYPE, "Gradient");
        setDefaultValue(BUTTON_SHOW_ICON, "true");
        setDefaultValue(BUTTON_SHOW_TEXT, "true");

        setDefaultValue(ROUND_LB, "3");
        setDefaultValue(ROUND_LT, "3");
        setDefaultValue(ROUND_RB, "3");
        setDefaultValue(ROUND_RT, "3");
    }

    public Component createPreviewComponent() {
        return new WebButton(getExampleText(), getExampleIcon());
    }
}
