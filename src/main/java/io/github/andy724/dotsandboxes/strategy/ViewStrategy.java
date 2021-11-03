package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ViewStrategy implements GameStrategy{
    private final GameStrategy force;
    private final int width,height;

    public ViewStrategy(int width, int height){
        this.width = width;
        this.height = height;
        // initialize a 3x3 game board for view calculation
        Node[][] nodes = new Node[height][width];
        for(int x = 0;x < width;x++)
            for(int y = 0;y < height;y++)
                nodes[y][x] = new Node(x,y);
        for(int y = 0;y < height;y++){
            for(int x = 0;x < width;x++){
                Node node = nodes[y][x];
                if(x - 1 >= 0) node.left = nodes[y][x - 1].right;
                if(x + 1 < width) node.right = new Edge(node,nodes[y][x + 1]);
                if(y - 1 >= 0) node.up = nodes[y - 1][x].down;
                if(y + 1 < height) node.down = new Edge(node,nodes[y + 1][x]);
            }
        }
        force = new BruteForceStrategy(nodes);
    }

    @Override public @NotNull WeightedEdge choose(@NotNull BoardView board){
        BoardView view = board.resize(width,height);
        WeightedEdge max = null;
        for(int y = 0;y <= board.height() - height;y++){
            for(int x = 0;x <= board.width() - width;x++){
                view.shift(x,y);
                try{
                    WeightedEdge result = force.choose(view);
                    if(max == null || max.weight < result.weight)
                        max = result;
                } catch(@NotNull IllegalStateException ignored){}
            }
        }
        return Objects.requireNonNull(max);
    }

    @Override public @NotNull BoardView view(@NotNull Node[][] board){
        return new BoardView(board,board[0].length,board.length);
    }
}
