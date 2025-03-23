package xyz.thomas.utils;

import java.util.List;

public interface WordAnalyzer<T> {
    List<T> split(String text);
}
