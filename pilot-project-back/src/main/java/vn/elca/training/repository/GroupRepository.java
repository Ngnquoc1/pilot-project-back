package vn.elca.training.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.elca.training.entity.Group;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, QuerydslPredicateExecutor<Group> {
    boolean existsGroupById(Long id);

    @Override
    @EntityGraph(attributePaths = {"groupLeader"})
    List<Group> findAll();
}
