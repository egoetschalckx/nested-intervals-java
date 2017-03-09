package com.goetschalckx.nestedintervals.services;

import com.fasterxml.uuid.Generators;
import com.goetschalckx.nestedintervals.models.web.CreateNodeRequest;
import com.goetschalckx.nestedintervals.models.web.Node;
import com.goetschalckx.nestedintervals.models.jpa.JpaNode;
import com.goetschalckx.nestedintervals.models.jpa.JpaNodeKey;
import com.goetschalckx.nestedintervals.models.web.Tree;
import com.goetschalckx.nestedintervals.repositories.JpaNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * Hazel (2008)
 * Adding nv to snv gives the numerator of the first child
 * Adding dv to sdv gives the denominator of the first child
 * Given the nv, dv, snv and sdv of a parent node p, we can determine the nv and dv of its cth child as follows:
 *      nvc = nvp + c × snvp (2.1)
 *      dvc = dvp + c × sdvp (2.2)
 * Since the next sibling of the cth child of node p, is the (c + 1)th child of node p, it follows that
 *      snvc = nvp + (c + 1) × snvp (2.3)
 *      sdvc = dvp + (c + 1) × sdvp (2.4)
 */
@Service
public class JpaNodeService implements NodeService {

    private static final long ROOT_NV = 2;
    private static final long ROOT_DV = 1;
    private static final long ROOT_SNV = 3;
    private static final long ROOT_SDV = 1;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JpaNodeRepository jpaNodeRepository;

    @Override
    public Node save(CreateNodeRequest node) {
        // get parent
        JpaNodeKey parentKey = JpaNodeKey.builder()
                .nodeId(node.getParentNodeId())
                .treeId(node.getTreeId())
                .build();
        JpaNode parent = jpaNodeRepository.findOne(parentKey);

        final long nv;
        final long dv;
        final long snv;
        final long sdv;
        final long depth;
        if (parent == null) {
            // first node in this tree
            nv = ROOT_NV;
            dv = ROOT_DV;
            snv = ROOT_SNV;
            sdv = ROOT_SDV;
            depth = 0;
        } else {
            // find the next child (c) to insert under parent
            String jqlQuery =
                    "SELECT COUNT(n.nodeKey.nodeId) + 1 "
                            + "FROM JpaNode n "
                            + "WHERE "
                            + "CAST(n.nv as double) / n.dv > :nvp / :dvp "
                            + "AND "
                            + "CAST(n.nv as double) / n.dv < :snvp / :sdvp";
            TypedQuery<Long> countQuery = entityManager.createQuery(jqlQuery, Long.class);
            countQuery.setParameter("nvp", parent.getNv().doubleValue());
            countQuery.setParameter("dvp", parent.getDv().doubleValue());
            countQuery.setParameter("snvp", parent.getSnv().doubleValue());
            countQuery.setParameter("sdvp", parent.getSdv().doubleValue());
            Long count = countQuery.getSingleResult();

            // NOTE: double + 1? probably not
            Long countPlusOne = count + 1;

            nv = parent.getNv() + count * parent.getSnv();
            dv = parent.getDv() + count * parent.getSdv();
            snv = parent.getNv() + countPlusOne * parent.getSnv();
            sdv = parent.getDv() + countPlusOne * parent.getSdv();
            depth = parent.getDepth() + 1;
        }

        // make the new node
        UUID nodeId = Generators.timeBasedGenerator().generate();
        JpaNodeKey jpaNodeKey = JpaNodeKey.builder()
                .nodeId(nodeId)
                .treeId(node.getTreeId())
                .build();

        JpaNode jpaNode = JpaNode.builder()
                .nodeKey(jpaNodeKey)
                .name(node.getName())
                .parentNodeId(node.getParentNodeId())
                .depth(depth)
                .nv(nv)
                .dv(dv)
                .snv(snv)
                .sdv(sdv)
                .build();

        JpaNode returnedJpaNode = jpaNodeRepository.save(jpaNode);

        return convertToNode(returnedJpaNode);
    }

    @Override
    public Tree findTree(UUID treeId) {
        String jpqlQuery =
                "SELECT n FROM JpaNode n "
                        + "WHERE "
                        + "n.nodeKey.treeId = :treeId "
                        + "ORDER BY n.depth ASC, n.parentNodeId ASC, (CAST(n.nv as double) / n.dv) ASC";
        // NOTE: could add depth support here
        // "and n.depth <= :maxDepth"

        TypedQuery<JpaNode> query = entityManager.createQuery(jpqlQuery, JpaNode.class);
        query.setParameter("treeId", treeId);

        List<JpaNode> childrenJpaNodes = query.getResultList();
        List<Node> children = childrenJpaNodes.stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        return Tree.builder()
                .treeId(treeId)
                .nodes(children)
                .build();
    }

    /*@Override
    public Node findTreeNode(UUID treeId, UUID nodeId) {
        JpaNodeKey jpaNodeKey = JpaNodeKey.builder()
                .nodeId(nodeId)
                .treeId(treeId)
                .build();

        JpaNode jpaNode = jpaNodeRepository.findOne(jpaNodeKey);
        List<JpaNode> childrenJpaNodes = findDescendants(jpaNode);
        List<Node> children = childrenJpaNodes.stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        return Tree.builder()
                .treeId(treeId)
                .nodes(children)
                .build();
    }*/

    /*private List<JpaNode> findDescendants(JpaNode parent) {
        return findDescendants(
                parent.getNodeKey().getTreeId(),
                parent.getNv(),
                parent.getDv(),
                parent.getSnv(),
                parent.getSdv());
    }

    private List<JpaNode> findDescendants(UUID treeId, Long nvp, Long dvp, Long snvp, Long sdvp) {
        String jpqlQuery =
                "SELECT n FROM JpaNode n "
                        + "WHERE "
                        + "n.nodeKey.treeId = :treeId "
                        + "AND "
                        + "CAST(n.nv as double) / n.dv > :nvp / :dvp "
                        + "AND "
                        + "CAST(n.nv as double) / n.dv < :snvp / :sdvp "
                        + "ORDER BY n.depth ASC, n.parentNodeId ASC, (CAST(n.nv as double) / n.dv) ASC";
        // NOTE: could add depth support here
        // "and n.depth <= :maxDepth"

        TypedQuery<JpaNode> query = entityManager.createQuery(jpqlQuery, JpaNode.class);
        query.setParameter("nvp", nvp.doubleValue());
        query.setParameter("dvp", dvp.doubleValue());
        query.setParameter("snvp", snvp.doubleValue());
        query.setParameter("sdvp", sdvp.doubleValue());
        query.setParameter("treeId", treeId);

        return query.getResultList();
    }*/

    private Node convertToNode(JpaNode jpaNode) {
        return Node.builder()
                .treeId(jpaNode.getNodeKey().getTreeId())
                .nodeId(jpaNode.getNodeKey().getNodeId())
                .name(jpaNode.getName())
                .depth(jpaNode.getDepth())
                .build();
    }
}
