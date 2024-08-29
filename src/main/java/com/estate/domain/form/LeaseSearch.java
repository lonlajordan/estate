package com.estate.domain.form;

import com.estate.domain.entity.Lease;
import com.estate.domain.enumaration.State;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class LeaseSearch {
    private Integer page = 1;
    private Long housingId;
    private State state;

    public Specification<Lease> toSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (housingId != null) {
                predicates.add(cb.equal(root.get("housing").get("id"), housingId));
            }
            if (state != null) {
                switch (state){
                    case INITIATED:
                        predicates.add(cb.isNull(root.get("startDate")));
                        break;
                    case ACTIVATED:
                        predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), LocalDate.now()));
                        predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), LocalDate.now()));
                        break;
                    case EXPIRED:
                        predicates.add(cb.greaterThan(root.get("endDate"), LocalDate.now()));
                        break;
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
