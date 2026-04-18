package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class CollectionUtils {

    public static <E> List<E> immutable(List<E> list) {
        return list == null ? List.of() : List.copyOf(list);
    }

    public static <E> Set<E> immutable(Set<E> set) {
        return set == null ? Set.of() : Set.copyOf(set);
    }

}
