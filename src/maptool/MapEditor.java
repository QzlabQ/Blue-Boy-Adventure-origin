package maptool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class MapEditor extends JFrame {
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;
    private static final int TILE_SIZE = 32;

    private int[][] mapData;
    private TileSet tileSet;
    private JPanel[][] tilePanels;

    private JPanel mapPanel;
    private JPanel tilePalettePanel;
    private JScrollPane mapScrollPane;
    private JLabel statusLabel;
    private JTextField mapFilePathField;

    private int selectedTile = 0;
    private JButton selectedTileButton;
    private JCheckBox coordsButton;

    private String currentMapFile = "res/maps/map.txt";
    private String tilesFolder = "res/tiles/";
    private String mapsFolder = "res/maps/";

    private boolean showCoordinates = false;
    private boolean mouseMode = false;

    private Stack<int[][]> undoStack = new Stack<>();
    private Stack<int[][]> redoStack = new Stack<>();
    private final int MAX_UNDO_STEPS = 1000;

    // 实体相关
    private List<EntityEntry> entityEntries = new ArrayList<>();
    private JLabel categoryLabel;
    private JComboBox<String> typeCombo;
    private JComboBox<String> extraCombo;
    private JPanel extraPanel;
    private JPanel entityPanel; // 确保成员变量声明（虽然已在原文件顶部有声明，这里是为了逻辑清晰）
    private String selectedEntityType = "OBJ_Coin_Bronze";
    private EntityImageCache entityImages = new EntityImageCache();

    // 模式控制组件
    private JRadioButton editModeRadio;
    private JRadioButton mouseModeRadio;
    private JRadioButton entityModeRadio;

    private static List<MapEditor> openEditors = new ArrayList<>();
    private String windowTitle;

    private boolean dirty = false;

    private MapEditorManager manager;
    private int tabIndex = -1; // 添加此字段来跟踪选项卡索引

    public MapEditor(String filePath) {
        mapData = new int[MAP_HEIGHT][MAP_WIDTH];
        tileSet = new TileSet();
        tilePanels = new JPanel[MAP_HEIGHT][MAP_WIDTH];

        // 先加载方块资源，确保调色板创建时有数据
        tileSet.loadTileSet(tilesFolder);

        // 根据传入的文件路径处理地图文件路径
        if (filePath != null && !filePath.isEmpty()) {
            currentMapFile = filePath;
        }
        // 如果filePath为空，保持默认值，稍后会在newMapAction中更新

        initUI();
        entityImages.loadEntityImages();

        ensureMapsFolder();

        // 根据传入的文件路径处理地图内容
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                // 文件存在，加载地图
                loadMap(filePath);
                updateStatus("加载地图文件: " + file.getName());
            } else {
                // 文件不存在，创建新地图
                clearMap();
                updateStatus("新建地图文件: " + file.getName());
            }
        } else {
            // 没有指定文件路径，创建默认新地图
            clearMap();
            updateStatus("新建地图文件");
        }

        setTitle("Blue Boy Adventure Map Editor - " + new File(currentMapFile).getName() + " - " + MAP_WIDTH + "x"
                + MAP_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // 可以在这里添加保存提醒等功能
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // 移除对不存在方法的调用
                // 可以在这里添加其他清理工作
            }
        });
    }

    public MapEditor(String filePath, MapEditorManager manager) {
        this(filePath); // 调用原有构造函数
        this.manager = manager;
    }

    private void ensureMapsFolder() {
        File mapsDir = new File(mapsFolder);
        if (!mapsDir.exists()) {
            if (mapsDir.mkdirs()) {
                System.out.println("已创建maps文件夹: " + mapsDir.getAbsolutePath());
            }
        }
    }

    private void saveCurrentState() {
        int[][] copy = new int[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++) {
            System.arraycopy(mapData[y], 0, copy[y], 0, MAP_WIDTH);
        }

        undoStack.push(copy);
        if (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.remove(0);
        }
        redoStack.clear();

        // 添加这一行
        setDirty(true);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            int[][] currentState = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                System.arraycopy(mapData[y], 0, currentState[y], 0, MAP_WIDTH);
            }
            redoStack.push(currentState);

            mapData = undoStack.pop();
            refreshMapDisplay();
            updateStatus("撤销操作完成");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            int[][] currentState = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                System.arraycopy(mapData[y], 0, currentState[y], 0, MAP_WIDTH);
            }
            undoStack.push(currentState);

            mapData = redoStack.pop();
            refreshMapDisplay();
            updateStatus("重做操作完成");
        }
    }

    private void clearSelection() {
        if (selectedTileButton != null) {
            selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        selectedTile = -1;
        selectedTileButton = null;
        mouseMode = false;
        editModeRadio.setSelected(true);
        updateStatus();
    }

    private void setEditMode() {
        mouseMode = false;

        // 恢复之前选中的方块
        if (selectedTileButton != null) {
            selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            // 不再强制设置selectedTile为0，保持当前选中的方块
        } else if (selectedTile < 0) {
            // 如果之前没有选中任何方块，则默认选择0号方块
            selectedTile = 0;
            updateSelectedTileButton();
        }
        updateStatus("方块编辑模式: 开启");
    }

    private void setMouseMode() {
        mouseMode = true;

        if (selectedTileButton != null) {
            selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        selectedTile = -1;
        refreshMapDisplay();
        updateStatus("鼠标模式: 开启");
    }

    private void setEntityMode() {
        mouseMode = false;
        updateStatus("实体编辑模式: 开启");
        refreshMapDisplay();
    }

    private void initUI() {
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");

        JMenuItem newMenuItem = new JMenuItem("新建");
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newMenuItem.addActionListener(e -> newMapAction());

        JMenuItem openMenuItem = new JMenuItem("打开");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        openMenuItem.addActionListener(e -> loadMapAction());

        JMenuItem saveMenuItem = new JMenuItem("保存");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveMenuItem.addActionListener(e -> saveMapAction());

        JMenuItem saveAsMenuItem = new JMenuItem("另存为");
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
        saveAsMenuItem.addActionListener(e -> saveMapAsAction());

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);

        menuBar.add(fileMenu);

        // 编辑菜单
        JMenu editMenu = new JMenu("编辑");

        JMenuItem undoMenuItem = new JMenuItem("撤销");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
        undoMenuItem.addActionListener(e -> undo());

        JMenuItem redoMenuItem = new JMenuItem("重做");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
        redoMenuItem.addActionListener(e -> redo());

        JMenuItem clearMenuItem = new JMenuItem("清空地图");
        clearMenuItem.addActionListener(e -> clearMap());

        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(clearMenuItem);

        menuBar.add(editMenu);

        // 注意：这里已经移除了视图菜单的创建代码

        setJMenuBar(menuBar);

        // ... 其余代码保持不变
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(800);
        mainSplitPane.setResizeWeight(0.7);
        add(mainSplitPane);

        JPanel leftPanel = new JPanel(new BorderLayout());

        mapPanel = new JPanel(new GridLayout(MAP_HEIGHT, MAP_WIDTH, 0, 0));
        mapPanel.setPreferredSize(new Dimension(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE));

        // 添加鼠标监听器到mapPanel来支持滑动绘制
        mapPanel.addMouseMotionListener(new MouseMotionAdapter() {
            private Point lastPoint = null;
            private Rectangle lastArea = null;

            @Override
            public void mouseDragged(MouseEvent e) {
                // 检查是否在实体编辑模式
                if (entityModeRadio.isSelected()) {
                    // 实体编辑模式下不执行任何拖拽操作
                    return;
                }

                if (mouseMode || selectedTile < 0)
                    return;

                Point mousePos = e.getPoint();
                Point viewPos = mapScrollPane.getViewport().getViewPosition();
                int mouseX = mousePos.x + viewPos.x;
                int mouseY = mousePos.y + viewPos.y;

                // 计算当前鼠标所在的格子
                int tileX = Math.min(Math.max(mouseX / TILE_SIZE, 0), MAP_WIDTH - 1);
                int tileY = Math.min(Math.max(mouseY / TILE_SIZE, 0), MAP_HEIGHT - 1);

                // 如果是第一次拖动，保存状态
                if (lastPoint == null) {
                    // 只有左键拖动才保存状态
                    if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                        saveCurrentState(); // 删除了 e.isControlDown() 条件检查
                        lastPoint = new Point(tileX, tileY);

                        // 设置单个格子
                        mapData[tileY][tileX] = selectedTile;
                        tilePanels[tileY][tileX].repaint();
                    }
                    return;
                }

                // 只有左键拖动才执行绘制
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    // 计算需要更新的区域
                    int startX = Math.min(tileX, lastPoint.x);
                    int endX = Math.max(tileX, lastPoint.x);
                    int startY = Math.min(tileY, lastPoint.y);
                    int endY = Math.max(tileY, lastPoint.y);

                    Rectangle currentArea = new Rectangle(startX, startY,
                            endX - startX + 1, endY - startY + 1);

                    // 更新所有格子（无论是否按住Ctrl键都会保存状态）
                    for (int y = startY; y <= endY; y++) {
                        for (int x = startX; x <= endX; x++) {
                            mapData[y][x] = selectedTile;
                            tilePanels[y][x].repaint();
                        }
                    }
                    lastArea = currentArea;

                    lastPoint = new Point(tileX, tileY);

                    updateStatus();
                }
            }

            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
                lastArea = null;
            }
        });

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                final int posX = x;
                final int posY = y;

                JPanel tilePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        int tileId = mapData[posY][posX];
                        TileInfo tile = tileSet.getTile(tileId);

                        if (tile != null && tile.getImage() != null) {
                            g.drawImage(tile.getImage(), 0, 0, TILE_SIZE, TILE_SIZE, this);
                        } else if (tile != null) {
                            g.setColor(tile.getDisplayColor());
                            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        } else {
                            g.setColor(Color.PINK);
                            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        }

                        g.setColor(Color.GRAY);
                        g.drawRect(0, 0, TILE_SIZE - 1, TILE_SIZE - 1);

                        if (tile != null && tile.hasCollision()) {
                            g.setColor(new Color(255, 0, 0, 150));
                            g.fillRect(TILE_SIZE - 8, 0, 8, 8);
                        }

                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 8));
                        String idStr = String.valueOf(tileId);
                        g.drawString(idStr, 2, 10);

                        if (showCoordinates) {
                            g.setColor(Color.YELLOW);
                            g.setFont(new Font("Arial", Font.PLAIN, 7));
                            g.drawString(posX + "," + posY, TILE_SIZE - 20, TILE_SIZE - 3);
                        }

                        // 绘制实体指示器 - 改进版本
                        if (hasEntityAt(posX, posY)) {
                            // 查找当前位置的实体类型
                            String entityType = null;
                            String entityExtra = null;
                            for (EntityEntry entry : entityEntries) {
                                if (entry.getX() == posX && entry.getY() == posY) {
                                    entityType = entry.getType();
                                    entityExtra = entry.getExtra();
                                    break;
                                }
                            }

                            if (entityType != null) {
                                // 尝试获取实体图像
                                BufferedImage entityImage = entityImages.getImage(entityType);
                                if (entityImage != null) {
                                    // 绘制实体图像，居中显示在格子中
                                    int imgWidth = Math.min(TILE_SIZE - 8, entityImage.getWidth());
                                    int imgHeight = Math.min(TILE_SIZE - 8, entityImage.getHeight());
                                    int imgX = (TILE_SIZE - imgWidth) / 2;
                                    int imgY = (TILE_SIZE - imgHeight) / 2;

                                    g.drawImage(entityImage, imgX, imgY, imgWidth, imgHeight, this);
                                } else {
                                    // 如果没有找到图像，使用原来的绿色圆点
                                    g.setColor(new Color(0, 255, 0, 180));
                                    g.fillOval(6, 6, TILE_SIZE - 12, TILE_SIZE - 12);
                                    g.setColor(Color.BLACK);
                                    g.drawOval(6, 6, TILE_SIZE - 12, TILE_SIZE - 12);
                                }

                                // 始终添加高亮效果，而不仅仅在实体编辑模式下
                                g.setColor(new Color(255, 255, 0, 100));
                                g.fillOval(2, 2, TILE_SIZE - 4, TILE_SIZE - 4);

                                // 重新绘制实体图像或绿色圆点（确保图像在高亮之上）
                                if (entityImage != null) {
                                    int imgWidth = Math.min(TILE_SIZE - 8, entityImage.getWidth());
                                    int imgHeight = Math.min(TILE_SIZE - 8, entityImage.getHeight());
                                    int imgX = (TILE_SIZE - imgWidth) / 2;
                                    int imgY = (TILE_SIZE - imgHeight) / 2;

                                    g.drawImage(entityImage, imgX, imgY, imgWidth, imgHeight, this);
                                } else {
                                    g.setColor(new Color(0, 255, 0, 180));
                                    g.fillOval(6, 6, TILE_SIZE - 12, TILE_SIZE - 12);
                                    g.setColor(Color.BLACK);
                                    g.drawOval(6, 6, TILE_SIZE - 12, TILE_SIZE - 12);
                                }

                                // 对于箱子，如果没有战利品则用红色高亮显示
                                if (entityType.equals("OBJ_Chest")
                                        && (entityExtra == null || entityExtra.isEmpty() || entityExtra.equals("无"))) {
                                    // 用红色边框高亮显示没有战利品的箱子
                                    g.setColor(Color.RED);
                                    Graphics2D g2d = (Graphics2D) g;
                                    g2d.setStroke(new BasicStroke(3));
                                    g.drawRect(2, 2, TILE_SIZE - 4, TILE_SIZE - 4);

                                    // 添加红色感叹号警告图标（覆盖在箱子图像上方）
                                    g.setColor(Color.RED);
                                    Font oldFont = g.getFont();
                                    g.setFont(new Font("Arial", Font.BOLD, 20));
                                    FontMetrics fm = g.getFontMetrics();
                                    String warning = "!";
                                    int textWidth = fm.stringWidth(warning);
                                    int textHeight = fm.getAscent();
                                    g.drawString(warning, (TILE_SIZE - textWidth) / 2, (TILE_SIZE + textHeight) / 2);
                                    g.setFont(oldFont);
                                }
                            }
                        }

                        // 当处于实体模式并且鼠标悬停时，添加视觉提示
                        if (entityModeRadio.isSelected()) {
                            // 可以根据需要添加更多视觉提示
                        }
                    }
                };
                tilePanel.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tilePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                tilePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        boolean isInEntityMode = entityModeRadio.isSelected();

                        // 在实体编辑模式下不保存方块编辑状态
                        if (isInEntityMode) {
                            return;
                        }

                        if (mouseMode || e.getButton() != MouseEvent.BUTTON1) {
                            return;
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        boolean isInEntityMode = entityModeRadio.isSelected();

                        if (isInEntityMode) {
                            // 实体编辑模式
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                String category = determineCategory(selectedEntityType);
                                String type = selectedEntityType;
                                String extra = null;

                                // 特殊处理箱子的额外参数
                                if (type.equals("OBJ_Chest") && extraCombo != null
                                        && extraCombo.getSelectedItem() != null) {
                                    String selected = (String) extraCombo.getSelectedItem();
                                    if (!selected.isEmpty() && !selected.equals("无")) {
                                        extra = selected;
                                    }
                                }

                                addEntityAt(category, type, posX, posY, extra);
                                tilePanel.repaint();
                                updateStatus("已添加实体: " + type + " 于 " + posX + "," + posY);
                            } else if (SwingUtilities.isRightMouseButton(e)) {
                                removeEntityAt(posX, posY);
                                tilePanel.repaint();
                                updateStatus("已删除坐标 (" + posX + "," + posY + ") 的实体");
                            }
                        } else if (mouseMode) {
                            // 鼠标模式下只有左键有效
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                int tileId = mapData[posY][posX];
                                TileInfo tile = tileSet.getTile(tileId);

                                StringBuilder infoBuilder = new StringBuilder();
                                infoBuilder.append("位置: (").append(posX).append(", ").append(posY).append(")\n")
                                        .append("方块ID: ").append(tileId).append("\n");

                                if (tile != null) {
                                    infoBuilder.append("名称: ").append(tile.getName()).append("\n")
                                            .append("碰撞: ").append(tile.hasCollision() ? "有" : "无");
                                }

                                // 添加实体信息
                                EntityEntry entityAtPosition = getEntityAt(posX, posY);
                                if (entityAtPosition != null) {
                                    infoBuilder.append("\n\n实体信息:\n")
                                            .append("类型: ").append(entityAtPosition.getType()).append("\n")
                                            .append("类别: ").append(entityAtPosition.getCategory());

                                    if (entityAtPosition.getExtra() != null && !entityAtPosition.getExtra().isEmpty()) {
                                        infoBuilder.append("\n额外参数: ").append(entityAtPosition.getExtra());
                                    }
                                } else {
                                    infoBuilder.append("\n\n该位置无实体");
                                }

                                JOptionPane.showMessageDialog(MapEditor.this, infoBuilder.toString(),
                                        "方块与实体信息", JOptionPane.INFORMATION_MESSAGE);
                            }
                            // 右键在鼠标模式下不做任何事情
                        } else if (SwingUtilities.isLeftMouseButton(e) && selectedTile >= 0) {
                            saveCurrentState();
                            mapData[posY][posX] = selectedTile;
                            tilePanel.repaint();
                            updateStatus();
                        }
                        // 右键不再有任何效果
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        boolean isInEntityMode = entityModeRadio.isSelected();

                        if (isInEntityMode) {
                            // 实体编辑模式下不显示黄色边框，但刷新面板以显示可能的高亮
                            tilePanel.repaint();
                        } else if (mouseMode) {
                            // 鼠标模式下显示黄色边框
                            tilePanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                            tilePanel.repaint();
                        } else if (e.isShiftDown() && selectedTile >= 0) {
                            saveCurrentState();
                            // Shift键+鼠标移动：连续绘制
                            mapData[posY][posX] = selectedTile;
                            tilePanel.repaint();
                        } else {
                            // 只在方块编辑模式下显示黄色边框
                            tilePanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // 在所有模式下都恢复默认边框
                        tilePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        tilePanel.repaint();
                    }
                });

                tilePanel.addMouseMotionListener(new MouseMotionAdapter() {
                    private boolean isDragging = false;

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // 检查是否在实体编辑模式
                        if (entityModeRadio.isSelected()) {
                            // 实体编辑模式下不执行任何拖拽操作
                            return;
                        }

                        // 只响应左键拖动
                        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
                            return;
                        }

                        if (mouseMode || selectedTile < 0)
                            return;

                        if (!isDragging) {
                            // 删除了 Ctrl 键检查，总是保存状态
                            saveCurrentState();
                            isDragging = true;
                        }
                        mapData[posY][posX] = selectedTile;
                        tilePanel.repaint();
                        updateStatus();
                    }

                    public void mouseReleased(MouseEvent e) {
                        isDragging = false;
                    }
                });

                tilePanels[posY][posX] = tilePanel;
                mapPanel.add(tilePanel);
            }
        }

        mapScrollPane = new JScrollPane(mapPanel);
        // 提高滚动速度 - 增加单位增量和块增量
        mapScrollPane.getVerticalScrollBar().setUnitIncrement(20); // 单次滚动步长
        mapScrollPane.getVerticalScrollBar().setBlockIncrement(100); // 页面滚动步长
        mapScrollPane.getHorizontalScrollBar().setUnitIncrement(20); // 单次滚动步长
        mapScrollPane.getHorizontalScrollBar().setBlockIncrement(100); // 页面滚动步长
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel optionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        coordsButton = new JCheckBox("显示坐标", showCoordinates);
        coordsButton.addActionListener(e -> {
            showCoordinates = coordsButton.isSelected();
            refreshMapDisplay();
        });

        JButton helpButton = new JButton("帮助");
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(MapEditor.this,
                    "方块编辑模式：\n" +
                            "• 左键：放置单个方块\n" +
                            "• Shift+鼠标移动：连续放置\n" +
                            "\n鼠标模式：\n" +
                            "• 左键：查看方块信息\n" +
                            "\n实体编辑模式：\n" +
                            "• 左键添加实体，右键删除实体\n" +
                            "\n快捷键：\n" +
                            "• 撤销：Ctrl+Z\n" +
                            "• 重做：Ctrl+Y\n" +
                            "\n显示特性：\n" +
                            "• ID始终显示在方块左上角\n" +
                            "• 有碰撞体积的方块右上角有红色标记\n" +
                            "• 不合法的实体会有红框警告\n",
                    "帮助",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        optionToolbar.add(new JLabel("地图:"));
        mapFilePathField = new JTextField(new File(currentMapFile).getName(), 20);
        mapFilePathField.setEditable(false);
        optionToolbar.add(mapFilePathField);
        optionToolbar.add(coordsButton);
        optionToolbar.add(helpButton);

        // 创建模式选择面板
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.setBorder(BorderFactory.createTitledBorder("模式选择"));

        editModeRadio = new JRadioButton("方块编辑模式", true);
        mouseModeRadio = new JRadioButton("鼠标模式");
        entityModeRadio = new JRadioButton("实体编辑模式");

        // 创建按钮组确保互斥选择
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(editModeRadio);
        modeGroup.add(mouseModeRadio);
        modeGroup.add(entityModeRadio);

        // 添加事件监听器
        editModeRadio.addActionListener(e -> setEditMode());
        mouseModeRadio.addActionListener(e -> setMouseMode());
        entityModeRadio.addActionListener(e -> setEntityMode());

        modePanel.add(editModeRadio);
        modePanel.add(mouseModeRadio);
        modePanel.add(entityModeRadio);

        toolbar.add(optionToolbar, BorderLayout.CENTER);
        toolbar.add(modePanel, BorderLayout.SOUTH);

        leftPanel.add(toolbar, BorderLayout.NORTH);
        leftPanel.add(mapScrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        JPanel paletteHeader = new JPanel(new BorderLayout(5, 5));
        paletteHeader.add(new JLabel("方块调色板 (共45种)"), BorderLayout.WEST);

        JTextField searchField = new JTextField();
        searchField.setToolTipText("搜索方块名称或ID");
        searchField.putClientProperty("JTextField.placeholderText", "搜索...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTiles(searchField.getText());
            }
        });
        paletteHeader.add(searchField, BorderLayout.CENTER);

        tilePalettePanel = new JPanel();
        tilePalettePanel.setLayout(new GridLayout(0, 4, 3, 3));
        
        // 设置首选尺寸以确保滚动条能正常工作
        // 计算所需高度：每个按钮85像素高，每行4个按钮，共45个按钮需要约12行
        int requiredHeight = (int) Math.ceil(45.0 / 4) * 90; // 每个按钮高度+垂直间距
        int requiredWidth = 4 * 80; // 每个按钮宽度+水平间距
        tilePalettePanel.setPreferredSize(new Dimension(requiredWidth, requiredHeight));
        tilePalettePanel.setBackground(Color.WHITE); // 设置背景色使面板更清晰可见

        JScrollPane paletteScrollPane = new JScrollPane(tilePalettePanel);
        paletteScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        // 提高调色板滚动速度
        paletteScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        paletteScrollPane.getVerticalScrollBar().setBlockIncrement(100);
        // 确保视口能正确追踪内容尺寸变化
        paletteScrollPane.getViewport().setBackground(Color.WHITE);
        // 明确设置滚动条策略
        paletteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        paletteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // 实体编辑面板 (初始化类成员变量)
        entityPanel = new JPanel(new BorderLayout(5, 5));
        entityPanel.setBorder(BorderFactory.createTitledBorder("实体编辑"));
        entityPanel.setVisible(false); // 默认隐藏

        // 创建一个专门用于容纳调色板相关组件的面板
        JPanel paletteContainer = new JPanel(new BorderLayout());
        paletteContainer.add(paletteHeader, BorderLayout.NORTH);
        paletteContainer.add(paletteScrollPane, BorderLayout.CENTER);
        
        // 修改 modePanel 的事件监听器，添加显示/隐藏逻辑
        editModeRadio.addActionListener(e -> {
            setEditMode();
            // 显示方块调色板，隐藏实体面板
            paletteContainer.setVisible(true);
            entityPanel.setVisible(false);
            rightPanel.revalidate();
            rightPanel.repaint();
        });

        mouseModeRadio.addActionListener(e -> {
            setMouseMode();
            // 隐藏方块调色板和实体面板
            paletteContainer.setVisible(false);
            entityPanel.setVisible(false);
            rightPanel.revalidate();
            rightPanel.repaint();
        });

        entityModeRadio.addActionListener(e -> {
            setEntityMode();
            // 隐藏方块调色板，显示实体面板
            paletteContainer.setVisible(false);
            entityPanel.setVisible(true);
            rightPanel.revalidate();
            rightPanel.repaint();
        });

        // 初始化时根据默认模式设置可见性
        paletteContainer.setVisible(true);
        entityPanel.setVisible(false);

        JPanel entityTop = new JPanel(new GridLayout(4, 1, 3, 3));

        // 类别标签
        categoryLabel = new JLabel("类别: OBJ");
        entityTop.add(categoryLabel);

        // 类型选择
        String[] entityTypes = {
                // IT 类型 (环境/物品)
                "IT_DestructibleWall", "IT_DryTree", "IT_MetalPlate", "IT_Trunk",

                // MON 类型 (怪物)
                "MON_Bat", "MON_GreenSlime", "MON_Orc", "MON_RedSlime", "MON_SkeletonLord",

                // NPC 类型 (NPC角色)
                "NPC_BigRock", "NPC_Merchant", "NPC_OldMan",

                // OBJ 类型 (可交互对象)
                "OBJ_Axe", "OBJ_Boots", "OBJ_Chest", "OBJ_Coin_Bronze", "OBJ_Door",
                "OBJ_Door_Iron", "OBJ_Fireball", "OBJ_Heart", "OBJ_Key", "OBJ_Lantern",
                "OBJ_ManaCrystal", "OBJ_Pickaxe", "OBJ_Potion_Red", "OBJ_Rock",
                "OBJ_Shield_Blue", "OBJ_Sword_Normal", "OBJ_Tent"
        };

        // 创建带图标的支持渲染的combobox
        typeCombo = new JComboBox<>(entityTypes);
        typeCombo.setPreferredSize(new Dimension(200, 30)); // 设置下拉框尺寸
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof String) {
                    String entityType = (String) value;
                    setText(entityType);

                    // 获取实体图像
                    BufferedImage entityImage = entityImages.getImage(entityType);
                    if (entityImage != null) {
                        // 缩放图像以适应显示 (增大到24x24像素)
                        Image scaledImage = entityImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    } else {
                        setIcon(null);
                    }
                }

                return this;
            }
        });

        typeCombo.addActionListener(e -> {
            selectedEntityType = (String) typeCombo.getSelectedItem();
            String category = determineCategory(selectedEntityType);
            categoryLabel.setText("类别: " + category);

            // 根据类型显示额外参数输入框
            boolean isChest = selectedEntityType.equals("OBJ_Chest");
            extraPanel.setVisible(isChest);

            // 处理箱子的战利品警告
            if (isChest) {
                // 找到警告标签并根据选择显示/隐藏
                Component[] components = extraPanel.getComponents();
                JLabel warningLabel = null;
                for (Component component : components) {
                    if (component instanceof JLabel && ((JLabel) component).getText().startsWith("!!!")) {
                        warningLabel = (JLabel) component;
                        break;
                    }
                }

                if (warningLabel != null) {
                    // 检查当前选择的战利品是否为"无"
                    Object selectedItem = extraCombo.getSelectedItem();
                    if (selectedItem != null && selectedItem.equals("无")) {
                        warningLabel.setVisible(true);
                    } else {
                        warningLabel.setVisible(false);
                    }
                }
            }
        });
        selectedEntityType = (String) typeCombo.getSelectedItem();
        entityTop.add(new JLabel("类型:"));
        entityTop.add(typeCombo);

        // 额外参数面板（用于箱子等需要额外参数的实体）
        extraPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extraPanel.add(new JLabel("战利品:"));

        // 创建战利品下拉框
        String[] lootTypes = {
                "无",
                // OBJ 类型 (可交互对象) - 按字典序排列
                "OBJ_Axe", "OBJ_Boots", "OBJ_Coin_Bronze", "OBJ_Door", "OBJ_Door_Iron",
                "OBJ_Fireball", "OBJ_Heart", "OBJ_Key", "OBJ_Lantern", "OBJ_ManaCrystal",
                "OBJ_Pickaxe", "OBJ_Potion_Red", "OBJ_Rock", "OBJ_Shield_Blue",
                "OBJ_Sword_Normal", "OBJ_Tent"
        };
        extraCombo = new JComboBox<>(lootTypes);
        extraCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof String) {
                    String entityType = (String) value;
                    // 对于"无"选项不显示图标
                    if (!entityType.equals("无")) {
                        setText(entityType);

                        // 获取实体图像
                        BufferedImage entityImage = entityImages.getImage(entityType);
                        if (entityImage != null) {
                            // 缩放图像以适应显示
                            Image scaledImage = entityImage.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                            setIcon(new ImageIcon(scaledImage));
                        } else {
                            setIcon(null);
                        }
                    } else {
                        setText(entityType);
                        setIcon(null); // "无"选项不显示图标
                    }
                }

                return this;
            }
        });
        extraCombo.setSelectedIndex(0);
        extraCombo.addActionListener(e -> {
            // 检查当前是否选择了箱子类型
            if (selectedEntityType != null && selectedEntityType.equals("OBJ_Chest")) {
                // 找到警告标签并根据选择显示/隐藏
                Component[] components = extraPanel.getComponents();
                JLabel warningLabel = null;
                for (Component component : components) {
                    if (component instanceof JLabel && ((JLabel) component).getText().startsWith("!!!")) {
                        warningLabel = (JLabel) component;
                        break;
                    }
                }

                if (warningLabel != null) {
                    // 检查选择的战利品是否为"无"
                    Object selectedItem = extraCombo.getSelectedItem();
                    if (selectedItem != null && selectedItem.equals("无")) {
                        warningLabel.setVisible(true);
                    } else {
                        warningLabel.setVisible(false);
                    }
                }
            }
        });

        // 创建醒目的警告标签，显示三个感叹号和文字提示
        JLabel warningLabel = new JLabel("!!! 请选择战利品");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14)); // 使用支持中文的字体
        warningLabel.setVisible(false); // 默认隐藏
        warningLabel.setToolTipText("请为箱子选择一个战利品");

        extraPanel.add(extraCombo);
        extraPanel.add(warningLabel);
        extraPanel.setVisible(false); // 默认隐藏
        entityTop.add(extraPanel);

        entityPanel.add(entityTop, BorderLayout.NORTH);

        // 添加清空实体按钮
        JButton clearEntitiesBtn = new JButton("清空实体");
        clearEntitiesBtn.addActionListener(e -> {
            entityEntries.clear();
            refreshMapDisplay();
            updateStatus("已清空所有实体");
        });
        entityPanel.add(clearEntitiesBtn, BorderLayout.SOUTH);

        // 调整组件添加顺序和方式
        rightPanel.add(entityPanel, BorderLayout.NORTH);
        rightPanel.add(paletteContainer, BorderLayout.CENTER);

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        setupKeyboardShortcuts();

        // 初始化调色板按钮
        createPaletteButtons();

        SwingUtilities.invokeLater(() -> {
            paletteHeader.setVisible(true);
            paletteScrollPane.setVisible(true);
            entityPanel.setVisible(false);
            rightPanel.revalidate();
            rightPanel.repaint();
        });
    }

    private String determineCategory(String typeName) {
        if (typeName.startsWith("OBJ_")) {
            return "OBJ";
        } else if (typeName.startsWith("NPC_")) {
            return "NPC";
        } else if (typeName.startsWith("MON_")) {
            return "MON";
        } else if (typeName.startsWith("IT_")) {
            return "IT";
        }
        return "OBJ"; // 默认
    }

    private boolean hasEntityAt(int x, int y) {
        for (EntityEntry entry : entityEntries) {
            if (entry.getX() == x && entry.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void addEntityAt(String category, String type, int x, int y, String extra) {
        // 移除该位置已存在的实体
        entityEntries.removeIf(entry -> entry.getX() == x && entry.getY() == y);

        // 添加新实体
        entityEntries.add(new EntityEntry(category, type, x, y, extra));
        setDirty(true);
    }

    private void removeEntityAt(int x, int y) {
        entityEntries.removeIf(entry -> entry.getX() == x && entry.getY() == y);
        setDirty(true);
    }

    private void createPaletteButtons() {
        tilePalettePanel.removeAll();

        for (int i = 0; i <= 44; i++) {
            final int tileId = i;
            TileInfo tile = tileSet.getTile(tileId);
            if (tile == null)
                continue;

            JButton tileButton = new JButton(tile.getName());
            tileButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            tileButton.setHorizontalTextPosition(SwingConstants.CENTER);
            tileButton.setPreferredSize(new Dimension(75, 85));
            tileButton.setMaximumSize(new Dimension(75, 85));
            tileButton.setMinimumSize(new Dimension(75, 85));
            tileButton.setFont(new Font("Arial", Font.PLAIN, 10));
            tileButton.setToolTipText("ID: " + tileId + " - " + tile.getName() +
                    " | 碰撞: " + (tile.hasCollision() ? "有" : "无"));

            // 设置图像预览（优先使用真实图像，若无使用颜色块）
            if (tile.getImage() != null) {
                Image img = tile.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                tileButton.setIcon(new ImageIcon(img));
            } else {
                BufferedImage bi = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = bi.createGraphics();
                g2.setColor(tile.getDisplayColor());
                g2.fillRect(0, 0, 50, 50);
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, 49, 49);
                g2.dispose();
                tileButton.setIcon(new ImageIcon(bi));
            }

            tileButton.addActionListener(e -> {
                selectedTile = tileId;
                updateSelectedTileButton();
                updateStatus();
            });

            tilePalettePanel.add(tileButton);

            if (selectedTileButton == null && tileId == 0) {
                selectedTile = 0;
                tileButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                selectedTileButton = tileButton;
            }
        }

        tilePalettePanel.revalidate();
        tilePalettePanel.repaint();
    }

    private void filterTiles(String filter) {
        tilePalettePanel.removeAll();

        for (int i = 0; i <= 44; i++) {
            final int tileId = i;
            TileInfo tile = tileSet.getTile(tileId);
            if (tile == null)
                continue;

            if (filter != null && !filter.isEmpty() &&
                    !tile.getName().toLowerCase().contains(filter.toLowerCase()) &&
                    !String.valueOf(tileId).contains(filter)) {
                continue;
            }

            JButton tileButton = new JButton(tile.getName());
            tileButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            tileButton.setHorizontalTextPosition(SwingConstants.CENTER);
            tileButton.setPreferredSize(new Dimension(75, 85));
            tileButton.setMaximumSize(new Dimension(75, 85));
            tileButton.setMinimumSize(new Dimension(75, 85));
            tileButton.setFont(new Font("Arial", Font.PLAIN, 10));
            tileButton.setToolTipText("ID: " + tileId + " - " + tile.getName() +
                    " | 碰撞: " + (tile.hasCollision() ? "有" : "无"));

            if (tile.getImage() != null) {
                Image img = tile.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                tileButton.setIcon(new ImageIcon(img));
            } else {
                BufferedImage bi = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = bi.createGraphics();
                g2.setColor(tile.getDisplayColor());
                g2.fillRect(0, 0, 50, 50);
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, 49, 49);
                g2.dispose();
                tileButton.setIcon(new ImageIcon(bi));
            }

            tileButton.addActionListener(e -> {
                selectedTile = tileId;
                updateSelectedTileButton();
                updateStatus();
            });

            if (tileId == selectedTile) {
                tileButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                selectedTileButton = tileButton;
            }

            tilePalettePanel.add(tileButton);
        }

        tilePalettePanel.revalidate();
        tilePalettePanel.repaint();
    }

    private void updateSelectedTileButton() {
        if (selectedTileButton != null) {
            selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        for (Component comp : tilePalettePanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String tooltip = button.getToolTipText();
                if (tooltip != null && tooltip.startsWith("ID: " + selectedTile)) {
                    button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                    selectedTileButton = button;
                    break;
                }
            }
        }
    }

    private void refreshMapDisplay() {
        if (mapPanel != null) {
            mapPanel.revalidate();
            mapPanel.repaint();
        }
    }

    private void setupKeyboardShortcuts() {
        // 获取整个窗口的输入映射和动作映射
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        // 为撤销操作设置快捷键 Ctrl+Z
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        // 为重做操作设置快捷键 Ctrl+Y
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        // 其他快捷键...
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
        actionMap.put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
                "saveAs");
        actionMap.put("saveAs", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapAsAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "toggleMouseMode");
        actionMap.put("toggleMouseMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseModeRadio.setSelected(true);
            }
        });

        // 实体编辑模式快捷键
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "toggleEntityMode");
        actionMap.put("toggleEntityMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entityModeRadio.setSelected(true);
            }
        });
    }

    private void loadMap(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this,
                        "地图文件不存在: " + filename,
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int y = 0;

            // 清空实体列表
            entityEntries.clear();

            // 读取地图数据
            boolean readingEntities = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 跳过空行和注释行（除非是在读取实体部分）
                if (line.isEmpty() || line.startsWith("#")) {
                    if (line.equals("#Entity")) {
                        readingEntities = true;
                        continue;
                    }
                    // 如果不是实体标记且不在实体部分，则跳过
                    if (!readingEntities) {
                        continue;
                    }
                }

                if (readingEntities) {
                    // 读取实体数据
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String category = parts[0].trim();
                        String type = parts[1].trim();
                        int x = Integer.parseInt(parts[2].trim());
                        int yy = Integer.parseInt(parts[3].trim());
                        String extra = parts.length > 4 ? parts[4].trim() : null;

                        entityEntries.add(new EntityEntry(category, type, x, yy, extra));
                    }
                } else if (y < MAP_HEIGHT) {
                    // 读取地图数据
                    String[] numbers = line.split("\\s+");
                    for (int x = 0; x < Math.min(numbers.length, MAP_WIDTH); x++) {
                        try {
                            mapData[y][x] = Integer.parseInt(numbers[x]);
                        } catch (NumberFormatException e) {
                            mapData[y][x] = 0;
                        }
                    }
                    y++;
                }
            }

            reader.close();
            refreshMapDisplay();
            currentMapFile = filename;
            mapFilePathField.setText(new File(filename).getName());
            undoStack.clear();
            redoStack.clear();
            updateStatus("地图已加载: " + new File(filename).getName());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "加载地图失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMap(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            // 保存地图数据
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    writer.write(String.valueOf(mapData[y][x]));
                    if (x < MAP_WIDTH - 1) {
                        writer.write(" ");
                    }
                }
                if (y < MAP_HEIGHT - 1) {
                    writer.newLine();
                }
            }

            // 保存实体数据
            if (!entityEntries.isEmpty()) {
                writer.newLine();
                writer.write("#Entity");
                writer.newLine();
                for (EntityEntry entry : entityEntries) {
                    writer.write(entry.toString());
                    writer.newLine();
                }
            }

            writer.close();
            currentMapFile = filename;
            mapFilePathField.setText(new File(filename).getName());
            updateStatus("地图已保存: " + new File(filename).getName());

            // 添加这两行
            setDirty(false);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "保存地图失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMapAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(mapsFolder));
        fileChooser.setDialogTitle("选择地图文件");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "文本文件 (*.txt)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadMap(fileChooser.getSelectedFile().getPath());
        }
    }

    void saveMapAction() {
        saveMap(currentMapFile);
    }

    void saveMapAsAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(mapsFolder));
        fileChooser.setDialogTitle("另存为地图文件");
        fileChooser.setSelectedFile(new File("map.txt"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "文本文件 (*.txt)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }
            saveMap(filename);
            // 更新UI显示
            mapFilePathField.setText(new File(filename).getName());
        }
    }

    private void newMapAction() {
        int result = JOptionPane.showConfirmDialog(this,
                "创建新地图会丢失当前未保存的修改，是否继续？",
                "新建地图",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            clearMap();
            String newName = JOptionPane.showInputDialog(this, "输入新地图文件名:", "新建地图",
                    JOptionPane.QUESTION_MESSAGE);
            if (newName != null && !newName.trim().isEmpty()) {
                if (!newName.endsWith(".txt")) {
                    newName += ".txt";
                }
                currentMapFile = mapsFolder + newName;
                mapFilePathField.setText(newName); // 更新UI显示
                undoStack.clear();
                redoStack.clear();
                updateStatus("新建地图: " + newName);
            }
        }
    }

    private void clearMap() {
        saveCurrentState();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                mapData[y][x] = 0;
            }
        }
        entityEntries.clear();
        refreshMapDisplay();
        updateStatus("地图已清空");
        setDirty(true); // 添加这一行
    }

    private void updateStatus() {
        String modeText;
        if (entityModeRadio.isSelected()) {
            modeText = "实体编辑模式";
        } else if (mouseMode) {
            modeText = "鼠标模式";
        } else if (selectedTile >= 0) {
            modeText = "方块编辑模式";
        } else {
            modeText = "未选择";
        }

        if (selectedTile >= 0 && !modeText.equals("实体编辑模式")) {
            TileInfo tile = tileSet.getTile(selectedTile);
            if (tile != null) {
                String status = "模式: " + modeText + " | 选中: ID=" + selectedTile + " 名称=" + tile.getName() +
                        " 碰撞=" + (tile.hasCollision() ? "有" : "无") +
                        " | 地图: " + new File(currentMapFile).getName() +
                        " | 实体数: " + entityEntries.size() +
                        " | 撤销栈: " + undoStack.size() + " 重做栈: " + redoStack.size();
                statusLabel.setText(status);
            }
        } else {
            String status = "模式: " + modeText +
                    " | 地图: " + new File(currentMapFile).getName() +
                    " | 实体数: " + entityEntries.size() +
                    " | 撤销栈: " + undoStack.size() + " 重做栈: " + redoStack.size();
            statusLabel.setText(status);
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // 启动主窗口管理器而不是直接启动MapEditor
            new MapEditorManager().setVisible(true);
        });
    }

    private EntityEntry getEntityAt(int x, int y) {
        for (EntityEntry entry : entityEntries) {
            if (entry.getX() == x && entry.getY() == y) {
                return entry;
            }
        }
        return null;
    }

    public String getCurrentMapFile() {
        return currentMapFile;
    }

    public boolean isDirty() {
        return dirty;
    }

    private void setDirty(boolean dirty) {
        this.dirty = dirty;

        // 通知 manager 更新选项卡标题
        if (manager != null) {
            manager.notifyEditorChanged(this);
        }
    }

    public void setTabIndex(int index) {
        this.tabIndex = index;
    }
}