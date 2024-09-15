package com.estate.domain.form;


import com.estate.domain.entity.Visitor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Data
public class VisitorSearchForm {
    private String email;
    private String name;
    private String phone;
    private Integer page = 1;

    public Specification<Visitor> toSpecification(){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), cb.lower(cb.literal("%" + email + "%"))));
            }
            if (StringUtils.isNotBlank(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), cb.lower(cb.literal("%" + name + "%"))));
            }
            if (StringUtils.isNotBlank(phone)) {
                predicates.add(cb.like(cb.lower(root.get("phone")), cb.lower(cb.literal("%" + phone + "%"))));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
