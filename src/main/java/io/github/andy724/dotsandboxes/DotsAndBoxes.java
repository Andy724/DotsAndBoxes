package io.github.andy724.dotsandboxes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;


public class DotsAndBoxes{
    // game state
    private final Player[][] points;
    private final Node[][] nodes;
    private Player next;

    // game analysis
    int possibleScore[][];

    public DotsAndBoxes(int width, int height){
        // initialize points matrix
        points = new Player[height - 1][];
        for(int i = 0;i < points.length;i++)
            points[i] = new Player[width - 1];

        // initialize game board
        nodes = new Node[height][];
        for(int i = 0;i < nodes.length;i++){
            int y = i;
            nodes[i] = IntStream.range(0,width).mapToObj(x -> new Node(x,y)).toArray(Node[]::new);
        }
        for(int y = 0;y < nodes.length;y++){
            for(int x = 0;x < width;x++){
                Node node = nodes[y][x];
                if(x - 1 >= 0) node.left = nodes[y][x - 1].right;
                if(x + 1 < width) node.right = new Edge(node,nodes[y][x + 1]);
                if(y - 1 >= 0) node.up = nodes[y - 1][x].down;
                if(y + 1 < height) node.down = new Edge(node,nodes[y + 1][x]);
            }
        }

        // initialize possible score matrix
        possibleScore = new int[height*2 -1][];
        for(int i = 0; i < width; i++) {
            possibleScore[i] = IntStream.range(0,i % 2 == 0 ? width - 1 : width).toArray();
        }

        // set starting player
        next = Player.ONE;
    }

    public @Nullable Player testBox(@NotNull Node node){    // node is top left node of box
        return points[node.y][node.x];
    }

    public boolean testFullBox(@NotNull Node node){
        int[] edgesActive = new int[4];
        edgesActive[0] = node.right.active ? 1 : 0;
        node = node.right.to;
        edgesActive[1] = node.down.active ? 1 : 0;
        node = node.down.to;
        edgesActive[2] = node.left.active ? 1 : 0;
        node = node.left.from;
        edgesActive[3] = node.up.active ? 1 : 0;
        return Arrays.stream(edgesActive).sum() == 4;
    }

    public Set<Node> findFullBox(Edge edge) { // return the node for a box that connects to an edge
        Set<Node> results = new HashSet<>();

        boolean horizontal = edge.from.y == edge.to.y;
        if(horizontal){
            // above
            if(edge.from.up != null)
                results.add(edge.from.up.from);

            // below
            if(edge.from.down != null)
                results.add(edge.from);
        } else{
            // right
            if(edge.from.right != null)
                results.add(edge.from);

            // left
            if(edge.from.left != null)
                results.add(edge.from.left.from);
        }
        return results.stream().filter(this::testFullBox).collect(Collectors.toSet());
    }

    public void playerMove(Player p) {  // draws a line for a player
        int max = 0;
        Edge e = null;
        for(Map.Entry<Edge,Integer> entry : analyzeMoves().entrySet()){
            Map<Edge, Integer> pm = playerMoveRecursive(entry.getKey());
            pm.put(entry.getKey(), entry.getValue());
            int sum = pm.values().stream().mapToInt(x -> x).sum();

            if(max < sum) {
                max = sum;
                e = entry.getKey();
            }
        }
        //System.out.println(max);
        if(e != null) {
            e.activate();
            Set<Node> boxes = findFullBox(e);
            for(Node n : boxes) {
                points[n.y][n.x] = p;
            }
        } else {
            boolean active;
            int y, x, edge;
            do{
                Random rn = new Random();
                y = rn.nextInt(nodes.length);
                x = rn.nextInt(nodes[0].length);
                edge = rn.nextInt(4);
                while((x == 0 && edge == 0) ||
                        (x == nodes[0].length - 1 && edge == 2) ||
                        (y == 0 && edge == 1) ||
                        (y == nodes.length - 1 && edge == 3)){
                    edge = rn.nextInt(4);
                }

                active = (switch(edge){
                    case 0 ->nodes[y][x].left;
                    case 1 -> nodes[y][x].up;
                    case 2 -> nodes[y][x].right;
                    case 3 -> nodes[y][x].down;
                    default -> null;
                }).active;
            } while (active);

            switch(edge){
                case 0 -> nodes[y][x].left.activate();
                case 1 -> nodes[y][x].up.activate();
                case 2 -> nodes[y][x].right.activate();
                case 3 -> nodes[y][x].down.activate();
            }
        }
        // analyzeMoves
        // for every one on the board
            // Map<Edge, int> pm = playerMoveRecursive
            // set edge score to sum of ints in pm

    }

