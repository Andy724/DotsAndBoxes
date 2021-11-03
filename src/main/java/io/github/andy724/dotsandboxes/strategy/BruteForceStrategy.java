package io.github.andy724.dotsandboxes.strategy;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.board.WeightedEdge;
import io.github.andy724.dotsandboxes.board.view.BoardView;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;

public class BruteForceStrategy implements GameStrategy{
    Map<Integer,IndexedWeightedEdge> cache;

    private static class IndexedWeightedEdge{
        final int index,weight;

        public IndexedWeightedEdge(int index, int weight){
            this.index = index;
            this.weight = weight;
        }

        public @NotNull Edge edge(@NotNull BoardView board){
            return edges(board)[index];
        }
    }

    public BruteForceStrategy(@NotNull Node[][] nodes){
        cache = new HashMap<>();
        // actually loop through all possible boards
        // mapping game state to edges to draw
        Edge[] edges = edges(nodes);
        for(int state = 0;state < Math.pow(2,edges.length);state++){
            for(int i = 0;i < edges.length;i++)
                edges[i].active = (state & (1 << i)) == 0;
            // if there is only one deactivated state, set weighted edge at that point to the edge with a weight of 1
            int[] inactive = IntStream.range(0,edges.length).filter(x -> !edges[x].active).toArray();
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
                                        return new IndexedWeightedEdge(x, wonBoxes + nextScore);
                                    else
                                        return new WeightedEdge(edges[x], -nextScore);

                                })
                                .sorted(comparingInt((WeightedEdge weighted) -> weighted.weight).reversed())
                                .findFirst().orElse(new WeightedEdge(inactive[0],0))
                );
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

    private static Edge[] edges(@NotNull BoardView board){
        Edge[] result = new Edge[board.height() * (board.width() - 1) + board.width() * (board.height() - 1)];
        int index = 0;
        // get horizontals
        for(int y = 0;y < board.height();y++)
            for(int x = 0;x < board.width() - 1;x++)
                result[index++] = board.at(x,y).right().base();
        // get verticals
        for(int y = 0;y < board.height() - 1;y++)
            for(int x = 0;x < board.width();x++)
                result[index++] = board.at(x,y).down().base();
        return result;
    }

    private static int hash(@NotNull BoardView board){
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

    @Override public @NotNull BoardView view(@NotNull Node[][] board){
        return new BoardView(board,board[0].length,board.length);
    }
}


