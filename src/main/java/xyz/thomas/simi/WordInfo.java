package xyz.thomas.simi;

import java.util.Objects;

/**
 * 通过 text 判断equals
 */
public class WordInfo implements Comparable<WordInfo>{
    private String text;
    private String type;

    public String getText() {
        return text;
    }

    public WordInfo setText(String text) {
        this.text = text;
        return this;
    }

    public String getType() {
        return type;
    }

    public WordInfo setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        // return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return "wi("+text+","+type+")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        WordInfo owi = (WordInfo) other;
        return text.equals(owi.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public int compareTo(WordInfo other) {
        return text.compareTo(other.text);
    }
}
