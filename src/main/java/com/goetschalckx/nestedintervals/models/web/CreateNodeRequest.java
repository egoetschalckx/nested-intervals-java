package com.goetschalckx.nestedintervals.models.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CreateNodeRequest {

    private UUID treeId;
    private String name;
    private UUID parentNodeId;
}