    public Map<Edge,Integer> playerMoveRecursive(@NotNull Edge move){ // return the set of edges that pick the most points if edge is picked
        move.activate();

        Map<Edge,Integer> result = analyzeMoves().entrySet().stream()
                .map(entry -> {
                    Map<Edge,Integer> map = playerMoveRecursive(entry.getKey());
                    map.compute(entry.getKey(),(k,v) -> v == null ? entry.getValue() : v + entry.getValue());
                    return map;
                })
                .max(comparingInt(map -> map.values().stream().mapToInt(x -> x).sum()))
                .orElse(new HashMap<>());

        move.deactivate();
        return result;

            // recurse

        // set edge to active
        // analyzeMoves
        // int maxLength = 0
        // Map<Edge,Integer> return
        // for each one on the board
            // Map<Edge,int> pm = playerMoveRecursive(new edge)
            // if(maxLength < sum of ints in pm)
                // return = pm
                // maxLength = sum of ints in pm
        // set edge to inactive
        // return return
    }

    public Map<Edge,Integer> analyzeMoves(){
        // set the score in possible score to 1
        Map<Edge,Integer> weights = new HashMap<>();
        stream(nodes)
                .limit(nodes.length - 1)
                .forEach(row -> stream(row).limit(row.length - 1).forEach(node -> {
                    int[] edgesActive = new int[4];
                    Edge[] edges = new Edge[4];
                    Node current = node;
                    edgesActive[0] = current.right.active ? 1 : 0;
                    current = (edges[0] = current.right).to;
                    edgesActive[1] = current.down.active ? 1 : 0;
                    current = (edges[1] = current.down).to;
                    edgesActive[2] = current.left.active ? 1 : 0;
                    current = (edges[2] = current.left).from;
                    edgesActive[3] = current.up.active ? 1 : 0;
                    edges[3] = current.up;

                    Edge[] inactive = IntStream.range(0,4)
                            .filter(x -> edgesActive[x] == 0)
                            .mapToObj(x -> edges[x])
                            .toArray(Edge[]::new);
                    // add point if 1 inactive
                    if(inactive.length == 1)
                        weights.compute(inactive[0],(k,v) -> v == null ? 1 : v + 1);
                }));

        return weights;
    }

    public static void main(@NotNull String[] args){
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);

        DotsAndBoxes game = new DotsAndBoxes(width,height);
        System.out.println(game);
        game.next = Player.ONE;

        for(int i = 0; i < 12; i++) {
            game.playerMove(game.next);
            game.next = game.next.next();
            System.out.println(i + 1);
            System.out.println(game);
        }
    }

    @Override public @NotNull String toString(){
        StringBuilder builder = new StringBuilder("------------(start)------------\n");
        for(int y = 0;y < nodes.length;y++){
            boolean[] activeVertical = new boolean[nodes[y].length];

            // build horizontals
            StringBuilder horizontals = new StringBuilder("o");
            for(int x = 1;x < nodes[y].length;x++){
                Node node = nodes[y][x];
                if(node.up != null)
                    activeVertical[x] = node.up.active;
                if(node.left.active)
                    horizontals.append(" ----- ");
                else
                    horizontals.append("       ");
                horizontals.append("o");
            }
            horizontals.append("\n");

            // build verticals if not first row
            if(y > 0){
                StringBuilder verticals = new StringBuilder();
                activeVertical[0] = nodes[y][0].up.active;
                for(int row = 0;row < 3;row++){
                    for(int x = 0;x < activeVertical.length;x++){
                        Player owner = x < activeVertical.length - 1 ? testBox(nodes[y-1][x]) : null;
                        if(activeVertical[x])
                            verticals.append("|   ");
                        else
                            verticals.append("    ");
                        if(row == 1 && owner != null)
                            verticals.append("%s   ".formatted(owner));
                        else
                            verticals.append("    ");
                    }
                    verticals.append("\n");
                }
                builder.append(verticals);
            }
            builder.append(horizontals);
        }
        builder.append("Player 1: %d points\n".formatted(stream(points).flatMap(Arrays::stream).filter(player -> player == Player.ONE).count()));
        builder.append("Player 2: %d points\n".formatted(stream(points).flatMap(Arrays::stream).filter(player -> player == Player.TWO).count()));
        builder.append("-------------(end)-------------\n");
        return builder.toString();
    }

    private static class Node{
        Edge up,right,down,left;
        int x,y;    // top left- 0,0

        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private static class Edge{
        private boolean active;
        private Node from,to;

        public Edge(Node from, Node to){
            this.from = from;
            this.to = to;
        }

        public void activate(){
            active = true;
        }

        public void deactivate(){
            active = false;
        }
    }

    private enum Player{
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
}
