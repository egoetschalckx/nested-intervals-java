package com.goetschalckx.nestedintervals.services;

import com.fasterxml.uuid.Generators;
import com.github.javafaker.Faker;
import com.goetschalckx.nestedintervals.models.web.Node;
import com.goetschalckx.nestedintervals.models.jpa.JpaNode;
import com.goetschalckx.nestedintervals.repositories.JpaNodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaNodeServiceIntegTest {

    @Autowired
    JpaNodeService jpaNodeService;

    @Autowired
    JpaNodeRepository jpaNodeRepository;

    private static final Faker FAKER = new Faker();

    /*@Before
    public void before() {
        jpaNodeService = new JpaNodeService(entityManager);
    }*/

    UUID treeId = Generators.timeBasedGenerator().generate();

    @Test
    public void test() {
        //UUID nodeId1 = initStaticNodes();
        randomTree();
        List<JpaNode> allNodes = jpaNodeRepository.findAll();

        TreeNode treeNode = jpaNodeService.findTree(treeId);

        assertEquals(8, allNodes.size());
    }

    @Test
    public void test2() {
        List<JpaNode> allNodes = jpaNodeRepository.findAll();
        int temp = 42;
    }

    private void randomTree() {
        Node node = Node.builder()
                .name(FAKER.company().profession())
                .treeId(treeId)
                .build();
        Node returnedNode = jpaNodeService.save(node);

        int depth = FAKER.number().numberBetween(2, 9);
        for (int i = 0; i < depth; i++) {
            Node node2 = Node.builder()
                    .name(FAKER.company().profession())
                    .treeId(treeId)
                    .build();
            Node returnedNode2 = jpaNodeService.save(node);

            int numChildren = FAKER.number().numberBetween(3, 5);
            for (int j = 0; j < numChildren; j++) {
                Node node3 = Node.builder()
                        .name(FAKER.company().profession())
                        .treeId(treeId)
                        .parentNodeId(returnedNode2.getNodeId())
                        .build();
                jpaNodeService.save(node);
            }
        }
    }

    private UUID initStaticNodes() {
        Node node1 = Node.builder()
                .name("1")
                .treeId(treeId)
                .build();
        Node returnedNode1 = jpaNodeService.save(node1);

        Node node2 = Node.builder()
                .name("2")
                .parentNodeId(returnedNode1.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode2 = jpaNodeService.save(node2);

        Node node3 = Node.builder()
                .name("3")
                .parentNodeId(returnedNode1.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode3 = jpaNodeService.save(node3);

        Node node4 = Node.builder()
                .name("4")
                .parentNodeId(returnedNode1.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode4 = jpaNodeService.save(node4);

        Node node5 = Node.builder()
                .name("5")
                .parentNodeId(returnedNode1.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode5 = jpaNodeService.save(node5);

        Node node6 = Node.builder()
                .name("6")
                .parentNodeId(returnedNode5.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode6 = jpaNodeService.save(node6);

        Node node7 = Node.builder()
                .name("7")
                .parentNodeId(returnedNode5.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode7 = jpaNodeService.save(node7);

        Node node8 = Node.builder()
                .name("8")
                .parentNodeId(returnedNode5.getNodeId())
                .treeId(treeId)
                .build();
        Node returnedNode8 = jpaNodeService.save(node8);

        return returnedNode1.getNodeId();
    }
}
