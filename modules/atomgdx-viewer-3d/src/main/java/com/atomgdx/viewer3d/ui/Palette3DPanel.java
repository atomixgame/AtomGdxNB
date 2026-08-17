package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.PaletteItemVO;
import com.atomgdx.viewer3d.data.PaletteLoader;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced Asset Palette displaying 40+ 2D & 3D Shapes, Prefabs, and Materials
 * with graphical thumbnails, rich descriptions, and instant search filtering.
 */
public class Palette3DPanel extends JPanel {

    private final List<PaletteItemVO> allItems;
    private Consumer<Object> itemSelectedListener;
    private final JTextField searchField;
    private final JTabbedPane tabs;

    private final List<JPanel> categoryContainers = new ArrayList<>();
    private final String[] categoryNames = {"All", "2D Shapes", "2D Prefabs", "3D Primitives", "3D Prefabs", "Materials"};

    public Palette3DPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        // 1. Load Items from Configuration
        allItems = PaletteLoader.loadPaletteItems();

        // 2. Search & Filter Bar
        JPanel topSearchPanel = new JPanel(new BorderLayout(6, 0));
        topSearchPanel.setBackground(DarkThemeUtils.BG_HEADER);
        topSearchPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel searchIcon = new JLabel(DarkThemeUtils.getFatcowIcon("magnifier.png"));
        searchField = DarkThemeUtils.createCompactTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search shapes, models, prefabs, materials...");

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterItems(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterItems(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterItems(); }
        });

        topSearchPanel.add(searchIcon, BorderLayout.WEST);
        topSearchPanel.add(searchField, BorderLayout.CENTER);
        add(topSearchPanel, BorderLayout.NORTH);

        // 3. Tabbed Categories with Icons
        tabs = new JTabbedPane();
        tabs.setBackground(DarkThemeUtils.BG_HEADER);
        tabs.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 10));

        String[] catIcons = {"application_view_tile.png", "shape_square.png", "car.png", "box.png", "brick.png", "color_wheel.png"};

        for (int i = 0; i < categoryNames.length; i++) {
            JPanel catPanel = createListPanel();
            categoryContainers.add(catPanel);
            tabs.addTab(categoryNames[i], DarkThemeUtils.getFatcowIcon(catIcons[i]), wrapScroll(catPanel));
        }

        add(tabs, BorderLayout.CENTER);

        populateTabs("");
    }

    public void setItemSelectedListener(Consumer<Object> listener) {
        this.itemSelectedListener = listener;
    }

    private void filterItems() {
        String query = searchField.getText().trim().toLowerCase();
        populateTabs(query);
    }

    private void populateTabs(String filter) {
        for (JPanel p : categoryContainers) {
            p.removeAll();
        }

        int[] countPerCat = new int[categoryNames.length];

        for (PaletteItemVO item : allItems) {
            if (!matchesFilter(item, filter)) continue;

            // Add to "All" (Index 0)
            categoryContainers.get(0).add(createPaletteCard(item));
            countPerCat[0]++;

            // Add to specific category
            for (int i = 1; i < categoryNames.length; i++) {
                if (categoryNames[i].equalsIgnoreCase(item.category)) {
                    categoryContainers.get(i).add(createPaletteCard(item));
                    countPerCat[i]++;
                    break;
                }
            }
        }

        for (int i = 0; i < categoryContainers.size(); i++) {
            categoryContainers.get(i).add(Box.createVerticalGlue());
            categoryContainers.get(i).revalidate();
            categoryContainers.get(i).repaint();
            tabs.setTitleAt(i, categoryNames[i] + " (" + countPerCat[i] + ")");
        }
    }

    private boolean matchesFilter(PaletteItemVO item, String filter) {
        if (filter.isEmpty()) return true;
        if (item.name.toLowerCase().contains(filter)) return true;
        if (item.subtitle != null && item.subtitle.toLowerCase().contains(filter)) return true;
        if (item.category.toLowerCase().contains(filter)) return true;
        for (String tag : item.tags) {
            if (tag.toLowerCase().contains(filter)) return true;
        }
        return false;
    }

    private JPanel createPaletteCard(PaletteItemVO item) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(DarkThemeUtils.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DarkThemeUtils.BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(500, 56));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 1. Thumbnail Container
        JPanel thumbPanel = new JPanel(new GridBagLayout());
        thumbPanel.setPreferredSize(new Dimension(36, 36));
        thumbPanel.setBackground(getThumbnailColor(item.category));
        thumbPanel.setBorder(new LineBorder(DarkThemeUtils.BORDER, 1, true));

        JLabel iconLbl = new JLabel(DarkThemeUtils.getFatcowIcon(item.icon));
        thumbPanel.add(iconLbl);
        card.add(thumbPanel, BorderLayout.WEST);

        // 2. Title & Description
        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 2));
        textP.setOpaque(false);

        JLabel titleLbl = new JLabel(item.name);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        JLabel descLbl = new JLabel(item.subtitle != null ? item.subtitle : item.category);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLbl.setForeground(DarkThemeUtils.TEXT_MUTED);

        textP.add(titleLbl);
        textP.add(descLbl);
        card.add(textP, BorderLayout.CENTER);

        // 3. Add Action Button
        JButton addBtn = new JButton("+ Add", DarkThemeUtils.getFatcowIcon("add.png"));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        addBtn.setBackground(DarkThemeUtils.BG_HEADER);
        addBtn.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(68, 26));
        addBtn.addActionListener(e -> notifySelected(item.createInstance()));
        card.add(addBtn, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                notifySelected(item.createInstance());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(DarkThemeUtils.BG_HEADER_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(DarkThemeUtils.BG_PANEL);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(500, 60));
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.add(Box.createVerticalStrut(4), BorderLayout.SOUTH);

        return wrapper;
    }

    private Color getThumbnailColor(String category) {
        if (category == null) return new Color(30, 32, 38);
        switch (category) {
            case "2D Shapes": return new Color(24, 42, 54);
            case "2D Prefabs": return new Color(35, 28, 48);
            case "3D Primitives": return new Color(26, 40, 60);
            case "3D Prefabs": return new Color(48, 36, 24);
            case "Materials": return new Color(44, 38, 28);
            default: return new Color(30, 32, 38);
        }
    }

    private JPanel createListPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DarkThemeUtils.BG_DARK);
        p.setBorder(new EmptyBorder(4, 4, 4, 4));
        return p;
    }

    private JScrollPane wrapScroll(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void notifySelected(Object instance) {
        if (itemSelectedListener != null && instance != null) {
            itemSelectedListener.accept(instance);
        }
    }
}
