package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements GameStrategy{
    @Override public @NotNull Edge choose(@NotNull Node[][] board){
        Edge chosen = null;
        do{
            int x = ThreadLocalRandom.current().nextInt(board[0].length);
            int y = ThreadLocalRandom.current().nextInt(board.length);
            boolean vertical = ThreadLocalRandom.current().nextBoolean();
            if((x == board[0].length - 1 && !vertical) ||
               (y == board.length - 1 && vertical)
            ){
                continue;
            }
            Node node = board[y][x];
            chosen = vertical ? node.down : node.right;
        } while(chosen == null || chosen.active);
        return chosen;
    }
}
