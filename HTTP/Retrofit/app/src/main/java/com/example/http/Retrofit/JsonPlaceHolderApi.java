package com.example.http.Retrofit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi
{
    // We will get back a list of post which is a json array of post json object. To tell retrofit what to do, we have to annotate this method. As
    // this is a get request so we will annotate this method using GET annotation. In GET annotation we will pass the 'posts' which represents
    // relative url as base url is handled in the RetrofitActivity.java. Instead of relative URL you can also pass the whole URl int GET() annotation
    // and in this case it will replace the base URL defined while creating the instance of the Retrofit. // While defining the base URL, backslash (/)
    // is temporary means if not defined here then it have to put at the start of each relative URL (i.e instead of "posts" write "/posts").
    @GET("posts")  // This is the relative URL.
    Call<List<Post>> getPosts();    // To get all the posts.


    // ******************************************************************************************************************************************************


    // Query annotation is used for query params. Retrofit will automatically add the question mark (?) before the first query variable to indicate
    // the start of the query params (url : "https://.....?userid=2&_sort=id...). Again Retrofit will add ampersand (&) sign b/w each query
    // parameter variables.
    @GET("posts")
    Call<List<Post>> getPostPerUser(
            // @Query("userId") Integer userId1,    // We used wrapper class of int as int in non-nullable but it's wrapper class can have null value.
            // @Query("userId") Integer userId2,   // To get the post of another user.

            // To get the posts of more than one user we can use above method and write down separate Query() for each user but instead we can approach
            // an another method and pass an array of Integer.
            @Query("userId") Integer[] userId,
            @Query("_sort") String sort,
            @Query("_order") String order
    );                                      // To get the post as per the user id(s).


    // If you want to pass any combination of query parameters and don't to define them in the method declaration, use QueryMap.
    @GET("posts")
    Call<List<Post>> getPostPerUser(@QueryMap Map<String, String> parameters);


    // ******************************************************************************************************************************************************


    // @Path annotation will replace the variable string ( here it is post_id ) inside the curly braces with the one followed by it ( here by postId ). See
    // that post_id is string type but postId is int type. Retrofit will automatically convert the string to integer.
    @GET("posts/{post_id}/comments")
    Call<List<Comment>> getComments(@Path("post_id") int postId);

    // If whole query parameter string is passed is passed. Here you can the whole URL also and in this case it will replace the base URL defined while
    // creating the instance of the Retrofit.
    @GET
    Call<List<Comment>> getComments(@Url String url);


    // *****************************************************************************************************************************************************


    // Since we use GSON as our converter Post will be automatically be serialized into JSON format before it is sent to the server.
    @POST("posts")
    Call<Post> createPost(@Body Post post);

    // @FormUrlEncoded is just the another way of sending data to the server. It will encode the fields just like URL is encoded (like query parameters).
    // userId, title, body : should be used with these names only as jsonPlaceHolder api has these names as keys.
    @FormUrlEncoded
    @POST("posts")
    Call<Post> createPost(
            @Field("userId") int userId,
            @Field("title") String title,
            @Field("body") String text
    );

    // If you don't want to define the parameters in the method declaration then in that case we can use FieldMap (similar to QueryMap used above).
    @FormUrlEncoded
    @POST("posts")
    Call<Post> createPost(@FieldMap Map<String, String> fields);


    // *****************************************************************************************************************************************************


    // Put request will replace the whole post whose id is mentioned. Extra we can add header also as we did below.
    @Headers({"Static-Header: 123", "Static-Header: 456"})
    @PUT("posts/{id}")
    Call<Post> putPost(@Header("Dynamic-Header") String header, @Path("id") int id, @Body Post post);

    // Where as patch request will only update the those fields whose key-value is passed in the body (or whose value is passed but as null).
    @PATCH("posts/{id}")
    Call<Post> patchPost(@HeaderMap Map<String,String> headers, @Path("id") int id, @Body Post post);


    // *****************************************************************************************************************************************************


    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);
}