package com.atomgdx.theme.options;

import com.atomgdx.core.settings.AtomGdxSettings;
import com.atomgdx.theme.AtomGdxPreferencesPanel;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * NetBeans Options Panel Controller registering a top-level 'LibGDX' category in the Options Dialog.
 * Reference: https://netbeans.apache.org/tutorial/main/tutorials/nbm-options/
 */
@OptionsPanelController.TopLevelRegistration(
        categoryName = "#OptionsCategory_Name_LibGDX",
        iconBase = "com/atomgdx/theme/icons/settings.png",
        keywords = "#OptionsCategory_Keywords_LibGDX",
        keywordsCategory = "LibGDX",
        position = 800
)
public class AtomGdxOptionsPanelController extends OptionsPanelController {

    private AtomGdxPreferencesPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            if (isChanged()) {
                getPanel().saveSettings();
                changed = false;
            }
        });
    }

    @Override
    public void cancel() {
        // Discard changes
    }

    @Override
    public boolean isValid() {
        return getPanel() != null;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private AtomGdxPreferencesPanel getPanel() {
        if (panel == null) {
            panel = new AtomGdxPreferencesPanel(new AtomGdxSettings());
        }
        return panel;
    }

    public void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
