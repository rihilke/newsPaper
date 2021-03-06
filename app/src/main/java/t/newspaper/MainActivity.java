package t.newspaper;

import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import t.newspaper.api.ApiClient;
import t.newspaper.api.ApiInterface;
import t.newspaper.models.Article;
import t.newspaper.models.News;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = "057bb4fabd1748c99d7b8e5ebfa6b74c";
    private List<Article> articles = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Button btnRetry;
    private RelativeLayout errorLayout;
    private TextView errorTitle, errorMessage;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        LoadJson("Java");

        errorLayout = findViewById(R.id.errorLayout);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);
        searchView = (SearchView) findViewById(R.id.search);

        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0){
                    LoadJson(query);
                }
                else {
                    LoadJson("Java");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void LoadJson(final String keyword){
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        /*
        String country = Utils.getCountry();
        String language = Utils.getLanguage();
        */

        String country = "ru";
        String language = "ru";

        Call<News> call;

        if (keyword.length() > 0 ){
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
//            System.out.println("published");
//            System.out.println(country + " " + language);
        } else {
            call = apiInterface.getNews(country, API_KEY);
//            System.out.println(country);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticle();
//                    System.out.println(articles.toString());
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {

                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }

                    showErrorMessage(
                            "Please Try Again!\n",
                                    errorCode);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                showErrorMessage(
                        "Network failure, Please Try Again\n",
                                t.toString());
            }
        });
    }

    /*
    private void initListener(){

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(String.valueOf(MainActivity.this));

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img",  article.getUrlToImage());
                intent.putExtra("date",  article.getPublishedAt());
                intent.putExtra("source",  article.getSource().getName());
                intent.putExtra("author",  article.getAuthor());

                startActivity(intent);
            }
        });
    }
*/

    private void showErrorMessage( String title, String message){

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

}