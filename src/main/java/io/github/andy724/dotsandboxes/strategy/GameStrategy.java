package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import org.jetbrains.annotations.NotNull;

public interface GameStrategy{
    @NotNull Edge choose(@NotNull Node[][] board);
}
