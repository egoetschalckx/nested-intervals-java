package com.goetschalckx.nestedintervals.services;

import com.goetschalckx.nestedintervals.models.web.CreateNodeRequest;
import com.goetschalckx.nestedintervals.models.web.Node;
import com.goetschalckx.nestedintervals.models.web.Tree;

import java.util.UUID;

public interface NodeService {

    Node save(CreateNodeRequest node);
    Tree findTree(UUID treeId);
}
