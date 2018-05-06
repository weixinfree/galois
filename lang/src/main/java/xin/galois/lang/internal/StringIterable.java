package xin.galois.lang.internal;

import android.support.annotation.NonNull;

import java.util.Iterator;

/**
 * 字符串迭代器
 * Created by wangwei on 2018/5/4.
 */

public class StringIterable implements Iterable<Character> {

    private final String str;

    public StringIterable(String str) {
        this.str = str;
    }

    @NonNull
    @Override
    public Iterator<Character> iterator() {
        return new StringIterator();
    }

    class StringIterator implements Iterator<Character> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < str.length();
        }

        @Override
        public Character next() {
            final char c = str.charAt(index);
            index++;
            return c;
        }
    }
}
