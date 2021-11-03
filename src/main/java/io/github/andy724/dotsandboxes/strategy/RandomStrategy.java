package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import io.github.andy724.dotsandboxes.board.view.BoardView.EdgeView;
import io.github.andy724.dotsandboxes.board.view.BoardView.NodeView;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements GameStrategy{
    @Override public @NotNull WeightedEdge choose(@NotNull BoardView board){
        EdgeView chosen = null;
        do{
            int x = ThreadLocalRandom.current().nextInt(board.width());
            int y = ThreadLocalRandom.current().nextInt(board.height());
            boolean vertical = ThreadLocalRandom.current().nextBoolean();
            if((x == board.width() - 1 && !vertical) ||
               (y == board.height() - 1 && vertical)
            ){
                continue;
            }
            NodeView node = board.at(x,y);
            chosen = vertical ? node.down() : node.right();
        } while(chosen == null || chosen.isActive());
        return new WeightedEdge(chosen.base(),0);
    }

    @Override public @NotNull BoardView view(@NotNull Node[][] board){
        return new BoardView(board,board[0].length,board.length);
    }

}
