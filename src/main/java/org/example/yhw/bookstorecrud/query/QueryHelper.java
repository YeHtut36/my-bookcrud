package org.example.yhw.bookstorecrud.query;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.example.yhw.bookstorecrud.query.enums.Fetch;

import org.example.yhw.bookstorecrud.query.annotations.Query;
import org.example.yhw.bookstorecrud.utils.ObjectUtils;
import org.example.yhw.bookstorecrud.utils.ReflectionUtils;
import org.example.yhw.bookstorecrud.utils.StringUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for constructing JPA {@link Predicate} objects based on query annotations.
 * This class facilitates the dynamic creation of JPA queries using the Criteria API.`
 *
 * @author Zin Ko Win
 */

@Slf4j
@SuppressWarnings({"unchecked", "all"})
public class QueryHelper {

    private QueryHelper() {
    }

    /**
     * Generates a {@link Predicate} for the given query object based on the specified root,
     * criteria query, and criteria builder.
     *
     * @param <R>   The type of the root entity.
     * @param <Q>   The type of the query object.
     * @param root  The root of the query, representing the entity.
     * @param query The query object containing filter criteria.
     * @param cq    The {@link CriteriaQuery} object for the query.
     * @param cb    The {@link CriteriaBuilder} used to construct the criteria.
     * @return A {@link Predicate} representing the filter criteria.
     */
    public static <R, Q> Predicate getPredicate(Root<R> root, Q query, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (query == null) {
            return cb.and();
        }

        List<Predicate> predicates = buildPredicates(root, query, cb, cq);

        return predicates.stream().reduce(cb::and).orElse(cb.and());
    }

    private static <R, Q> List<Predicate> buildPredicates(Root<R> root, Q query, CriteriaBuilder cb, CriteriaQuery<?> cq) {
        List<Predicate> predicates = new ArrayList<>();

        List<Field> fields = getAllFields(query.getClass());
        for (Field field : fields) {
            Predicate predicate = buildPredicateForField(root, field, query, cb, cq);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        return predicates;
    }

    private static <R, Q> Predicate buildPredicateForField(Root<R> root, Field field, Q query, CriteriaBuilder cb, CriteriaQuery<?> cq) {
        Query q = field.getAnnotation(Query.class);
        if (ObjectUtils.isEmpty(q)) {
            return null;
        }
        String attributeName = getAttributeName(q, field);
        Object val = ReflectionUtils.getValueByFieldName(query, field.getName());

        if (shouldSkipPredicate(val)) {
            return null;
        }

        Join<R, ?> join = createJoin(root, q, val);

        PredicateBuilder<R> predicateBuilder = new PredicateBuilder<>(val, field, attributeName, join, root, cb, cq);

        if ((q.fetchType() == Fetch.LEFT || q.fetchType() == Fetch.RIGHT) && ((cq.getResultType().equals(Long.class) || cq.getResultType().equals(long.class)))) {
            log.warn("Invalid fetch type for result type Long/long");
            return null;
        }

        if (ObjectUtils.isNotEmpty(q.blurry())) {
            return predicateBuilder.createBlurryPredicate(q.blurry());
        }

        predicateBuilder.applyFetchType(q);

        if (BooleanUtils.isTrue(isDistinct(q))) {
            cq.distinct(true);
        }
        return createPredicateForQueryType(predicateBuilder, q);
    }

    private static Boolean isDistinct(Query q) {
        return BooleanUtils.isTrue(q.distinct());
    }

    private static <R> Predicate createPredicateForQueryType(PredicateBuilder<R> predicateBuilder, Query q) {
        switch (q.type()) {
            case EQUAL:
                return predicateBuilder.createEqualPredicate();
            case NOT_EQUAL:
                return predicateBuilder.createNotEqualPredicate();
            case IS_NULL:
                return predicateBuilder.createIsNullPredicate();
            case NOT_NULL:
                return predicateBuilder.createNotNullPredicate();
            case GREATER_THAN:
                return predicateBuilder.createGreaterThanPredicate();
            case GREATER_THAN_OR_EQUAL:
                return predicateBuilder.createGreaterThanOrEqualPredicate();
            case LESS_THAN:
                return predicateBuilder.createLessThanPredicate();
            case LESS_THAN_OR_EQUAL:
                return predicateBuilder.createLessThanOrEqualPredicate();
            case INNER_LIKE:
            case REG_EXP:
                return predicateBuilder.createInnerLikePredicate();
            case LEFT_LIKE:
                return predicateBuilder.createLeftLikePredicate();
            case RIGHT_LIKE:
                return predicateBuilder.createRightLikePredicate();
            case IN_STRING:
                return predicateBuilder.createInStringPredicate();
            case IN_LONG:
                return predicateBuilder.createInLongPredicate();
            case BETWEEN:
                return predicateBuilder.createBetweenPredicate();
            default:
                throw new IllegalArgumentException("Unsupported query type: " + q.type());
        }
    }

    private static <R> Join<R, ?> createJoin(Root<R> root, Query q, Object val) {
        if (ObjectUtils.isNotEmpty(q.joinName())) {
            String[] joinNames = q.joinName().split(">");
            Join<R, ?> join = null;
            for (String name : joinNames) {
                join = createJoin(join, root, name, q.join().getJoinType(), val);
            }
            return join;
        }
        return null;
    }

    private static <R> Join<R, ?> createJoin(Join<R, ?> currentJoin, Root<R> root, String name, JoinType joinType, Object val) {
        return ObjectUtils.isNotEmpty(currentJoin) && ObjectUtils.isNotEmpty(val) ? currentJoin.join(name, joinType) : root.join(name, joinType);
    }


    private static String getAttributeName(Query q, Field field) {
        return ObjectUtils.isNotEmpty(q) && StringUtils.isBlank(q.propName()) ? field.getName() : q.propName();
    }

    private static boolean shouldSkipPredicate(Object val) {
        return val == null || "".equals(val);
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static class PredicateBuilder<R> {
        private final Root<R> root;
        private final Field field;
        private final String attributeName;
        private String subAttribute = "";
        private final CriteriaBuilder cb;
        private final CriteriaQuery cq;
        private Join<R, ?> join;

        private Object val;

        public PredicateBuilder(Object value, Field field, String attributeName, Join<R, ?> join, Root<R> root, CriteriaBuilder cb, CriteriaQuery cq) {
            this.root = root;
            this.field = field;
            this.attributeName = attributeName;
            this.cb = cb;
            this.join = join;
            this.cq = cq;
            this.val = value;
        }

        public <R> void applyFetchType(Query q) {
            if (q.fetchType() == Fetch.NONE) {
                return;
            }
            root.fetch(q.propName(), q.fetchType() == Fetch.LEFT ? JoinType.LEFT : JoinType.RIGHT);
            cq.distinct(true);
        }

        public Predicate createBlurryPredicate(String blurry) {
            String[] blurrys = blurry.split(",");
            List<Predicate> orPredicate = Arrays.stream(blurrys)
                    .map(s -> cb.like(getExpression(s.trim()).as(String.class), "%" + val.toString() + "%"))
                    .collect(Collectors.toList());

            if (!orPredicate.isEmpty()) {
                Predicate[] p = new Predicate[orPredicate.size()];
                return cb.or(orPredicate.toArray(p));
            }
            return null;
        }

        public Predicate createEqualPredicate() {
            return cb.equal(getExpression(attributeName).as(field.getType()), val);
        }

        public Predicate createNotEqualPredicate() {
            return cb.notEqual(getExpression(attributeName), val);
        }

        public Predicate createIsNullPredicate() {
            return cb.isNull(getExpression(attributeName));
        }

        public Predicate createNotNullPredicate() {
            return cb.isNotNull(getExpression(attributeName));
        }

        public Predicate createGreaterThanPredicate() {
            return cb.greaterThan(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createGreaterThanOrEqualPredicate() {
            return cb.greaterThanOrEqualTo(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createLessThanPredicate() {
            return cb.lessThan(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createLessThanOrEqualPredicate() {
            return cb.lessThanOrEqualTo(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createInnerLikePredicate() {
            return cb.like(getExpression(attributeName).as(String.class), "%" + val + "%");
        }

        public Predicate createLeftLikePredicate() {
            return cb.like(getExpression(attributeName).as(String.class), "%" + val);
        }

        public Predicate createRightLikePredicate() {
            return cb.like(getExpression(attributeName).as(String.class), val + "%");
        }

        public Predicate createRegularExpressionPredicate() {
            return cb.like(getExpression(attributeName).as(String.class), "%" + val + "%");
        }

        public Predicate createInStringPredicate() {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getExpression(attributeName).in((List<String>) val);
        }

        public Predicate createInLongPredicate() {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getExpression(attributeName).in((Collection<String>) val);
        }

        public Predicate createBetweenPredicate() {
            List<Object> betweenObj = new ArrayList<>((List<Object>) val);
            List<String> between = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (betweenObj != null && betweenObj.size() == 2) {
                if (betweenObj.get(0) instanceof java.util.Date) {
                    between.add(dateFormat.format((java.util.Date) betweenObj.get(0)).concat(" 00:00"));
                }
                if (betweenObj.get(1) instanceof java.util.Date) {
                    between.add(dateFormat.format((java.util.Date) betweenObj.get(1)).concat(" 23:59"));
                }
            }

            if (between.size() == 2) {
                return createBetweenPredicate(between);
            }
            return null;
        }

        private Predicate createBetweenPredicate(List<String> between) {
            if (between.isEmpty()) {
                return null;
            }

            Expression<? extends Comparable> expression = getExpression(attributeName).as((Class<? extends Comparable>) between.get(0).getClass());

            return cb.between(expression, (Comparable) between.get(0), (Comparable) between.get(1));
        }

        private Expression<?> getExpression(String attributeName) {
            Path path = ObjectUtils.isNotEmpty(join) ? join : root;
            String[] attributeNames = attributeName.split("\\.");

            for (String attributeNamePart : attributeNames) {
                try {
                    path = path.get(attributeNamePart);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid attribute name or path: " + attributeName, e);
                }
            }
            return path;
        }
    }
}


