package vn.elca.training.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.elca.training.entity.Project;
import vn.elca.training.repository.custom.ProjectRepositoryCustom;

import java.util.Optional;

/**
 * Repository interface for Project entity.
 *
 * @author vlp
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, QuerydslPredicateExecutor<Project>, ProjectRepositoryCustom {
    @Override
    @EntityGraph(attributePaths = {"group", "group.groupLeader", "members"})
    Optional<Project> findById(Long id);

    boolean existsByProjectNumber(Integer projectNumber);
}
