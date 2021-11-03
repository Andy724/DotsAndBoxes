package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import io.github.andy724.dotsandboxes.board.view.BoardView.NodeView;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class PlayerStrategy implements GameStrategy{
    @Override public @NotNull WeightedEdge choose(@NotNull BoardView board){
        Edge result = null;
        Scanner scanner = new Scanner(System.in);
        while(result == null) {
            String[] args = scanner.nextLine().split(" ");
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            NodeView node = board.at(x,y);
            result = (switch (args[2]) {
                case "l" -> node.left();
                case "r" -> node.right();
                case "u" -> node.up();
                case "d" -> node.down();
                default -> null;
            }).base();
        }
        return new WeightedEdge(result,0);
    }

    @Override public @NotNull BoardView view(@NotNull Node[][] board){
        return new BoardView(board,board[0].length,board.length);
    }
}
