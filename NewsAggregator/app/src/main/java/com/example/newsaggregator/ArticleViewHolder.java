package com.example.newsaggregator;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView articleTitleTV;
    TextView articleDateTV;
    TextView articleAuthorTV;
    ImageView articleImage;
    TextView articleBodyTV;
    TextView articleCountTV;
    public ArticleViewHolder(@NonNull View itemView){
        super(itemView);
        articleTitleTV = itemView.findViewById(R.id.articleTitle);
        articleDateTV= itemView.findViewById(R.id.articleDate);
        articleAuthorTV= itemView.findViewById(R.id.articleAuthor);
        articleImage= itemView.findViewById(R.id.articleImage);
        articleBodyTV= itemView.findViewById(R.id.articleDescription);
        articleBodyTV.setMovementMethod(new ScrollingMovementMethod());
        articleCountTV= itemView.findViewById(R.id.articleCount);



    }
}
