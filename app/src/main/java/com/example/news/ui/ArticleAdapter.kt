package com.example.news.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.data.Article

class ArticleAdapter(
    var onItemClickListener: (Int) -> Unit = {}
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private var articles: List<Article> = emptyList()

    class ViewHolder(
        root: View,
        onItemClickListener: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(root) {
        val title: TextView by lazy { root.findViewById(R.id.title) }
        val author: TextView by lazy { root.findViewById(R.id.author) }
        val source: TextView by lazy { root.findViewById(R.id.source) }

        init {
            root.setOnClickListener {
                onItemClickListener.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false),
            onItemClickListener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.apply {
            title.text = article.title
            author.text = article.author
            source.text = article.source.name
        }
    }

    override fun getItemCount() = articles.size

    fun setArticles(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }
}
