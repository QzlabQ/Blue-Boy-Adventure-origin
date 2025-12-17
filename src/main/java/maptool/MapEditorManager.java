package maptool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapEditorManager extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newWindowItem;
    private JMenuItem openWindowItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem exitItem;
    private JTabbedPane tabbedPane; // 使用选项卡而不是窗口列表
    private List<MapEditor> openEditors;

    public MapEditorManager() {
        openEditors = new ArrayList<>();
        initializeComponents();
        setupMenu();
        setupLayout();
        setupKeyboardShortcuts();

        setTitle("Map Editor Manager");
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // 修改窗口关闭事件监听器
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAllTabsWithConfirmation();
            }
        });
    }

    private void initializeComponents() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件");
        tabbedPane = new JTabbedPane();

        newWindowItem = new JMenuItem("新建地图");
        newWindowItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

        openWindowItem = new JMenuItem("打开地图");
        openWindowItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

        saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        saveAsItem = new JMenuItem("另存为");
        saveAsItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        exitItem = new JMenuItem("退出");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));

        newWindowItem.addActionListener(e -> createNewEditorTab(null));
        openWindowItem.addActionListener(e -> openMapFile());
        saveItem.addActionListener(e -> saveCurrentTab());
        saveAsItem.addActionListener(e -> saveAsCurrentTab());
        exitItem.addActionListener(e -> closeAllTabsWithConfirmation());
    }

    private void setupMenu() {
        fileMenu.add(newWindowItem);
        fileMenu.add(openWindowItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void createNewEditorTab(String filePath) {
        // 如果 filePath 为空，说明是新建地图，需要询问用户文件名
        String actualFilePath = filePath;
        if (filePath == null) {
            String fileName = JOptionPane.showInputDialog(this,
                    "请输入新地图的文件名:", "新建地图", JOptionPane.QUESTION_MESSAGE);

            if (fileName == null) {
                // 用户取消操作
                return;
            }

            if (fileName.trim().isEmpty()) {
                fileName = "untitled_map";
            }

            // 确保文件名以 .txt 结尾
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            // 构造完整的文件路径
            actualFilePath = "res/maps/" + fileName;
        }

        MapEditor editor = new MapEditor(actualFilePath, this);
        editor.setTabIndex(tabbedPane.getTabCount());
        openEditors.add(editor);

        // 添加到选项卡
        String title = new File(actualFilePath).getName();
        int tabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab(title, editor.getContentPane());
        tabbedPane.setSelectedIndex(tabIndex);

        // 更新选项卡标题样式
        updateTabTitle(tabIndex);

        // 确保焦点在新创建的编辑器上，以便快捷键能正常工作
        editor.getContentPane().requestFocusInWindow();

        // 添加选项卡选择监听器，确保切换选项卡时焦点正确
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < openEditors.size()) {
                MapEditor selectedEditor = openEditors.get(selectedIndex);
                selectedEditor.getContentPane().requestFocusInWindow();
            }
        });

        // 监听标签关闭事件
        tabbedPane.setTabComponentAt(tabIndex, new TabButton(title, tabIndex));

        // 隐藏独立的 editor 窗口
        editor.setVisible(false);
    }

    // 内部类：带关闭按钮的选项卡标题
    class TabButton extends JPanel {
        private JLabel label;
        private JButton closeButton;
        private int tabIndex;

        public TabButton(String title, int index) {
            this.tabIndex = index;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

            label = new JLabel(title);
            add(label);

            closeButton = new JButton("×");
            closeButton.setPreferredSize(new Dimension(16, 16));
            closeButton.setToolTipText("关闭");
            closeButton.setMargin(new Insets(0, 0, 0, 0));

            // 修改这里的处理方式
            closeButton.addActionListener(e -> {
                // 我们不能直接调用 closeTab，因为这会导致无论如何选项卡都会被关闭
                // 应该先检查是否可以安全关闭
                closeTabWithConfirmation(tabIndex);
            });
            add(closeButton);
        }

        public JLabel getLabel() {
            return label;
        }

        public void updateTitle(String title, boolean isDirty) {
            label.setText(title);
            Font font = label.getFont();
            if (isDirty) {
                label.setFont(font.deriveFont(Font.ITALIC));
            } else {
                label.setFont(font.deriveFont(Font.PLAIN));
            }
        }

        // 新增方法：带确认的关闭选项卡
        private void closeTabWithConfirmation(int tabIndex) {
            if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
                MapEditor editor = openEditors.get(tabIndex);

                // 只有在地图被修改时才提示保存
                if (editor.isDirty()) {
                    // 询问是否保存更改
                    String fileName = new File(editor.getCurrentMapFile()).getName();
                    int result = JOptionPane.showConfirmDialog(
                            SwingUtilities.windowForComponent(this),
                            "是否保存对 \"" + fileName + "\" 的更改?",
                            "保存更改",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    // 注意：当用户点击对话框的关闭按钮时，result 会是 JOptionPane.CLOSED_OPTION (-1)
                    if (result == JOptionPane.CLOSED_OPTION) {
                        // 用户点击了对话框的关闭按钮，什么也不做，继续编辑
                        return;
                    } else if (result == JOptionPane.YES_OPTION) {
                        editor.saveMapAction();
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        // 用户取消关闭操作
                        return;
                    }
                }

                // 执行关闭操作
                tabbedPane.removeTabAt(tabIndex);
                if (tabIndex < openEditors.size()) {
                    openEditors.remove(tabIndex);
                }
            }
        }
    }

    private void openMapFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("res/maps/"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "文本文件 (*.txt)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            createNewEditorTab(filePath);
        }
    }

    // 添加保存当前标签页的方法
    private void saveCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < openEditors.size()) {
            MapEditor editor = openEditors.get(selectedIndex);
            editor.saveMapAction(); // 调用MapEditor的保存方法
        } else {
            JOptionPane.showMessageDialog(this, "没有打开的地图文件", "保存错误", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 添加另存为当前标签页的方法
    private void saveAsCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < openEditors.size()) {
            MapEditor editor = openEditors.get(selectedIndex);
            editor.saveMapAsAction(); // 调用MapEditor的另存为方法
        } else {
            JOptionPane.showMessageDialog(this, "没有打开的地图文件", "另存为错误", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void closeTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            MapEditor editor = openEditors.get(tabIndex);

            // 只有在地图被修改时才提示保存
            if (editor.isDirty()) {
                // 询问是否保存更改
                String fileName = new File(editor.getCurrentMapFile()).getName();
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "是否保存对 \"" + fileName + "\" 的更改?",
                        "保存更改",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // 注意：当用户点击对话框的关闭按钮时，result 会是 JOptionPane.CLOSED_OPTION (-1)
                if (result == JOptionPane.CLOSED_OPTION) {
                    // 用户点击了对话框的关闭按钮，什么也不做，继续编辑
                    return;
                } else if (result == JOptionPane.YES_OPTION) {
                    editor.saveMapAction();
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    // 用户取消关闭操作
                    return;
                }
            }

            // 执行关闭操作
            tabbedPane.removeTabAt(tabIndex);
            if (tabIndex < openEditors.size()) {
                openEditors.remove(tabIndex);
            }
        }
    }

    private void closeAllTabsWithConfirmation() {
        if (openEditors.isEmpty()) {
            System.exit(0);
            return;
        }

        // 检查是否有未保存的更改
        boolean hasDirty = false;
        for (MapEditor editor : openEditors) {
            if (editor.isDirty()) {
                hasDirty = true;
                break;
            }
        }

        // 只有在有未保存更改时才提示
        if (hasDirty) {
            // 询问是否保存全部更改
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "是否保存对所有地图的更改?",
                    "保存全部",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // 注意：当用户点击对话框的关闭按钮时，result 会是 JOptionPane.CLOSED_OPTION (-1)
            if (result == JOptionPane.CLOSED_OPTION) {
                // 用户点击了对话框的关闭按钮，什么也不做，继续编辑
                return;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                // 用户取消退出操作
                return;
            }

            if (result == JOptionPane.YES_OPTION) {
                // 保存所有未保存的更改
                for (MapEditor editor : openEditors) {
                    if (editor.isDirty()) {
                        editor.saveMapAction();
                    }
                }
            }
        }

        // 关闭程序
        System.exit(0);
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        actionMap.put("undo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int idx = tabbedPane.getSelectedIndex();
                if (idx >= 0 && idx < openEditors.size()) {
                    openEditors.get(idx).undo();
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        actionMap.put("redo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int idx = tabbedPane.getSelectedIndex();
                if (idx >= 0 && idx < openEditors.size()) {
                    openEditors.get(idx).redo();
                }
            }
        });
    }

    public void updateTabTitle(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < openEditors.size()) {
            MapEditor editor = openEditors.get(tabIndex);
            String title = new File(editor.getCurrentMapFile()).getName();

            // 如果有未保存的更改，在标题前添加星号
            if (editor.isDirty()) {
                title = "* " + title;
            }

            // 更新选项卡标题
            tabbedPane.setTitleAt(tabIndex, title);

            // 设置字体样式
            Component tabComponent = tabbedPane.getTabComponentAt(tabIndex);
            if (tabComponent instanceof TabButton) {
                TabButton tabButton = (TabButton) tabComponent;
                tabButton.updateTitle(title, editor.isDirty());
            } else {
                // 如果使用默认的标签组件，设置字体
                JLabel label = getTabLabel(tabIndex);
                if (label != null) {
                    Font font = label.getFont();
                    if (editor.isDirty()) {
                        // 设置为斜体
                        label.setFont(font.deriveFont(Font.ITALIC));
                    } else {
                        // 恢复正常字体
                        label.setFont(font.deriveFont(Font.PLAIN));
                    }
                }
            }
        }
    }

    private JLabel getTabLabel(int tabIndex) {
        // 获取默认选项卡标签
        Component tabComponent = tabbedPane.getTabComponentAt(tabIndex);
        if (tabComponent == null) {
            return null;
        }

        // 如果是自定义的TabButton
        if (tabComponent instanceof TabButton) {
            return ((TabButton) tabComponent).getLabel();
        }

        // 如果是默认标签组件，查找其中的JLabel
        if (tabComponent instanceof Container) {
            Container container = (Container) tabComponent;
            for (Component comp : container.getComponents()) {
                if (comp instanceof JLabel) {
                    return (JLabel) comp;
                }
            }
        }

        return null;
    }

    public void notifyEditorChanged(MapEditor editor) {
        int index = openEditors.indexOf(editor);
        if (index >= 0) {
            updateTabTitle(index);
        }
    }
}