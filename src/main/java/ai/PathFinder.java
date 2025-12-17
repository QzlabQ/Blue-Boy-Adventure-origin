package ai;

import java.util.ArrayList;

import main.GamePanel;

public class PathFinder {
    GamePanel gp;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;
    public int maxSearchRange = 10;

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[gp.maxWorldCol][gp.maxWorldRow];

        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            node[col][row] = new Node(col, row);

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void resetNodes() {
        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // Reset open, checked and solid state
            node[col][row].open = false;
            node[col][row].checked = false;
            node[col][row].solid = false;

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }

        // reset other settings
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        resetNodes();

        // Set start and goal node
        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);

        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // Set solid node
            // check tiles
            int tileNum = gp.tileM.mapTileNum[gp.currentMap][col][row];
            if (gp.tileM.tile[tileNum].collision == true) {
                node[col][row].solid = true;
            }
            // check interactive tiles
            for (int i = 0; i < gp.iTile[1].length; i++) {
                if (gp.iTile[gp.currentMap][i] != null && gp.iTile[gp.currentMap][i].destructible == true) {
                    int itCol = gp.iTile[gp.currentMap][i].worldX / gp.tileSize;
                    int itRow = gp.iTile[gp.currentMap][i].worldY / gp.tileSize;
                    node[itCol][itRow].solid = true;
                }
            }
            // get cost
            getCost(node[col][row]);

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void getCost(Node node) {
        // G cost
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;
        // H cost
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;
        // F cost
        node.fCost = node.gCost + node.hCost;
    }

    public boolean search() {
        while (goalReached == false && step < 500) {
            int col = currentNode.col;
            int row = currentNode.row;

            // check the current node
            currentNode.checked = true;
            openList.remove(currentNode);

            // open the up node
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }
            // open the left node
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }
            // open the down node
            if (row + 1 < gp.maxWorldRow) {
                openNode(node[col][row + 1]);
            }
            // open the right node
            if (col + 1 < gp.maxWorldCol) {
                openNode(node[col + 1][row]);
            }

            // find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for (int i = 0; i < openList.size(); i++) {
                // check if this node's f cost is better
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                // if f cost is the same, check the g cost
                else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }
            // if there is no node in the openList, end the loop
            if (openList.size() == 0) {
                break;
            }

            // After the loop, openList[bestNodeIndex] is the next step (=currentNode)
            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            step++;
        }
        return goalReached;
    }

    public void openNode(Node node) {
        // 检查条件：未开放，未检查，非障碍物
        if (node.open == false && node.checked == false && node.solid == false) {

            // 【新增检查】: 检查该节点是否超出了最大搜索范围
            // 我们需要先计算这个节点的 G Cost
            // 由于在 setNodes 中只计算了初始节点的 G/H/F，扩展邻居时需要重新计算
            // 步骤 1: 临时计算 G Cost (因为它依赖于 parent 节点)

            // 计算从起点经过当前父节点 (currentNode) 到该节点的新 G Cost
            int tempGCost = currentNode.gCost + 1; // 假设相邻移动成本为 1

            // 【核心判断】: 如果 G Cost 超过了最大范围，则不加入 Open List
            if (tempGCost > maxSearchRange) {
                return; // 超出范围，不予探索
            }

            // 步骤 2: 更新节点的成本和父节点 (这部分必须在检查通过后执行)
            if (node.open == false || tempGCost < node.gCost) {
                // 只有当该节点是新的，或者找到了更短的路径时，才更新
                node.gCost = tempGCost;
                node.hCost = Math.abs(node.col - goalNode.col) + Math.abs(node.row - goalNode.row);
                node.fCost = node.gCost + node.hCost;
                node.parent = currentNode;

                if (node.open == false) {
                    node.open = true;
                    openList.add(node);
                }
            }
        }
    }

    public void trackThePath() {
        Node current = goalNode;
        while (current != startNode) {
            pathList.add(0, current);
            current = current.parent;
        }
    }
}
