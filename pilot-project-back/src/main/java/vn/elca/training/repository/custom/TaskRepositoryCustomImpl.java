package vn.elca.training.repository.custom;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.QProject;
import vn.elca.training.model.entity.QTask;
import vn.elca.training.model.entity.Task;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author gtn
 *
 */
@Repository
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    @Autowired
    public TaskRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Project> findProjectsByTaskName(String taskName) {
        QProject qProject=QProject.project;
        QTask qTask=QTask.task;
        return queryFactory
                .selectFrom(qProject)
                .innerJoin(qProject.tasks, qTask)
                .where(qTask.name.eq(taskName))
                .fetch();
    }

    @Override
    public List<Task> listRecentTasks(int limit) {
        QTask qTask=QTask.task;
        return queryFactory
                .selectFrom(qTask)
                .orderBy(qTask.id.desc())
                .limit(limit)
                .fetch();
    }
}
