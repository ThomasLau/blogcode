package xyz.thomas.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// from apache cosine， modified by tm
public class Cosine {
    public static <T, N extends Number> Double cosineSimilarity(final Map<T, N> leftVector, final Map<T, N> rightVector) {
        if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }
        final Set<T> intersection = getIntersection(leftVector, rightVector);
        final double dotProduct = dot(leftVector, rightVector, intersection);
        double d1 = squares(leftVector.values());
        double d2 = squares(rightVector.values());
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        }
        return cosineSimilarity;
    }

    private static <T, N extends Number> Set<T> getIntersection(final Map<T, N> leftVector, final Map<T, N> rightVector) {
        final Set<T> intersection = new HashSet<>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

    private static <N extends Number> Double squares(final Collection<N> vect) {
        N total = vect.iterator().next();
        double squv = 0;
        if (total instanceof Double) {
            for (final N iter : vect) {
                double itv = (double) iter;
                squv += (itv * itv);
            }
            return squv;
        } else if (total instanceof Integer) {
            for (final N iter : vect) {
                int itv = (Integer) iter;
                squv += (itv * itv);
            }
            return squv;
        }
        return 0d;
    }

    private static <T, N extends Number> Double dot(final Map<T, N> leftVector, final Map<T, N> rightVector, final Set<T> intersection) {
        N total = leftVector.values().iterator().next();
        if (total instanceof Double) {
            double dotProduct = 0;
            for (final T key : intersection) {
                Double left = (Double) leftVector.get(key);
                Double right = (Double) rightVector.get(key);
                dotProduct += (left * right);
            }
            return dotProduct;
        } else if (total instanceof Integer) {
            double dotProduct = 0;
            for (final T key : intersection) {
                Integer left = (Integer) leftVector.get(key);
                Integer right = (Integer) rightVector.get(key);
                dotProduct += (left * right);
            }
            return dotProduct;
        }
        return 0d;
    }
}
