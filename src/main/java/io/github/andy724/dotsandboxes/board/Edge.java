package io.github.andy724.dotsandboxes.board;

import org.jetbrains.annotations.NotNull;

public class Edge{
    public boolean active;
    public Node from,to;

    public Edge(Node from, Node to){
        this.from = from;
        this.to = to;
    }

    public Edge(Node from, Node to, boolean active){
        this.from = from;
        this.to = to;
        this.active = active;
    }

    public void activate(){
        active = true;
    }

    public void deactivate(){
        active = false;
    }

    public @NotNull Orientation orientation(){
        return from.x == to.x ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }

    public enum Orientation{
        HORIZONTAL,VERTICAL
    }
}