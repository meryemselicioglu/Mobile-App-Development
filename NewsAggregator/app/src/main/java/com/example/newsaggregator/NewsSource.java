package com.example.newsaggregator;

public class NewsSource {
    private final String id;
    private final String name;
    private final String category;

    public NewsSource(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;

    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
