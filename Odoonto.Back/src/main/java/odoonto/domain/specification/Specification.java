package odoonto.domain.specification;

import java.util.function.Predicate;

public interface Specification<T> extends Predicate<T> {
    
    default Specification<T> and(final Specification<T> other) {
        return t -> test(t) && other.test(t);
    }
    
    default Specification<T> or(final Specification<T> other) {
        return t -> test(t) || other.test(t);
    }
    
    default Specification<T> not() {
        return t -> !test(t);
    }
    
    static <T> Specification<T> of(final Predicate<T> predicate) {
        return predicate::test;
    }
} 