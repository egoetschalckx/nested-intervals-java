package com.goetschalckx.nestedintervals.repositories;

import com.goetschalckx.nestedintervals.models.jpa.JpaNode;
import com.goetschalckx.nestedintervals.models.jpa.JpaNodeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNodeRepository extends JpaRepository<JpaNode, JpaNodeKey> {


}
