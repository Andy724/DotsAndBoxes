package io.github.andy724.dotsandboxes.board;

import org.jetbrains.annotations.NotNull;

public class WeightedEdge{
    public final Edge edge;
    public final int weight;

    public WeightedEdge(@NotNull Edge edge, int weight){
        this.edge = edge;
        this.weight = weight;
    }
}