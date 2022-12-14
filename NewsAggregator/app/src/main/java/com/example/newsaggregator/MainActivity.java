package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArticleAdapter articleAdapter;
    private ArrayList<NewsSource> fullSources = new ArrayList<>();
    private ArrayList<Article> articleList = new ArrayList<>();
    private ArrayList<NewsSource> sourcesList = new ArrayList<>();
    private final ArrayList<String> topicsList = new ArrayList<>();
    private FrameLayout frame;
    private String topicFilter = "All";
    private Menu menu;
    private String[] Names;
    private String source = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager2 feedPager = findViewById(R.id.feedPager);
        articleAdapter = new ArticleAdapter(this, articleList);
        feedPager.setAdapter(articleAdapter);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener((parent, view, position, id) -> selectItem(position));
        frame = findViewById(R.id.frameLayout);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        doArticleDownload();
    }

    private void doArticleDownload() {
        ArticleDownloadRunnable loaderTaskRunnable = new ArticleDownloadRunnable(this, source);
        new Thread(loaderTaskRunnable).start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        if (item.hasSubMenu()) {
            return true;
        }

        topicFilter = item.getTitle().toString();
        filterSources();

        return super.onOptionsItemSelected(item);
    }

    public void filterSources() {
        sourcesList.clear();
        setTitle("News Aggregator (" + sourcesList.size() + ")");
        for (int i = 0; i < fullSources.size(); i++) {
            NewsSource s = fullSources.get(i);
            if (!s.getCategory().equals(topicFilter) && !topicFilter.equals("All")) {
                continue;
            }
            sourcesList.add(s);
        }
        Names = new String[sourcesList.size()];
        for (int i = 0; i < sourcesList.size(); i++) {

            Names[i] = sourcesList.get(i).getName();
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, Names));
        setTitle("News Aggregator (" + sourcesList.size() + ")");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void makeMenu() {
        topicsList.add("All");

        for (int i = 0; i < fullSources.size(); i++) {
            String tempCategory = fullSources.get(i).getCategory();

            if (!topicsList.contains(tempCategory)) {
                topicsList.add(tempCategory);
            }
        }

        for (int i = 0; i < topicsList.size(); i++) {
            menu.add(0, i, i, topicsList.get(i));
        }
    }

    private void selectItem(int position) {
        source = sourcesList.get(position).getId();
        doArticleDownload();
        mDrawerLayout.closeDrawer(mDrawerList);
        setTitle(sourcesList.get(position).getName());
    }

    public void updateSources(ArrayList<NewsSource> s) {
        fullSources = new ArrayList<>(s);
        sourcesList = new ArrayList<>(fullSources);

        Names = new String[fullSources.size()];
        for (int i = 0; i < fullSources.size(); i++) {

            Names[i] = fullSources.get(i).getName();
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, Names));
        makeMenu();
        setTitle("News Aggregator (" + sourcesList.size() + ")");
    }

    public void updateArticle(ArrayList<Article> a) {
        articleList = new ArrayList<>(a);
        articleAdapter = new ArticleAdapter(this, articleList);
        ViewPager2 feedPager = findViewById(R.id.feedPager);
        feedPager.setAdapter(articleAdapter);
        frame.setVisibility(View.INVISIBLE);

    }
}