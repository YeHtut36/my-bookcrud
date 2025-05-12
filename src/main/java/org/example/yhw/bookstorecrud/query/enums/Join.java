package org.example.yhw.bookstorecrud.query.enums;

import lombok.Getter;

import javax.persistence.criteria.JoinType;

/**
 * @author Zin Ko Win
 */

@Getter
public enum Join {
    LEFT(JoinType.LEFT),
    RIGHT(JoinType.RIGHT),
    INNER(JoinType.INNER);

    private final JoinType joinType;

    Join(JoinType joinType) {
        this.joinType = joinType;
    }
}
