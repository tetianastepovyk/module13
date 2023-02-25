package task1;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import task1.comments.Comments;
import task1.todos.Todos;
import task1.user.User;
import task1.posts.Posts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;

public class HTTPUsersInteraction {
    private  static final Gson gson = new Gson();
    /*Task1
     * створення нового об'єкта*/
    public static HttpResponse<String> createUser(String uri, User user) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /*оновлення об'єкту */
    public static HttpResponse<String> updateUser(String uri, int userID, User user) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/" + userID))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    }

    /*видалення об'єкта */
    public static HttpResponse<String> deleteUser(String uri, int userID) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/" + userID))
                .DELETE()
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /*отримання інформації про всіх користувачів */
    public static List<User> getUsers(String uri) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response != null) {
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            return null;
        }
    }

    /*отримання інформації про користувача за id*/
    public static User getUser(String uri, int userId) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/" + userId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response != null) {
            return gson.fromJson(response.body(), User.class);
        } else {
            return null;
        }
    }
    /*отримання інформації про користувача за username*/

    public static User getUser(URI uri, String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "?username=" + username))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("\n"+username+" httpResponse.statusCode() = " + response.statusCode());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        return gson.fromJson(jsonArray.get(0), User.class);
    }

    /*виводити всі коментарі до останнього поста певного користувача і записувати їх у файл.*/
    public static boolean saveCommentsToJSON(String uriUsers, String uriPosts, int userId) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriUsers + "/" + userId + "/posts"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        int lastPostID = 0;

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<List<Posts>>() {
        }.getType();

        List<Posts> posts = gson.fromJson(response.body(), listType);


        lastPostID = posts.stream().mapToInt(Posts::getId).max().orElseThrow(NoSuchElementException::new);

        request = HttpRequest.newBuilder()
                .uri(URI.create(uriPosts + "/" + lastPostID + "/comments"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        File theDir = new File("src/main/resources");
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        try (FileWriter fileWriter = new FileWriter("src/main/resources/user-" + userId +
                "-post-" + lastPostID + ".json")
        ) {

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            listType = new TypeToken<List<Comments>>() {
            }.getType();

            List<Comments> comments = gson.fromJson(response.body(), listType);

            fileWriter.write(gson.toJson(comments));

            fileWriter.flush();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
    /* /*виводити всі коментарі до останнього поста певного користувача і записувати їх у файл.*/
    public static List<Todos> getUserOpenTask(String uri, int userID) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "/" + userID + "/todos"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response != null) {
            Type listType = new TypeToken<List<Todos>>() {
            }.getType();

            return gson.fromJson(response.body(), listType);
        } else {
            return null;
        }

    }
}
