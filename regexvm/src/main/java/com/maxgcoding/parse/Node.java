package com.maxgcoding.parse;

import lombok.Data;

@Data
public class Node {
    private NodeType type;
    private String data;
    private Node left;
    private Node right;
}
