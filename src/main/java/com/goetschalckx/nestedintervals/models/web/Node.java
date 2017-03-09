package com.goetschalckx.nestedintervals.models.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Node {

    private UUID treeId;
    private UUID nodeId;
    private String name;
    private UUID parentNodeId;
    private Long depth;
}
