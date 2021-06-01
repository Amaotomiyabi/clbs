package com.miyabi.clbs.util;

import org.springframework.data.domain.Sort;

/**
 * com.miyabi.clbs.util
 *
 * @Author amotomiyabi
 * @Date 2020/05/07/
 * @Description
 */
public interface Sorter {
    static Sort getSort(int sort, String property) {
        return switch (sort) {
            case Constant.NEW_SORT -> Sort.by(Sort.Direction.DESC, property);
            case Constant.OLD_SORT -> Sort.by(Sort.Direction.ASC, property);
            default -> Sort.by(Sort.Direction.DESC, "clicks");
        };
    }
}
