package io.github.andy724.dotsandboxes.board;

public class Node{
    public Edge up,right,down,left;
    public int x,y;    // top left- 0,0

    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }
}