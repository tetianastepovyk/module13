package task1;

import com.google.gson.Gson;
import task1.todos.Todos;
import task1.user.User;

import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URI;
import java.util.stream.Collectors;

public class HttpUrlTest {
    public static final Gson gson = new Gson();
    public static final String URI_USERS = "https://jsonplaceholder.typicode.com/users";
    public static final String URI_POSTS = "https://jsonplaceholder.typicode.com/posts";

    public static void main(String[] args) throws IOException, InterruptedException{

        /*Task1
        * створення нового об'єкта*/
        {
            User user = gson.fromJson(new FileReader("src/main/resources/user.json"), User.class);

            HttpResponse<String> response = HTTPUsersInteraction.createUser(URI_USERS, user);
            System.out.println("createUser httpResponse.statusCode() = " + response.statusCode());
            System.out.println("createUser httpResponse.body() = " + response.body());
        }
        /*Task1
         * оновлення об'єкту */
        {
            User modUser = gson.fromJson(new FileReader("src/main/resources/user.json"), User.class);

            modUser.setName("\nUpdate name");
            modUser.setUsername("Update username");
            modUser.setEmail("Update email");

            int userId = 3;

            HttpResponse<String> response = HTTPUsersInteraction.updateUser(URI_USERS, userId, modUser);
            System.out.println("\nupdateUser id: "+userId+" httpResponse.statusCode() = " + response.statusCode());
            System.out.println("updateUser id: "+userId+" httpResponse.body() = " + response.body());
        }

        /*Task1
         * видалення об'єкта */
        {
            int userId = 4;

            HttpResponse<String> response = HTTPUsersInteraction.deleteUser(URI_USERS, userId);
            System.out.println("\ndeleteUser id: "+userId+" httpResponse.statusCode() = " + response.statusCode());
            System.out.println("deleteUser id: "+userId+" httpResponse.body() = " + response.body()+"\n");
        }

        /*отримання інформації про всіх користувачів */
        {
            List<User> users = HTTPUsersInteraction.getUsers(URI_USERS);
            for (User user : users) {
                System.out.println(user);
            }
        }

        /*отримання інформації про користувача за id*/
        {
            int userId = 5;

            User user = HTTPUsersInteraction.getUser(URI_USERS, userId);
            System.out.println("\nCheck user from method getUser by userID = " + userId + ":\n" + user);
        }

        /*отримання інформації про користувача за username*/
        {
            String userName = "Elwyn.Skiles";
            User userByUsername = HTTPUsersInteraction.getUser(URI.create(URI_USERS), userName);
            System.out.println("User by "+userName+": " + userByUsername);
        }

        /*виводити всі коментарі до останнього поста певного користувача і записувати їх у файл.*/
        {
            int userID = 6;
            System.out.println("\n"+HTTPUsersInteraction.saveCommentsToJSON(URI_USERS, URI_POSTS, userID));
        }

        {
            int userID = 5;

            List<Todos> todos = HTTPUsersInteraction.getUserOpenTask(URI_USERS, userID);

            System.out.printf("\nвсі відкриті задачі для користувача з ідентифікатором "+ userID+":\n");

            todos.stream()
                    .filter(todo -> !todo.isCompleted()).collect(Collectors.toList())
                    .forEach(System.out::println);
        }
    }
}
