package io.github.andy724.dotsandboxes;

import org.jetbrains.annotations.NotNull;

public enum Player{
    ONE("1"),TWO("2");

    private final String name;

    Player(@NotNull String name){
        this.name = name;
    }

    public @NotNull Player next(){
        return values()[(ordinal() + 1) % 2];
    }


    @Override public String toString(){
        return name;
    }
}