package com.astra.melkovhw142;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LARGE_TEXT = "large_text";

    private ArrayList<Integer> deletedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey(LARGE_TEXT))  {
            deletedItems.addAll(Objects.requireNonNull(savedInstanceState.getIntegerArrayList(LARGE_TEXT)));
        }

        initViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putIntegerArrayList(LARGE_TEXT, deletedItems);
        super.onSaveInstanceState(bundle);
    }

    private void initViews() {
        /* ListView */
        final List<Map<String, String>> content = prepareContent();
        final BaseAdapter adapter = createAdapter(content);

        final ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // save deleted position for future restoring state
                deletedItems.add(position);

                content.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        /* SwipeRefreshLayout */
        final SwipeRefreshLayout swipe = findViewById(R.id.main_swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                content.clear();
                content.addAll(prepareContent());
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);
            }
        });
    }

    private BaseAdapter createAdapter(List<Map<String, String>> content) {
        String[] from = new String[]{"header","content"};
        int[] to = new int[]{R.id.header, R.id.content};
        return new SimpleAdapter(this, content, R.layout.list_item, from, to);
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> result = new ArrayList<>();

        String[] values = getString(R.string.large_text).split("\n\n");
        for(int i = 0; i < values.length; i++) {
            Map<String, String> item = new HashMap<>();

            /* header */
            String headerValue = values[i].trim();
            i++;

            /* paragraphs */
            StringBuilder contentValue = new StringBuilder();
            while(values[i].length() > 100) {
                /* add paragraph to header */
                contentValue.append(values[i].trim());

                /* check next paragraph - this header? */
                i++;

                /* check end data */
                if(i >= values.length - 1) {
                    break;
                }
            }
            i--;

            /* add header + paragraph */
            item.put("header", headerValue);
            item.put("content", contentValue.toString());
            result.add(item);
        }

        for(int i : deletedItems) {
           result.remove(i);
        }

        return result;
    }
}