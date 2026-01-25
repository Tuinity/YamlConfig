package ca.spottedleaf.yamlconfig.adapter.collection;

import ca.spottedleaf.yamlconfig.adapter.TypeAdapter;
import ca.spottedleaf.yamlconfig.adapter.TypeAdapterRegistry;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SortedMapTypeAdapter extends TypeAdapter<Map<String, Object>, Map<String, Object>> {

    public static final SortedMapTypeAdapter SORTED_CASE_INSENSITIVE = new SortedMapTypeAdapter(String.CASE_INSENSITIVE_ORDER, UnsortedMapTypeAdapter.INSTANCE);
    public static final SortedMapTypeAdapter SORTED_CASE_SENSITIVE = new SortedMapTypeAdapter(null, UnsortedMapTypeAdapter.INSTANCE);

    private final Comparator<String> keyComparator;
    private final TypeAdapter<Map<String, Object>, Map<String, Object>> mapAdapter;

    public SortedMapTypeAdapter(final Comparator<String> keyComparator, final TypeAdapter<Map<String, Object>, Map<String, Object>> mapAdapter) {
        this.keyComparator = keyComparator;
        this.mapAdapter = mapAdapter;
    }

    private LinkedHashMap<String, Object> sortMap(final Map<String, Object> map) {
        final List<Map.Entry<String, Object>> sorted = new ArrayList<>(map.size());

        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            sorted.add(entry);
        }

        sorted.sort((final Map.Entry<String, Object> e1, final Map.Entry<String, Object> e2) -> {
            final String k1 = e1.getKey();
            final String k2 = e2.getKey();

            if (SortedMapTypeAdapter.this.keyComparator == null) {
                return k1.compareTo(k2);
            } else {
                return SortedMapTypeAdapter.this.keyComparator.compare(k1, k2);
            }
        });

        final LinkedHashMap<String, Object> ret = new LinkedHashMap<>(map.size());

        for (final Map.Entry<String, Object> entry : sorted) {
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    @Override
    public Map<String, Object> deserialize(final TypeAdapterRegistry registry, final Object input, final Type type) {
        return this.sortMap(this.mapAdapter.deserialize(registry, input, type));
    }

    @Override
    public Map<String, Object> serialize(final TypeAdapterRegistry registry, final Map<String, Object> value, final Type type) {
        return this.mapAdapter.serialize(registry, this.sortMap(value), type);
    }
}
