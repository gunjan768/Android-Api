package com.example.http.Retrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.http.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity
{
    private TextView textViewResult;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        textViewResult = findViewById(R.id.text_view_result);

        // Here you can customize more but we only care about the null. Now we will have null values (before, GSON can't have null value).
        Gson gson = new GsonBuilder().serializeNulls().create();

        // Will log out all the events we are performing regarding Retrofit including the request we send and response we get back.
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain ->
                {
                    Request originalRequest = chain.request();

                    // We cannot make changes to the originalRequest so we will copy it.
                    Request newRequest = originalRequest.newBuilder()
                            .header("Interceptor-Header", "Emilia-Gunjan")      // To add header(s) to all the requests made.
                            .build();

                    return chain.proceed(newRequest);
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        // While defining the base URL, backslash (/) is temporary means if not defined here then it have to put at the start of each relative URL.
        // GsonConverterFactory.create() : may have GSON object if defined with some user customizations like here it has.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create(gson))      // Define Gson how to parse the request.
                .client(okHttpClient)
                .build();

        // Now retrofit will bring life to the methods declared in the JsonPlaceHolderApi interface. Bringing life means retrofit will automatically
        // provide the implementation of methods (i.e we don't need to write the body of methods).
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        // getPosts();
        // getPostAsPerUserID();
        // getComments();
        // createPost();
        updatePost();
        // deletePost();
    }

    private void getPosts()
    {
        // Now to execute the network request, we have to use the Call object as return type of getPosts() method is Call object.
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        // call.execute() will execute the method on the main UI thread. But we don't want to do a network operation on the main thread as it will
        // completely freeze the main thread. So we will call call.enqueue() which will execute the method on the different thread ( see here we
        // didn't need to create a thread as it is done by retrofit only ).
        call.enqueue(new Callback<List<Post>>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                List<Post> posts = response.body();

                assert posts != null;
                for(Post post : posts)
                {
                    StringBuilder content = new StringBuilder();

                    content.append("ID : ").append(post.getId()).append("\n");
                    content.append("User ID : ").append(post.getUserID()).append("\n");
                    content.append("Title : ").append(post.getTitle()).append("\n");
                    content.append("Text : ").append(post.getText()).append("\n\n");

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void getPostAsPerUserID()
    {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("_order", "desc");

        // Now to execute the network request we have to use the Call object as return type of getPosts() method is Call object.
        // Call<List<Post>> call = jsonPlaceHolderApi.getPostPerUser(new Integer[]{1,3,6}, "id", "desc");

        // If you want to pass any combination of query parameters and don't want to define them in the method declaration, use QueryMap.
        // Above used method is for the else ( where each query parameters are defined in the method declaration ).
        Call<List<Post>> call = jsonPlaceHolderApi.getPostPerUser(parameters);

        // call.execute() will execute the method on the main UI thread. But we don't want to do a network operation on the main thread as it will
        // completely freeze the main thread. So we will call call.enqueue() which will execute the method on the different thread ( see here we
        // didn't need to create a thread as it was done by retrofit only ).
        call.enqueue(new Callback<List<Post>>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                List<Post> posts = response.body();

                assert posts != null;
                for(Post post : posts)
                {
                    StringBuilder content = new StringBuilder();

                    content.append("ID : ").append(post.getId()).append("\n");
                    content.append("User ID : ").append(post.getUserID()).append("\n");
                    content.append("Title : ").append(post.getTitle()).append("\n");
                    content.append("Text : ").append(post.getText()).append("\n\n");

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void getComments()
    {
        // Now to execute the network request we have to use the Call object as return type of getPosts() method is Call object.
        // Call<List<Comment>> call = jsonPlaceHolderApi.getComments(3);       // If only query parameter variables are used.

        // If whole query parameter string is passed. Here you can pass the whole URL also and in this case it will replace the base URL
        // defined while creating an instance of the Retrofit.
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments("posts/3/comments");

        // call.execute() will execute the method on the main UI thread. But we don't want to do a network operation on the main thread as it will
        // completely freeze the main thread. So we will call call.enqueue() which will execute the method on the different thread ( see here we
        // didn't need to create a thread as it was done by retrofit only ).
        call.enqueue(new Callback<List<Comment>>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                List<Comment> comments = response.body();

                assert comments != null;
                for(Comment comment : comments)
                {
                    StringBuilder content = new StringBuilder();

                    content.append("ID : ").append(comment.getId()).append("\n");
                    content.append("Post ID : ").append(comment.getPostId()).append("\n");
                    content.append("Name : ").append(comment.getName()).append("\n");
                    content.append("Email : ").append(comment.getEmail()).append("\n");
                    content.append("Text : ").append(comment.getText()).append("\n\n");

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void createPost()
    {
        // Post post = new Post(23, null, "New Title", "New Text");

        // Call<Post> call = jsonPlaceHolderApi.createPost(post);

        // To send the data after encoding it using @FormUrlEncoded annotation (see JsonPlaceHolderApi interface).
        // Call<Post> call = jsonPlaceHolderApi.createPost(23, "New Title", "New Text");

        Map<String, String> fields = new HashMap<>();

        fields.put("userId", "12");
        fields.put("title", "Hello Emilia");
        fields.put("body", "She loves Gunjan");

        Call<Post> call = jsonPlaceHolderApi.createPost(fields);

        call.enqueue(new Callback<Post>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                Post postResponse = response.body();

                assert postResponse != null;

                StringBuilder content = new StringBuilder();

                content.append("Code : ").append(response.code()).append("\n");
                content.append("ID : ").append(postResponse.getId()).append("\n");
                content.append("Post ID : ").append(postResponse.getUserID()).append("\n");
                content.append("Name : ").append(postResponse.getTitle()).append("\n");
                content.append("Email : ").append(postResponse.getText()).append("\n");
                content.append("Text : ").append(postResponse.getText()).append("\n\n");

                textViewResult.append(content);
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void updatePost()
    {
        // ##################################################### PUT REQUEST #######################################################


        // Post post = new Post(12, null, "New Title", "New Text");

        // PUT request.
        // Call<Post> call = jsonPlaceHolderApi.putPost("Any Header", 5, post);


        // ##################################################### PUT REQUEST #######################################################


        // **************************************************** PATCH REQUEST *****************************************************

        Map<String, String> headers = new HashMap<>();

        headers.put("Map-Header1", "First Header");
        headers.put("Map-Header2", "Second Header");
        headers.put("Map-Header3", "Third Header");

        // For patch request we will send the title as null hence it will not update the title so previous title will be retained.
        Post post = new Post(12, null, null, "New Text");

        // PATCH request.
        Call<Post> call = jsonPlaceHolderApi.patchPost(headers, 5, post);

        // **************************************************** PATCH REQUEST *****************************************************


        call.enqueue(new Callback<Post>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                Post postResponse = response.body();

                assert postResponse != null;

                StringBuilder content = new StringBuilder();

                content.append("ID : ").append(postResponse.getId()).append("\n");
                content.append("User ID : ").append(postResponse.getUserID()).append("\n");
                content.append("Title : ").append(postResponse.getTitle()).append("\n");
                content.append("Text : ").append(postResponse.getText()).append("\n\n");

                textViewResult.append(content);
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void deletePost()
    {
        // PATCH request.
        Call<Void> call = jsonPlaceHolderApi.deletePost(5);

        call.enqueue(new Callback<Void>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response)
            {
                if(!response.isSuccessful())
                {
                    textViewResult.setText("Code : " + response.code());

                    return;
                }

                textViewResult.setText("Given item has been deleted successfully with code : " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t)
            {
                textViewResult.setText(t.getMessage());
            }
        });
    }
}