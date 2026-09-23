package vn.elca.training.repository.custom;

import com.querydsl.core.types.OrderSpecifier;
import org.apache.commons.lang3.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.entity.QProject;


import java.util.Collections;
import java.util.List;

@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public List<Project> searchProjects(String keyword, ProjectStatus status){
        QProject qProject=QProject.project;
        BooleanBuilder builder = buildSearchPredicate(qProject,keyword,status);
        return queryFactory.selectFrom(qProject)
                .leftJoin(qProject.group).fetchJoin()
                .leftJoin(qProject.members).fetchJoin()
                .where(builder)
                .orderBy(qProject.projectNumber.asc())
                .distinct()
                .fetch();
    }

    @Override
    public Page<Project> searchProjects(String keyword, ProjectStatus status, Pageable pageable) {
        QProject qProject=QProject.project;
        BooleanBuilder builder = buildSearchPredicate(qProject,keyword,status);

        Long total=queryFactory.select(qProject.id.countDistinct())
                .from(qProject)
                .where(builder)
                .fetchOne();
        long totalElements= total != null ? total : 0L;

        if (totalElements == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Project> content = queryFactory
                .selectFrom(qProject)
                .leftJoin(qProject.group).fetchJoin() // Safe To-One fetch join with limit/offset
                .where(builder)
                .orderBy(getOrderSpecifier(qProject, pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return new PageImpl<>(content, pageable, totalElements);
    }

    private BooleanBuilder buildSearchPredicate(QProject qProject, String keyword, ProjectStatus status) {
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
        return builder;
    }
    private OrderSpecifier<?> getOrderSpecifier(QProject qProject, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return qProject.projectNumber.asc();
        }
        for (Sort.Order order : sort) {
            boolean isAsc = order.isAscending();
            switch (order.getProperty()) {
                case "name":
                    return isAsc ? qProject.name.asc() : qProject.name.desc();
                case "customer":
                    return isAsc ? qProject.customer.asc() : qProject.customer.desc();
                case "status":
                    return isAsc ? qProject.status.asc() : qProject.status.desc();
                case "startDate":
                    return isAsc ? qProject.startDate.asc() : qProject.startDate.desc();
                case "projectNumber":
                default:
                    return isAsc ? qProject.projectNumber.asc() : qProject.projectNumber.desc();
            }
        }
        return qProject.projectNumber.asc();
    }

}
