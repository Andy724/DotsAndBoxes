package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import io.github.andy724.dotsandboxes.board.view.BoardView.NodeView;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TitForTatStrategy implements GameStrategy{
    @Override public @NotNull WeightedEdge choose(@NotNull BoardView board){
        if(board.width() % 2 == 0 || board.height() % 2 == 0)
            throw new IllegalArgumentException("Invalid board dimensions");
        Edge result = null;
        for(int x = 0;x < board.width();x++){
            for(int y = 0;y < board.height() - x;y++){
                NodeView first = board.at(x,y);
                NodeView second = board.at(board.width() - x - 1,board.height() - y - 1);
                if(first.left() != null && (first.left().isActive() ^ second.right().isActive())){
                    Edge edge = (second.right().isActive() ? first.left() : second.right()).base();
                    if(boxesFilled(edge, board.height(), board.width()) > 0){
                        return new WeightedEdge(edge, 0);
                    }
                    result = edge;
                }
                if(first.up() != null && (first.up().isActive() ^ second.down().isActive())){
                    Edge edge = (second.down().isActive() ? first.up() : second.down()).base();
                    if(boxesFilled(edge, board.height(), board.width()) > 0){
                        return new WeightedEdge(edge, 0);
                    }
                    result = edge;
                }
                if(first.right() != null && (first.right().isActive() ^ second.left().isActive())){
                    Edge edge = (second.left().isActive() ? first.right() : second.left()).base();
                    if(boxesFilled(edge, board.height(), board.width()) > 0){
                        return new WeightedEdge(edge, 0);
                    }
                    result = edge;
                }
                if(first.down() != null && (first.down().isActive() ^ second.up().isActive())){
                    Edge edge = (second.up().isActive() ? first.down() : second.up()).base();
                    if(boxesFilled(edge, board.height(), board.width()) > 0){
                        return new WeightedEdge(edge, 0);
                    }
                    result = edge;
                }
            }
        }
        if(result == null)
            throw new IllegalArgumentException("Only player 2 may use this strategy");
        return new WeightedEdge(result,0);
    }

    private static int boxesFilled(@NotNull Edge edge, int h, int w) {
        int result = 0;
        switch(edge.orientation()){
            case HORIZONTAL -> {
                Node left = edge.from;
                Node right = edge.to;
                if(left.y != 0 && left.up.active && left.up.from.right.active && right.up.active) result++;
                if(right.y != h - 1 && left.down.active && left.down.to.right.active && right.down.active) result++;
            }
            case VERTICAL -> {
                Node top = edge.from;
                Node bottom = edge.to;
                if(top.x != 0 && top.left.active && top.left.from.down.active && bottom.left.active) result++;
                if(top.x != w - 1 && top.right.active && top.right.to.down.active && bottom.right.active) result++;
            }
        }
        return result;
    }


    @Override public @NotNull BoardView view(@NotNull Node[][] board){
        return new BoardView(board,board[0].length,board.length);
    }
}
