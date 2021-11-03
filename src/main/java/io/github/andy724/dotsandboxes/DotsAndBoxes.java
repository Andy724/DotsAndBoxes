package io.github.andy724.dotsandboxes;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import io.github.andy724.dotsandboxes.strategy.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

public class DotsAndBoxes{
    // game state
    private final Map<Player,GameStrategy> strategies = new HashMap<>();
    private final Player[][] points;
    private final Node[][] nodes;
    private Player next;

    // game analysis
    int[][] possibleScore;

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

        // set player strategies
        strategies.put(Player.ONE,new BruteForceStrategy(nodes));
        strategies.put(Player.TWO,strategies.get(Player.ONE));

        // set starting player
        next = Player.ONE;
    }

    public boolean isFinished(){
        return Arrays.stream(points).flatMap(Arrays::stream).allMatch(Objects::nonNull);
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

    public boolean playerMove(Player p) {  // draws a line for a player
        //System.out.println(max);
        Edge e = strategies.get(p).choose(nodes);
        e.activate();
        Set<Node> boxes = findFullBox(e);
        for(Node n : boxes) {
            points[n.y][n.x] = p;
        }
        return !boxes.isEmpty();
    }

    /**
     * 2 2: 2
     * 2 3: tie
     * 3 2: tie
     * 3 3: 1
     * 3 4: 2
     * 4 3: 2
     * 4 4: 2
     * -––––––––––––––––––– 2x2 vs 3x3 -–––––––––––––––––––
     * 20 20
     *      (3x3 view – 1, 2x2 view – 2): 2 (49 to 312) - 2x2 wins
     *      (2x2 view – 1, 3x3 view – 2): 1 (324 to 37) - 2x2 wins
     *
     * -––––––––––––––––––– 3x3 vs 4x4 -–––––––––––––––––––
     * 7 7
     *      (4x4 view - 1, 3x3 view - 2): 1 (19 to 17) - 4x4 wins
     *      (3x3 view - 1, 4x4 view - 2): 1 (21 to 15) - 3x3 wins
     *      (3x3 view - 1, 3x3 view - 2): tie (18 to 18)
     *      (4x4 view - 1, 4x4 view - 2): 2 (13 to 23)
     *
     * 8 8
     *      (4x4 view - 1, 3x3 view - 2): 2 (16 to 33) – 3x3 wins
     *      (3x3 view - 1, 4x4 view - 2): 1 (28 to 21) – 3x3 wins
     *      (3x3 view - 1, 3x3 view - 2): 2 (24 to 25)
     *      (4x4 view - 1, 4x4 view - 2): 1 (25 to 24)
     *
     * 9 9
     *      (4x4 view - 1, 3x3 view - 2): 1 (33 to 31) – 4x4 wins
     *      (3x3 view - 1, 4x4 view - 2): 1 (38 to 26) – 3x3 wins
     *      (3x3 view - 1, 3x3 view - 2): 1 (43 to 21)
     *      (4x4 view - 1, 4x4 view - 2): 1 (39 to 25)
     *
     * 10 10
     *      (4x4 view – 1, 3x3 view - 2): 1 (42 to 39) – 4x4 wins
     *      (3x3 view - 1, 4x4 view - 2): 1 (56 to 25) – 3x3 wins
     *      (3x3 view - 1, 3x3 view - 2): 2 (36 to 45)
     *      (4x4 view - 1, 4x4 view - 2): 2 (37 to 44)
     */
    public static void main(@NotNull String[] args){
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);

        long start = System.currentTimeMillis();
        DotsAndBoxes game = new DotsAndBoxes(width,height);
        long end = System.currentTimeMillis();
        System.out.println(Duration.ofMillis(end - start).toString().substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase());
        System.out.println(game);
        game.next = Player.ONE;

        for(int i = 0;!game.isFinished();i++) {
            boolean scored = game.playerMove(game.next);
            if(!scored) game.next = game.next.next();
            System.out.println(i + 1);
            System.out.println(game);
        }
        System.out.println(game);
    }

    @Override public @NotNull String toString(){
        StringBuilder builder = new StringBuilder("------------(start)------------\n");
        builder.append("Playing: %s%n".formatted(next.next()));
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
}
