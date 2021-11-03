package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import org.jetbrains.annotations.NotNull;

public interface GameStrategy{
    @NotNull Edge choose(@NotNull Node[][] board);
}
