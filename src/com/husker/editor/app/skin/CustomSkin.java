package com.husker.editor.app.skin;

import com.alee.managers.style.StyleManager;
import com.alee.managers.style.XmlSkin;
import com.alee.managers.style.data.SkinInfo;
import com.alee.utils.XmlUtils;
import com.husker.editor.app.Main;
import com.husker.editor.app.window.panels.preview.PreviewPanel;
import com.husker.editor.app.xml.XMLHead;

import javax.swing.*;
import java.awt.*;

public class CustomSkin extends XmlSkin {

    static String pattern = "<skin xmlns=\"http://weblookandfeel.com/XmlSkin\">\n" +
            "    <id>husker.editor.skin</id>\n" +
            "    <class>com.husker.editor.app.skin.CustomSkin</class>\n" +
            "    <supportedSystems>all</supportedSystems>\n" +
            "    <title>Custom skin</title>\n" +
            "    <description>WebLaF Editor Skin</description>\n" +
            "    <author>Husker</author>\n" +
            "    <include nearClass=\"com.alee.skin.web.WebSkin\">resources/skin.xml</include>\n" +
            "\n" +
            "<!-- CODE -->\n" +
            "</skin>";

    static boolean applying = false;
    static int thread_id = 0;

    public CustomSkin() {
        super(CustomSkin.class, "editor_skin.xml");
    }

    public static void applySkin(Component component, XMLHead skin){
        new Thread(() -> {
            thread_id = (int)Thread.currentThread().getId();
            try {
                while(applying)
                    Thread.sleep(50);

                if(thread_id != (int)Thread.currentThread().getId())
                    return;
                applying = true;

                PreviewPanel.progressBar.setVisible(true);

                String text = pattern.replace("<!-- CODE -->", skin.toString(1));

                SkinInfo skinInfo = XmlUtils.fromXML(text);

                StyleManager.setSkin((JComponent)component, new XmlSkin(skinInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                applying = false;
                if(thread_id == (int)Thread.currentThread().getId())
                    PreviewPanel.progressBar.setVisible(false);


                Main.frame.preview.updateUI();
                Main.frame.preview.painting.updateContent();
            }
        }).start();
    }
}