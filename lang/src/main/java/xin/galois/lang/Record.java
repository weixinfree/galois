package xin.galois.lang;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (record User (age name sex)) => Record
 * (def xm (User (10 'xm' 'male')) =>
 * (@age xm)
 * (@sex xm)
 * (@name xm)
 * (@age= xm 18)
 * <p>
 * (xm :age)
 * (xm :sex)
 * (xm :name)
 * (xm :age 18)
 * (xm :sex 'female')
 * (xm :name 'xh')
 * (iter xm)
 * (iter (keys xm))
 * (iter (values xm))
 * <p>
 * Created by wangwei on 2018/5/5.
 */

public class Record implements Functor {

    private static final Pattern FIELD = Pattern.compile("(\\w+)");

    private final String name;
    private final String _fields;
    private final List<String> fields;

    public Record(String name, String fields) {
        this.name = name;
        this._fields = fields;
        this.fields = parseFields(fields);
    }

    private static List<String> parseFields(String raw) {
        final ArrayList<String> fieldList = new ArrayList<>();

        final Matcher matcher = FIELD.matcher(raw);
        int start = 0;
        while (matcher.find(start)) {
            fieldList.add(matcher.group(1).trim());
            start = matcher.end();
        }

        final LinkedHashSet<String> fields = new LinkedHashSet<>(fieldList);

        if (fields.size() != fieldList.size()) {
            throw new GaloisException("record fields repeated! fields: " + raw);
        }

        return fieldList;
    }

    @Override
    public Object call(String operator, List<Object> params, Env env, Galois galois) {

        galois.assertTrue(params.size() == fields.size(),
                "record fields count mismatch!, except: " + fields + ", given: " + params);

        final RecordInstance record = new RecordInstance();
        for (int i = 0; i < fields.size(); i++) {
            final String field = fields.get(i);
            final Object value = params.get(i);
            record.__dict__.put(field, value);
        }

        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (name != null ? !name.equals(record.name) : record.name != null) return false;
        if (_fields != null ? !_fields.equals(record._fields) : record._fields != null)
            return false;
        return fields != null ? fields.equals(record.fields) : record.fields == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (_fields != null ? _fields.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(record " + name + " " + _fields + ")";
    }

    public class RecordInstance implements Functor, Iterable<Map.Entry<String, Object>> {

        public final Map<String, Object> __dict__ = new LinkedHashMap<>();

        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            if (params.size() == 1) {
                final String field = (String) params.get(0);
                galois.assertTrue(fields.contains(field),
                        "field: " + field + " not in record: " + name + " fields: " + fields);
                return __dict__.get(field);
            }

            if (params.size() == 2) {
                final String field = (String) params.get(0);
                galois.assertTrue(fields.contains(field),
                        "field: " + field + " not in record: " + name + " fields: " + fields);

                final Object value = params.get(1);
                __dict__.put(field, value);
                return value;
            }

            return galois.fatal("wrong record access way!");
        }

        @NonNull
        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            return __dict__.entrySet().iterator();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RecordInstance entries = (RecordInstance) o;

            return __dict__.equals(entries.__dict__);
        }

        @Override
        public int hashCode() {
            return __dict__.hashCode();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("(")
                    .append(name)
                    .append(" (");

            for (Map.Entry<String, Object> entry : __dict__.entrySet()) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append(" ");
            }
            sb.deleteCharAt(sb.lastIndexOf(" "));
            sb.append("))");
            return sb.toString();
        }
    }
}
