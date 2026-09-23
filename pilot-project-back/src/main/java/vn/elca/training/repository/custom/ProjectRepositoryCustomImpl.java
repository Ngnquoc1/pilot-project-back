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
import vn.elca.training.model.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.entity.QProject;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public List<Project> searchProjects(String keyword, ProjectStatus status){
        QProject qProject=QProject.project;
        ProjectSearchCriteriaDto criteria= new ProjectSearchCriteriaDto(keyword,status);
        BooleanBuilder builder = buildSearchPredicate(qProject,criteria);
        return queryFactory.selectFrom(qProject)
                .leftJoin(qProject.group).fetchJoin()
                .leftJoin(qProject.members).fetchJoin()
                .where(builder)
                .orderBy(qProject.projectNumber.asc())
                .distinct()
                .fetch();
    }

    @Override
    public Page<Project> searchProjects(ProjectSearchCriteriaDto criteria, Pageable pageable) {
        QProject qProject=QProject.project;
        BooleanBuilder builder = buildSearchPredicate(qProject,criteria);

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

    private BooleanBuilder buildSearchPredicate(QProject qProject, ProjectSearchCriteriaDto criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria == null) {
            return builder;
        }

        //Keyword
        if(StringUtils.isNotBlank(criteria.getKeyword())) {
            String cleanKeyword=criteria.getKeyword().trim();
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

        //Status
        if(criteria.getStatus()!=null) {
            builder.and(qProject.status.eq(criteria.getStatus()));
        }

        //Project Leader VISA
        if(StringUtils.isNotBlank(criteria.getGroupLeaderVisa())) {
            builder.and(qProject.group.groupLeader.visa.equalsIgnoreCase(criteria.getGroupLeaderVisa().trim()));
        }

        // Member Visa
        if (criteria.getMemberVisas() != null && !criteria.getMemberVisas().isEmpty()) {
            Set<String> visas = criteria.getMemberVisas();

            BooleanBuilder memberBuilder = new BooleanBuilder();
            for (String v : visas) {
                if (StringUtils.isNotBlank(v)) {
                    memberBuilder.or(qProject.members.any().visa.equalsIgnoreCase(v.trim()));
                }
            }
            if (memberBuilder.hasValue()) {
                builder.and(memberBuilder);
            }
        }

        //StartDate
        if (criteria.getStartDateFrom() != null) {
            builder.and(qProject.startDate.goe(criteria.getStartDateFrom())); // goe: Greater or Equal
        }
        if (criteria.getStartDateTo() != null) {
            builder.and(qProject.startDate.loe(criteria.getStartDateTo()));   // loe: Less or Equal
        }

        //EndDate
        if (criteria.getEndDateFrom() != null) {
            builder.and(qProject.endDate.isNotNull().and(qProject.endDate.goe(criteria.getEndDateFrom())));
        }
        if (criteria.getEndDateTo() != null) {
            builder.and(qProject.endDate.isNotNull().and(qProject.endDate.loe(criteria.getEndDateTo())));
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
