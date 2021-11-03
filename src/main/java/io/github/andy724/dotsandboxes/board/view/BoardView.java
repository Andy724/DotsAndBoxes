package io.github.andy724.dotsandboxes.board.view;

import io.github.andy724.dotsandboxes.board.Edge;
import io.github.andy724.dotsandboxes.board.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class BoardView{
    private final NodeView[][] view;
    private final Node[][] board;
    private int dx,dy;

    public BoardView(@NotNull Node[][] board, int width, int height){
        this.board = board;
        view = new NodeView[height][];
        for(int i = 0;i < view.length;i++){
            int y = i;
            view[i] = IntStream.range(0,width).mapToObj(x -> new NodeView(x,y)).toArray(NodeView[]::new);
        }
    }

    public @NotNull BoardView resize(int width, int height){
        return new BoardView(board,width,height);
    }

    public void shift(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

    public int width(){
        return view[0].length;
    }

    public int height(){
        return view.length;
    }

    public @NotNull NodeView at(int x, int y){
        return view[y][x];
    }

    public class NodeView{
        private final int x,y;

        private NodeView(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int x(){
            return x;
        }

        public int y(){
            return y;
        }

        private @NotNull Node base(){
            return board[y + dy][x + dx];
        }

        public @Nullable EdgeView up(){
            return y > 0 ? new EdgeView(base().up) : null;
        }

        public @Nullable EdgeView right(){
            return x < width() - 1 ? new EdgeView(base().right) : null;
        }

        public @Nullable EdgeView down(){
            return y < height() - 1 ? new EdgeView(base().down) : null;
        }

        public @Nullable EdgeView left(){
            return x > 0 ? new EdgeView(base().left) : null;
        }
    }

    public class EdgeView{
        private final Edge base;

        private EdgeView(@NotNull Edge base){
            this.base = base;
        }

        public @NotNull Edge base(){
            return base;
        }

        public @NotNull NodeView from(){
            return view[base.from.y - dy][base.from.x - dx];
        }

        public @NotNull NodeView to(){
            return view[base.to.y - dy][base.to.x - dx];
        }

        public boolean isActive(){
            return base.active;
        }

        public void setActive(boolean active){
            base.active = active;
        }
    }
}
