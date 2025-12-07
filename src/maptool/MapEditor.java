package maptool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Stack;

public class MapEditor extends JFrame {
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;
    private static final int TILE_SIZE = 32;

    private int[][] mapData;
    private HashMap<Integer, TileInfo> tileSet;
    private JPanel[][] tilePanels;

    private JPanel mapPanel;
    private JPanel tilePalettePanel;
    private JScrollPane mapScrollPane;
    private JLabel statusLabel;
    private JTextField mapFilePathField;

    private int selectedTile = 0;
    private JButton selectedTileButton;
    private JToggleButton mouseModeButton;
    private JToggleButton coordsButton;

    private String currentMapFile = "res/maps/map.txt";
    private String tilesFolder = "res/tiles/";
    private String mapsFolder = "res/maps/";

    private boolean showCoordinates = false;
    private boolean mouseMode = false;

    private Stack<int[][]> undoStack = new Stack<>();
    private Stack<int[][]> redoStack = new Stack<>();
    private final int MAX_UNDO_STEPS = 50;

    private class TileInfo {
        String name;
        boolean hasCollision;
        BufferedImage image;
        Color displayColor;

        TileInfo(String name, boolean hasCollision) {
            this.name = name;
            this.hasCollision = hasCollision;
            this.image = null;
            this.displayColor = generateColor(name);
        }

        private Color generateColor(String name) {
            int hash = name.hashCode();
            return new Color(
                    Math.abs((hash & 0xFF0000) >> 16) % 200 + 55,
                    Math.abs((hash & 0x00FF00) >> 8) % 200 + 55,
                    Math.abs(hash & 0x0000FF) % 200 + 55);
        }
    }

