package com.husker.editor.app.window.panels.preview;

import com.alee.extended.tab.DocumentAdapter;
import com.alee.extended.tab.DocumentData;
import com.alee.extended.tab.PaneData;
import com.alee.extended.tab.WebDocumentPane;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.style.StyleId;
import com.husker.editor.app.component.StyleComponent;
import com.husker.editor.app.events.*;
import com.husker.editor.app.listeners.editable_object.EditableObjectAdapter;
import com.husker.editor.app.listeners.skin.SkinAdapter;
import com.husker.editor.app.project.*;
import com.husker.editor.app.skin.CustomSkin;
import com.husker.editor.app.tools.VisibleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class PreviewPanel extends WebPanel {

    private HashMap<EditableObject, PaintingPanel> paints = new HashMap<>();
    private WebDocumentPane components_tab;
    private static WebProgressBar progressBar;
    private static WebToggleButton shape;
    private static ImageIcon style_icon = new ImageIcon("bin/style_small.png");

    public PreviewPanel(){
        setLayout(new BorderLayout());

        CustomSkin.addSkinListener(new SkinAdapter() {
            public void lastApplied(LastSkinAppliedEvent event) {
                progressBar.setVisible(false);
            }
            public void applied(SkinAppliedEvent event) {
                SwingUtilities.invokeLater(() -> {
                    if(getCurrentPaint() != null)
                        getCurrentPaint().updateUI();
                });
            }
            public void applying(SkinApplyingEvent event) {
                progressBar.setVisible(true);
            }
        });

        Project.addListener(e -> {
            components_tab.closeAll();
            for(StyleComponent component : Project.getCurrentProject().Components.getComponents())
                addComponent(component);
        });

        EditableObject.addEditableObjectListener(new EditableObjectAdapter() {
            public void variableChanged(VariableChangedEvent event) {
                if(VisibleUtils.isEditableObject(event.getObject()))
                    updateComponent(event.getObject());
            }
            public void selectedChanged(SelectedChangedEvent event) {
                addComponent(event.getObject());
                updateComponent(event.getObject());
            }
        });
        Project.addListener(e -> components_tab.setVisible(!(Project.getCurrentProject() == null)));
        Code.addCodeListener(event -> {
            CustomSkin.applySkin(getCurrentPaint().getContent(), event.getCode().getText());
        });

        components_tab = new WebDocumentPane(StyleId.of("custom-documentpane"), tabbedPane -> ((WebTabbedPane)tabbedPane).setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT));
        components_tab.setSplitEnabled(true);
        components_tab.setTabMenuEnabled(false);
        components_tab.addDocumentListener(new DocumentAdapter() {
            public void selected(DocumentData document, PaneData pane, int index) {
                EditableObject component = ((PaintingPanel)document.getComponent()).getComponent();
                Project.getCurrentProject().setSelectedObject(component);
            }
            public void closed(DocumentData document, PaneData pane, int index) {
                removeComponent(((PaintingPanel)document.getComponent()).getComponent());
            }
        });
        add(components_tab, BorderLayout.CENTER);
        add(new WebToolBar(){{
            add(new WebButton("Update"){{
                addActionListener(e -> {
                    if(VisibleUtils.onEditableObject())
                        getCurrentPaint().updateSkin();
                });
            }});
            add(shape = new WebToggleButton("Shape"){{
                addActionListener(e -> {
                    for(Map.Entry<EditableObject, PaintingPanel> entry : paints.entrySet())
                        entry.getValue().setDrawBorder(isSelected());
                });
            }});

            addToEnd(progressBar = new WebProgressBar(0, 100){{
                setVisible(false);
                setStringPainted(true);
                setString("Applying...");
                setIndeterminate(true);
            }});

        }}, BorderLayout.SOUTH);
    }

    private void addComponent(EditableObject component){
        if(component != null) {
            if (paints.containsKey(component)) {
                for (int i = 0; i < components_tab.getDocumentsCount(); i++) {
                    if (components_tab.getDocument(i).getComponent() == paints.get(component)) {
                        components_tab.setSelected(i);
                    }
                }
            } else {
                PaintingPanel painting = new PaintingPanel(component);
                painting.setDrawBorder(shape.isSelected());

                paints.put(component, painting);
                components_tab.openDocument(new DocumentData(component.toString(), style_icon, component.getTitle(), painting));
            }
        }
    }

    private void updateComponent(EditableObject component){
        PaintingPanel paintingPanel = paints.get(component);

        paintingPanel.updateSkin();

        for (int i = 0; i < components_tab.getDocumentsCount(); i++) {
            if (components_tab.getDocument(i).getComponent() == paints.get(paintingPanel.getComponent())) {
                if(component.getVariableValue("id").equals("")) {
                    components_tab.getDocument(i).setTitle(paintingPanel.getComponent().getTitle());
                }else
                    components_tab.getDocument(i).setTitle(paintingPanel.getComponent().getTitle() + " (" + paintingPanel.getComponent().getVariableValue("id") + ")");
            }
        }
    }

    private void removeComponent(EditableObject component){
        paints.remove(component);
        if(paints.size() == 0)
            Project.getCurrentProject().resetSelectedObject();
    }

    private PaintingPanel getCurrentPaint(){
        return paints.get(Project.getCurrentProject().getSelectedObject());
    }
}
