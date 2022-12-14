package com.example.newsaggregator;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private final MainActivity mainActivity;
    private final ArrayList<Article> articleList;
    private static final String TAG = "ArticleAdapter";

    public ArticleAdapter(MainActivity mainActivity, ArrayList<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
    }


    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ArticleViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_entry,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.articleTitleTV.setText(article.getHeadline());
        holder.articleDateTV.setText(article.getDate());
        holder.articleBodyTV.setText(article.getBody());
        if(article.getAuthor() != "null"){
            holder.articleAuthorTV.setText(article.getAuthor());
        } else{
            holder.articleAuthorTV.setText(" ");
        }
        holder.articleImage.setImageDrawable(article.getImage());
        holder.articleCountTV.setText(position+1 +" of " + articleList.size());
        View.OnClickListener l = view -> getUrl(article.getUrl());

        holder.articleTitleTV.setOnClickListener(v -> onClickURL(article.getUrl()));
        holder.articleImage.setOnClickListener(v -> onClickURL(article.getUrl()));
        holder.articleBodyTV.setOnClickListener(v -> onClickURL(article.getUrl()));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    protected void getUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Check if there is an app that can handle https intents
        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            mainActivity.startActivity(intent);
        }
        else {
            Log.d(TAG, "openArticleUrl: " + "No Application found that handles ACTION_VIEW (https) intents");
        }
    }

    public void onClickURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mainActivity.startActivity(intent);
    }
}