    public MapEditor() {
        mapData = new int[MAP_HEIGHT][MAP_WIDTH];
        tileSet = new HashMap<>();
        tilePanels = new JPanel[MAP_HEIGHT][MAP_WIDTH];

        initUI();
        loadTileSet();

        ensureMapsFolder();

        File defaultMap = new File(currentMapFile);
        if (defaultMap.exists()) {
            loadMap(currentMapFile);
        } else {
            clearMap();
            updateStatus("新建地图文件");
        }

        setTitle("Blue Boy Adventure Map Editor - " + MAP_WIDTH + "x" + MAP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
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
    }

    private void undo() {
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

    private void redo() {
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
        if (mouseModeButton != null) {
            mouseModeButton.setSelected(false);
        }
        updateStatus();
    }

    private void initUI() {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(800);
        mainSplitPane.setResizeWeight(0.7);
        add(mainSplitPane);

        JPanel leftPanel = new JPanel(new BorderLayout());

        mapPanel = new JPanel(new GridLayout(MAP_HEIGHT, MAP_WIDTH, 0, 0));
        mapPanel.setPreferredSize(new Dimension(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE));

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                final int posX = x;
                final int posY = y;

                JPanel tilePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        int tileId = mapData[posY][posX];
                        TileInfo tile = tileSet.get(tileId);

                        if (tile != null && tile.image != null) {
                            g.drawImage(tile.image, 0, 0, TILE_SIZE, TILE_SIZE, this);
                        } else if (tile != null) {
                            g.setColor(tile.displayColor);
                            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        } else {
                            g.setColor(Color.PINK);
                            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        }

                        g.setColor(Color.GRAY);
                        g.drawRect(0, 0, TILE_SIZE - 1, TILE_SIZE - 1);

                        if (tile != null && tile.hasCollision) {
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

                        if (mouseMode) {
                            g.setColor(new Color(0, 255, 0, 100));
                            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        }
                    }
                };

                tilePanel.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tilePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                tilePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (mouseMode || e.getButton() != MouseEvent.BUTTON1) {
                            return;
                        }
                        saveCurrentState();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (mouseMode) {
                            int tileId = mapData[posY][posX];
                            TileInfo tile = tileSet.get(tileId);
                            if (tile != null) {
                                String info = "位置: (" + posX + ", " + posY + ")\n" +
                                        "方块ID: " + tileId + "\n" +
                                        "名称: " + tile.name + "\n" +
                                        "碰撞: " + (tile.hasCollision ? "有" : "无");
                                JOptionPane.showMessageDialog(MapEditor.this, info,
                                        "方块信息", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            int tileId = mapData[posY][posX];
                            TileInfo tile = tileSet.get(tileId);
                            if (tile != null) {
                                selectedTile = tileId;
                                updateSelectedTileButton();
                                updateStatus();
                            }
                        } else if (selectedTile >= 0) {
                            mapData[posY][posX] = selectedTile;
                            tilePanel.repaint();
                            updateStatus();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (mouseMode) {
                            tilePanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                            tilePanel.repaint();
                        } else {
                            tilePanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        tilePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        tilePanel.repaint();
                    }
                });

                tilePanel.addMouseMotionListener(new MouseMotionAdapter() {
                    private boolean isDragging = false;

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (mouseMode || selectedTile < 0)
                            return;

                        if (!isDragging) {
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
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel fileToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel optionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadButton = new JButton("加载");
        loadButton.addActionListener(e -> loadMapAction());

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveMapAction());

        JButton saveAsButton = new JButton("另存");
        saveAsButton.addActionListener(e -> saveMapAsAction());

        JButton newButton = new JButton("新建");
        newButton.addActionListener(e -> newMapAction());

        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearMap());

        JButton undoButton = new JButton("撤销");
        undoButton.addActionListener(e -> undo());

        JButton redoButton = new JButton("重做");
        redoButton.addActionListener(e -> redo());

        JButton clearSelectButton = new JButton("取消选择");
        clearSelectButton.addActionListener(e -> clearSelection());

        mapFilePathField = new JTextField(new File(currentMapFile).getName(), 20);
        mapFilePathField.setEditable(false);

        coordsButton = new JToggleButton("坐标");
        coordsButton.setSelected(showCoordinates);
        coordsButton.addActionListener(e -> {
            showCoordinates = coordsButton.isSelected();
            refreshMapDisplay();
        });

        mouseModeButton = new JToggleButton("鼠标模式");
        mouseModeButton.addActionListener(e -> {
            mouseMode = mouseModeButton.isSelected();
            if (mouseMode) {
                if (selectedTileButton != null) {
                    selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                }
                selectedTile = -1;
                selectedTileButton = null;
            }
            refreshMapDisplay();
            updateStatus();
        });

        JButton helpButton = new JButton("帮助");
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(MapEditor.this,
                    "操作指南：\n" +
                            "• 左键：放置方块\n" +
                            "• 右键：选择方块\n" +
                            "• 拖拽：连续绘制\n" +
                            "• 鼠标模式：查看方块信息\n" +
                            "• 取消选择：退出编辑模式\n" +
                            "• 坐标按钮：显示/隐藏坐标\n" +
                            "• 撤销/重做：Ctrl+Z/Ctrl+Y\n" +
                            "• ID始终显示在方块左上角\n" +
                            "• 碰撞方块有红色标记",
                    "操作指南",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        fileToolbar.add(new JLabel("地图:"));
        fileToolbar.add(mapFilePathField);
        fileToolbar.add(newButton);
        fileToolbar.add(loadButton);
        fileToolbar.add(saveButton);
        fileToolbar.add(saveAsButton);
        fileToolbar.add(clearButton);
        fileToolbar.add(undoButton);
        fileToolbar.add(redoButton);
        fileToolbar.add(clearSelectButton);

        optionToolbar.add(new JLabel("选项:"));
        optionToolbar.add(coordsButton);
        optionToolbar.add(mouseModeButton);
        optionToolbar.add(helpButton);

        toolbar.add(fileToolbar, BorderLayout.NORTH);
        toolbar.add(optionToolbar, BorderLayout.SOUTH);

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

        JScrollPane paletteScrollPane = new JScrollPane(tilePalettePanel);
        paletteScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("选中方块信息"));
        JTextArea infoArea = new JTextArea(4, 20);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        rightPanel.add(paletteHeader, BorderLayout.NORTH);
        rightPanel.add(paletteScrollPane, BorderLayout.CENTER);
        rightPanel.add(infoPanel, BorderLayout.SOUTH);

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        setupKeyboardShortcuts();
    }

    private void loadTileSet() {
        setup(0, "grass00", false);
        setup(1, "grass00", false);
        setup(2, "grass00", false);
        setup(3, "grass00", false);
        setup(4, "grass00", false);
        setup(6, "grass00", false);
        setup(7, "grass00", false);
        setup(8, "grass00", false);
        setup(9, "grass00", false);

        setup(10, "grass00", false);
        setup(11, "grass01", false);
        setup(12, "water00", true);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);
        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);
        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);
        setup(40, "wall", true);
        setup(41, "tree", true);
        setup(42, "hut", false);
        setup(43, "floor01", false);
        setup(44, "table01", true);

        createPaletteButtons();
    }

