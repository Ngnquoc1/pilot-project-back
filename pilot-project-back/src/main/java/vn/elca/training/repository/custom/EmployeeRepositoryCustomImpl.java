package vn.elca.training.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.elca.training.entity.Employee;
import vn.elca.training.entity.QEmployee;

import java.util.Collections;
import java.util.List;

@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    private static final long MAX_RESULTS = 10;

    private final JPAQueryFactory queryFactory;

    @Autowired
    public EmployeeRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Employee> searchEmployee(String term) {
        if (StringUtils.isBlank(term)) {
            return Collections.emptyList();
        }

        String keyword = term.trim();
        QEmployee qEmployee = QEmployee.employee;
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(qEmployee.visa.containsIgnoreCase(keyword));
        builder.or(qEmployee.firstName.containsIgnoreCase(keyword));
        builder.or(qEmployee.lastName.containsIgnoreCase(keyword));

        return queryFactory.selectFrom(qEmployee)
                .where(builder)
                .limit(MAX_RESULTS)
                .fetch();
    }
}
