package com.tomakeitgo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {

        var dims = List.of(
//                IntStream.rangeClosed(-9, 9).filter(i -> i != 0).boxed().toList(),
                IntStream.rangeClosed(0, 9).boxed().toList()
        );
        System.out.println(cartesianProduct(dims));
    }


    public static List<List<Integer>> cartesianProduct(List<List<Integer>> sets) {
        if (sets.isEmpty()) return List.of();
        return cartesianProduct(0, sets);
    }

    private static List<List<Integer>> cartesianProduct(int index, List<List<Integer>> sets) {
        List<List<Integer>> ret = new ArrayList<>();
        if (index == sets.size()) {
            ret.add(new ArrayList<>());
        } else {
            for (Integer obj : sets.get(index)) {
                for (List<Integer> set : cartesianProduct(index + 1, sets)) {
                    set.add(obj);
                    ret.add(set);
                }
            }
        }
        return ret;
    }
}
