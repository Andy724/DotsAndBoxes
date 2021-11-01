package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;

public class BruteForceStrategy implements GameStrategy{
    Map<Integer,WeightedEdge> cache;

    public BruteForceStrategy(@NotNull Node[][] nodes){
        cache = new HashMap<>();
        // actually loop through all possible boards
        // mapping game state to edges to draw
        Edge[] edges = edges(nodes);
        for(int state = 0;state < Math.pow(2,edges.length);state++){
            for(int i = 0;i < edges.length;i++)
                edges[i].active = (state & (1 << i)) == 0;
            // if there is only one deactivated state, set weighted edge at that point to the edge with a weight of 1
            Edge[] inactive = Arrays.stream(edges).filter(edge -> !edge.active).toArray(Edge[]::new);
            if(inactive.length == 1) {
                cache.put(hash(nodes), new WeightedEdge(inactive[0], 1));
            } else if(inactive.length != 0) { // else, go through the deactivated states
                cache.put(hash(nodes),
                        IntStream
                                .range(0,edges.length)
                                .filter(x -> !edges[x].active)
                                .mapToObj(x -> {
                                    // adds your possible points
                                    edges[x].activate();
                                    int nextScore = cache.get(hash(nodes)).weight;
                                    edges[x].deactivate();

                                    int wonBoxes = boxesFilled(edges,x, nodes.length, nodes[0].length);
                                    if(wonBoxes != 0)
                                        return new WeightedEdge(edges[x], wonBoxes + nextScore);
                                    else
                                        return new WeightedEdge(edges[x], -nextScore);

                                })
                                .sorted(comparingInt((WeightedEdge weighted) -> weighted.weight).reversed())
                                .findFirst().orElse(new WeightedEdge(inactive[0],0))
                );



                // if it finishes a box
                // set score to finished game state + 1
                // find the largest of all those finished states and set the score at this state to that.
            }
        }
    }

    private static int boxesFilled(@NotNull Edge[] edges, int index, int h, int w) {
        int result = 0;
        Edge edge = edges[index];
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

    private static Edge[] edges(@NotNull Node[][] board){
        Edge[] result = new Edge[board.length * (board[0].length - 1) + board[0].length * (board.length - 1)];
        int index = 0;
        // get horizontals
        for(int y = 0;y < board.length;y++)
            for(int x = 0;x < board[y].length - 1;x++)
                result[index++] = board[y][x].right;
        // get verticals
        for(int y = 0;y < board.length - 1;y++)
            for(int x = 0;x < board[y].length;x++)
                result[index++] = board[y][x].down;
        return result;
    }

    private static int hash(@NotNull Node[][] board){
        Edge[] edges = edges(board);
        return IntStream
                .range(0,edges.length)
                .reduce(0,(acc,x) -> edges[x].active ? acc | (1 << x) : acc);
    }

    @Override public @NotNull Edge choose(@NotNull Node[][] board){
        WeightedEdge e = cache.get(hash(board));
        System.out.println("Weight: " + e.weight);
        return e.edge;
    }

    private static class WeightedEdge{
        Edge edge;
        int weight;

        public WeightedEdge(@NotNull Edge edge, int weight){
            this.edge = edge;
            this.weight = weight;
        }
    }
}