    private void setup(int id, String name, boolean hasCollision) {
        TileInfo tile = new TileInfo(name, hasCollision);
        tileSet.put(id, tile);

        try {
            String imagePath = tilesFolder + name + ".png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                tile.image = ImageIO.read(imageFile);
            } else {
                System.out.println("警告: 图像文件不存在: " + imagePath);
            }
        } catch (IOException e) {
            System.out.println("无法加载图像: " + name + ".png");
        }
    }

    private void createPaletteButtons() {
        tilePalettePanel.removeAll();

        for (int i = 0; i <= 44; i++) {
            final int tileId = i;
            TileInfo tile = tileSet.get(tileId);
            if (tile == null)
                continue;

            JButton tileButton = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int size = Math.min(getWidth(), getHeight()) - 10;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;

                    if (tile.image != null) {
                        g.drawImage(tile.image, x, y, size, size, this);
                    } else {
                        g.setColor(tile.displayColor);
                        g.fillRect(x, y, size, size);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, size, size);
                    }

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 10));
                    String idStr = String.valueOf(tileId);
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(idStr);
                    g.drawString(idStr, 5, 12);

                    if (tile.hasCollision) {
                        g.setColor(Color.RED);
                        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    }

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 12));
                    String name = tile.name;
                    int nameWidth = g.getFontMetrics().stringWidth(name);
                    g.drawString(name, (getWidth() - nameWidth) / 2, getHeight() - 5);
                }
            };

            tileButton.setPreferredSize(new Dimension(70, 70));
            tileButton.setToolTipText("ID: " + tileId + " - " + tile.name +
                    " | 碰撞: " + (tile.hasCollision ? "有" : "无"));

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
            TileInfo tile = tileSet.get(tileId);
            if (tile == null)
                continue;

            if (!filter.isEmpty() &&
                    !tile.name.toLowerCase().contains(filter.toLowerCase()) &&
                    !String.valueOf(tileId).contains(filter)) {
                continue;
            }

            JButton tileButton = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int size = Math.min(getWidth(), getHeight()) - 10;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;

                    if (tile.image != null) {
                        g.drawImage(tile.image, x, y, size, size, this);
                    } else {
                        g.setColor(tile.displayColor);
                        g.fillRect(x, y, size, size);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, size, size);
                    }

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 10));
                    String idStr = String.valueOf(tileId);
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(idStr);
                    g.drawString(idStr, 5, 12);

                    if (tile.hasCollision) {
                        g.setColor(Color.RED);
                        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    }

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 8));
                    String name = tile.name;
                    int nameWidth = g.getFontMetrics().stringWidth(name);
                    g.drawString(name, (getWidth() - nameWidth) / 2, getHeight() - 5);
                }
            };

            tileButton.setPreferredSize(new Dimension(70, 70));
            tileButton.setToolTipText("ID: " + tileId + " - " + tile.name +
                    " | 碰撞: " + (tile.hasCollision ? "有" : "无"));

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

    private void handleTileClick(MouseEvent e, int x, int y) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int tileId = mapData[y][x];
            TileInfo tile = tileSet.get(tileId);
            if (tile != null) {
                selectedTile = tileId;
                updateSelectedTileButton();
                updateStatus();
            }
        } else {
            mapData[y][x] = selectedTile;
            tilePanels[y][x].repaint();
            updateStatus();
        }
    }

    private void refreshMapDisplay() {
        if (mapPanel != null) {
            mapPanel.revalidate();
            mapPanel.repaint();
        }
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("control S"), "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control O"), "open");
        actionMap.put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control N"), "new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newMapAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
        actionMap.put("saveAs", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapAsAction();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control C"), "toggleCoords");
        actionMap.put("toggleCoords", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCoordinates = !showCoordinates;
                if (coordsButton != null) {
                    coordsButton.setSelected(showCoordinates);
                }
                refreshMapDisplay();
                updateStatus("坐标显示: " + (showCoordinates ? "开启" : "关闭"));
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "clearSelection");
        actionMap.put("clearSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSelection();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control M"), "toggleMouseMode");
        actionMap.put("toggleMouseMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseMode = !mouseMode;
                if (mouseModeButton != null) {
                    mouseModeButton.setSelected(mouseMode);
                }
                if (mouseMode) {
                    if (selectedTileButton != null) {
                        selectedTileButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    }
                    selectedTile = -1;
                    selectedTileButton = null;
                }
                refreshMapDisplay();
                updateStatus();
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

            while ((line = reader.readLine()) != null && y < MAP_HEIGHT) {
                String[] numbers = line.trim().split("\\s+");
                for (int x = 0; x < Math.min(numbers.length, MAP_WIDTH); x++) {
                    try {
                        mapData[y][x] = Integer.parseInt(numbers[x]);
                    } catch (NumberFormatException e) {
                        mapData[y][x] = 0;
                    }
                }
                y++;
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

            writer.close();
            currentMapFile = filename;
            mapFilePathField.setText(new File(filename).getName());
            updateStatus("地图已保存: " + new File(filename).getName());

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

    private void saveMapAction() {
        saveMap(currentMapFile);
    }

    private void saveMapAsAction() {
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
                mapFilePathField.setText(newName);
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
        refreshMapDisplay();
        updateStatus("地图已清空");
    }

    private void updateStatus() {
        String modeText = mouseMode ? "鼠标模式" : (selectedTile >= 0 ? "编辑模式" : "未选择");
        if (selectedTile >= 0) {
            TileInfo tile = tileSet.get(selectedTile);
            if (tile != null) {
                String status = "模式: " + modeText + " | 选中: ID=" + selectedTile + " 名称=" + tile.name +
                        " 碰撞=" + (tile.hasCollision ? "有" : "无") +
                        " | 地图: " + new File(currentMapFile).getName() +
                        " | 撤销栈: " + undoStack.size() + " 重做栈: " + redoStack.size();
                statusLabel.setText(status);
            }
        } else {
            String status = "模式: " + modeText +
                    " | 地图: " + new File(currentMapFile).getName() +
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

        SwingUtilities.invokeLater(() -> new MapEditor());
    }
}