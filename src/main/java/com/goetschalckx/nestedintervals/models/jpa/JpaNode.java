package com.goetschalckx.nestedintervals.models.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "node")
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class JpaNode {

    @EmbeddedId
    private JpaNodeKey nodeKey;

    private String name;
    private Long nv;
    private Long dv;
    private Long snv;
    private Long sdv;
    private Long depth;
    private UUID parentNodeId;
}
