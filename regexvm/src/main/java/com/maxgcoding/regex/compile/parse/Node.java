package com.maxgcoding.regex.compile.parse;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Node {
    private NodeType type;
    private Character data;
    private String ccl;
    private Node left;
    private Node right;
}
