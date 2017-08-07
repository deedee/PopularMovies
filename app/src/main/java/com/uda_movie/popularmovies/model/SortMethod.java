package com.uda_movie.popularmovies.model;


public enum SortMethod {
    POPULAR(0), TOP_RATE(1), FAVORITE(2);

    private final int value;

    SortMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SortMethod fromInt(int i) {
        for (SortMethod m : SortMethod.values()) {
            if (m.getValue() == i) {
                return m;
            }
        }
        return null;
    }
}