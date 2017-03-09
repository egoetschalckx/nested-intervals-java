package com.goetschalckx.nestedintervals.models.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Tree {

    private UUID treeId;
    private List<Node> nodes;
}
