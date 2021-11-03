package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import org.jetbrains.annotations.NotNull;

public interface GameStrategy{
    @NotNull WeightedEdge choose(@NotNull BoardView board);
    @NotNull BoardView view(@NotNull Node[][] board);
}
