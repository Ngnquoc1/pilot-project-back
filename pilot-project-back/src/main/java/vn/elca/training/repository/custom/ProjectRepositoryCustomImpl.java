package vn.elca.training.repository.custom;

import org.apache.commons.lang3.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.entity.QProject;


import java.util.List;

@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public List<Project> searchProjects(String keyword, ProjectStatus status){
        QProject qProject=QProject.project;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.isNotBlank(keyword)) {
            String cleanKeyword=keyword.trim();
            BooleanBuilder keywordBuilder=new BooleanBuilder();
            keywordBuilder.or(qProject.name.containsIgnoreCase(cleanKeyword));
            keywordBuilder.or(qProject.customer.containsIgnoreCase(cleanKeyword));

            try{
                int number = Integer.parseInt(cleanKeyword);
                keywordBuilder.or(qProject.projectNumber.eq(number));
            } catch (NumberFormatException ignored) {

            }
            builder.and(keywordBuilder);
        }

        if(status!=null) {
            builder.and(qProject.status.eq(status));
        }
        return queryFactory.selectFrom(qProject)
                .leftJoin(qProject.group).fetchJoin()
                .leftJoin(qProject.members).fetchJoin()
                .where(builder)
                .orderBy(qProject.projectNumber.asc())
                .distinct()
                .fetch();
    }
}
