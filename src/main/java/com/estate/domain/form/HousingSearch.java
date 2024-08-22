package com.estate.domain.form;

import com.estate.domain.entity.Housing;
import com.estate.domain.enumaration.Availability;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Data
public class HousingSearch {
    private Long standingId;
    private Availability status;

    public Specification<Housing> toSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (standingId != null) {
                predicates.add(cb.equal(root.get("standing").get("id"), standingId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
